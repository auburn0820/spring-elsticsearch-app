package org.suyeong.springelasticsearchapp.service

import mu.KLogging
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.StringQuery
import org.springframework.stereotype.Service
import org.suyeong.springelasticsearchapp.domain.Product
import org.suyeong.springelasticsearchapp.repository.ProductRepository

@Service
class ProductSearchService(
  private val productRepository: ProductRepository,
  private val elasticsearchOperations: ElasticsearchOperations
) {

  companion object : KLogging()

  fun saveProduct(product: Product): Product {
    logger.info { "Saving product: ${product.name}" }
    return productRepository.save(product)
  }

  fun saveProducts(products: List<Product>): List<Product> {
    logger.info { "Saving ${products.size} products" }
    return productRepository.saveAll(products).toList()
  }

  fun findById(id: String): Product? {
    return productRepository.findById(id).orElse(null)
  }

  fun findAll(pageable: Pageable): Page<Product> {
    return productRepository.findAll(pageable)
  }

  fun deleteById(id: String) {
    logger.info { "Deleting product with id: $id" }
    productRepository.deleteById(id)
  }

  fun searchByKeyword(keyword: String, pageable: Pageable): Page<Product> {
    logger.info { "Searching products by keyword: $keyword" }
    return productRepository.searchByKeyword(keyword, pageable)
  }

  fun searchByCategory(category: String, pageable: Pageable): Page<Product> {
    logger.info { "Searching products by category: $category" }
    return productRepository.findByCategory(category, pageable)
  }

  fun searchByPriceRange(minPrice: Double, maxPrice: Double, pageable: Pageable): Page<Product> {
    logger.info { "Searching products by price range: $minPrice - $maxPrice" }
    return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
  }

  fun advancedSearch(
    query: String,
    minPrice: Double,
    maxPrice: Double,
    pageable: Pageable
  ): Page<Product> {
    logger.info { "Advanced search: query=$query, price=$minPrice-$maxPrice" }
    return productRepository.advancedSearch(query, minPrice, maxPrice, pageable)
  }

  fun fuzzySearch(searchTerm: String): SearchHits<Product> {
    logger.info { "Fuzzy search for: $searchTerm" }

    val queryString = """
            {
                "multi_match": {
                    "query": "$searchTerm",
                    "fields": ["name", "description", "category"],
                    "fuzziness": "AUTO"
                }
            }
        """.trimIndent()

    val searchQuery = StringQuery(queryString)
    return elasticsearchOperations.search(searchQuery, Product::class.java)
  }

  fun partialSearch(searchTerm: String): SearchHits<Product> {
    logger.info { "Partial search for: $searchTerm" }

    val queryString = """
            {
                "bool": {
                    "should": [
                        {
                            "wildcard": {
                                "name": {
                                    "value": "*$searchTerm*",
                                    "case_insensitive": true
                                }
                            }
                        },
                        {
                            "wildcard": {
                                "description": {
                                    "value": "*$searchTerm*",
                                    "case_insensitive": true
                                }
                            }
                        },
                        {
                            "match_phrase_prefix": {
                                "name": "$searchTerm"
                            }
                        },
                        {
                            "match_phrase_prefix": {
                                "description": "$searchTerm"
                            }
                        }
                    ]
                }
            }
        """.trimIndent()

    val searchQuery = StringQuery(queryString)
    return elasticsearchOperations.search(searchQuery, Product::class.java)
  }

  fun aggregateByCategory(): Map<String, Long> {
    logger.info { "Aggregating products by category" }

    // 간단한 구현: 모든 제품을 가져와서 카테고리별로 그룹화
    val allProducts = productRepository.findAll()
    return allProducts.groupBy { it.category }
      .mapValues { it.value.size.toLong() }
  }

  fun suggestProducts(prefix: String): List<String> {
    logger.info { "Suggesting products for prefix: $prefix" }

    val queryString = """
            {
                "prefix": {
                    "name": "${prefix.lowercase()}"
                }
            }
        """.trimIndent()

    val searchQuery = StringQuery(queryString, Pageable.ofSize(10))
    val searchHits = elasticsearchOperations.search(searchQuery, Product::class.java)

    return searchHits.map { it.content.name }
      .distinct()
  }

  // ==================== Nori 한글 형태소 분석 검색 메서드들 ====================

  /**
   * Nori 형태소 분석을 사용한 검색
   *
   * 예시:
   * - "냉장고" → ["냉장고"] 또는 ["냉장", "고"]
   * - "운동화" → ["운동", "화"] 또는 ["운동화"]
   * - "갤럭시북" → ["갤럭시", "북"] 또는 ["갤럭시북"] (사용자 사전에 따라)
   */
  fun searchByNoriAnalyzer(keyword: String, pageable: Pageable): Page<Product> {
    logger.info { "Nori analyzer search for: $keyword" }
    return productRepository.searchByNoriAnalyzer(keyword, pageable)
  }

  /**
   * 부분 문자열 매칭 (Edge N-gram)
   *
   * 예시:
   * - "냉장" 검색 → "냉장고" 매칭
   * - "갤럭" 검색 → "갤럭시북" 매칭
   */
  fun searchByNoriPartialMatch(keyword: String, pageable: Pageable): Page<Product> {
    logger.info { "Nori partial match search for: $keyword" }
    return productRepository.searchByNoriPartialMatch(keyword, pageable)
  }

  /**
   * 혼합 검색: 정확한 매칭 + 형태소 분석 + 부분 매칭
   *
   * 점수 기반 우선순위:
   * 1. 정확한 매칭 (boost: 3.0)
   * 2. 형태소 분석 매칭 (boost: 2.0)
   * 3. 부분 문자열 매칭 (boost: 1.0)
   */
  fun searchByNoriMixedAnalysis(keyword: String, pageable: Pageable): Page<Product> {
    logger.info { "Nori mixed analysis search for: $keyword" }
    return productRepository.searchByNoriMixedAnalysis(keyword, pageable)
  }

  /**
   * 고급 Nori 검색
   * 복합명사 분해와 품사 필터링을 활용
   */
  fun advancedNoriSearch(keyword: String): SearchHits<Product> {
    logger.info { "Advanced Nori search for: $keyword" }

    val queryString = """
            {
                "bool": {
                    "should": [
                        {
                            "match": {
                                "name": {
                                    "query": "$keyword",
                                    "analyzer": "nori_standard",
                                    "boost": 2.0
                                }
                            }
                        },
                        {
                            "match": {
                                "description": {
                                    "query": "$keyword",
                                    "analyzer": "nori_standard",
                                    "boost": 1.5
                                }
                            }
                        },
                        {
                            "match_phrase_prefix": {
                                "name": {
                                    "query": "$keyword",
                                    "boost": 1.0
                                }
                            }
                        }
                    ]
                }
            }
        """.trimIndent()

    val searchQuery = StringQuery(queryString)
    return elasticsearchOperations.search(searchQuery, Product::class.java)
  }

  /**
   * 한글 자동완성을 위한 제안 검색
   */
  fun getNoriSuggestions(prefix: String): List<String> {
    logger.info { "Getting Nori suggestions for prefix: $prefix" }

    val queryString = """
            {
                "match": {
                    "name.ngram": "$prefix"
                }
            }
        """.trimIndent()

    val searchQuery = StringQuery(queryString, Pageable.ofSize(10))
    val searchHits = elasticsearchOperations.search(searchQuery, Product::class.java)

    return searchHits.map { it.content.name }
      .distinct()
      .take(10)
  }

  /**
   * 분석기 테스트용 메서드
   */
  fun analyzeText(text: String, analyzer: String): Map<String, Any> {
    logger.info { "Analyzing text '$text' with analyzer '$analyzer'" }

    // Elasticsearch의 _analyze API를 호출하여 토큰 분석 결과를 가져올 수 있습니다
    // 여기서는 간단한 예시로 구현
    return mapOf(
      "text" to text,
      "analyzer" to analyzer,
      "note" to "실제 토큰 분석 결과를 보려면 Elasticsearch _analyze API를 사용하세요",
      "example_tokens" to when (analyzer) {
        "nori_standard" -> "한글 형태소 분석: '$text' → 형태소 단위로 분해"
        "standard" -> "표준 분석: '$text' → 공백과 특수문자로 분리"
        "edge_ngram" -> "부분 문자열: '$text' → 앞에서부터 N-gram 생성"
        else -> "분석기 '$analyzer'로 '$text' 분석"
      }
    )
  }
}