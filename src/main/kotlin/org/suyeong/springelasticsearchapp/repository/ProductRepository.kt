package org.suyeong.springelasticsearchapp.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import org.suyeong.springelasticsearchapp.domain.Product

@Repository
interface ProductRepository : ElasticsearchRepository<Product, String> {

  fun findByName(name: String, pageable: Pageable): Page<Product>

  fun findByCategory(category: String, pageable: Pageable): Page<Product>

  fun findByBrand(brand: String, pageable: Pageable): Page<Product>

  fun findByPriceBetween(minPrice: Double, maxPrice: Double, pageable: Pageable): Page<Product>

  fun findByTagsContaining(tag: String, pageable: Pageable): Page<Product>

  @Query(
    """
        {
            "bool": {
                "should": [
                    { "match": { "name.standard": "?0" } },
                    { "match": { "description.standard": "?0" } },
                    { "match": { "category": "?0" } },
                    { "wildcard": { "name": "*?0*" } },
                    { "wildcard": { "description": "*?0*" } }
                ]
            }
        }
    """
  )
  fun searchByKeyword(keyword: String, pageable: Pageable): Page<Product>

  @Query(
    """
        {
            "bool": {
                "must": [
                    {
                        "multi_match": {
                            "query": "?0",
                            "fields": ["name^3", "description^2", "category", "brand"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ],
                "filter": [
                    { "range": { "price": { "gte": ?1, "lte": ?2 } } }
                ]
            }
        }
    """
  )
  fun advancedSearch(query: String, minPrice: Double, maxPrice: Double, pageable: Pageable): Page<Product>

  // ==================== Nori 한글 형태소 분석 검색 ====================

  /**
   * Nori 형태소 분석을 활용한 검색
   * 예: "냉장고" 검색 시 "냉장"으로도 매칭 가능
   */
  @Query(
    """
        {
            "bool": {
                "should": [
                    {
                        "match": {
                            "name": {
                                "query": "?0",
                                "analyzer": "nori_standard"
                            }
                        }
                    },
                    {
                        "match": {
                            "description": {
                                "query": "?0",
                                "analyzer": "nori_standard"
                            }
                        }
                    }
                ]
            }
        }
    """
  )
  fun searchByNoriAnalyzer(keyword: String, pageable: Pageable): Page<Product>

  /**
   * 부분 문자열 검색 (Edge N-gram) - Nori 버전
   * 예: "냉장" → "냉장고" 매칭
   */
  @Query(
    """
        {
            "bool": {
                "should": [
                    {
                        "match": {
                            "name.ngram": "?0"
                        }
                    },
                    {
                        "match": {
                            "name": {
                                "query": "?0",
                                "analyzer": "nori_standard"
                            }
                        }
                    }
                ]
            }
        }
    """
  )
  fun searchByNoriPartialMatch(keyword: String, pageable: Pageable): Page<Product>

  /**
   * 혼합 검색: 정확한 매칭 + 형태소 분석 + 부분 매칭
   */
  @Query(
    """
        {
            "bool": {
                "should": [
                    {
                        "match": {
                            "name.keyword": {
                                "query": "?0",
                                "boost": 3.0
                            }
                        }
                    },
                    {
                        "match": {
                            "name": {
                                "query": "?0",
                                "analyzer": "nori_standard",
                                "boost": 2.0
                            }
                        }
                    },
                    {
                        "match": {
                            "name.ngram": {
                                "query": "?0",
                                "boost": 1.0
                            }
                        }
                    }
                ]
            }
        }
    """
  )
  fun searchByNoriMixedAnalysis(keyword: String, pageable: Pageable): Page<Product>
}