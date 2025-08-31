package org.suyeong.springelasticsearchapp.controller

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.suyeong.springelasticsearchapp.domain.Product
import org.suyeong.springelasticsearchapp.service.ProductSearchService

@RestController
@RequestMapping("/api/products")
class ProductController(
  private val productSearchService: ProductSearchService
) {

  companion object : KLogging()

  @PostMapping
  fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
    logger.info { "Creating product: ${product.name}" }
    val savedProduct = productSearchService.saveProduct(product)
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct)
  }

  @PostMapping("/bulk")
  fun createProducts(@RequestBody products: List<Product>): ResponseEntity<List<Product>> {
    logger.info { "Creating ${products.size} products in bulk" }
    val savedProducts = productSearchService.saveProducts(products)
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts)
  }

  @GetMapping("/{id}")
  fun getProduct(@PathVariable id: String): ResponseEntity<Product> {
    logger.info { "Getting product with id: $id" }
    return productSearchService.findById(id)?.let {
      ResponseEntity.ok(it)
    } ?: ResponseEntity.notFound().build()
  }

  @GetMapping
  fun getAllProducts(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
    @RequestParam(defaultValue = "name") sortBy: String,
    @RequestParam(defaultValue = "ASC") sortDirection: String
  ): ResponseEntity<Page<Product>> {
    logger.info { "Getting all products - page: $page, size: $size" }

    val sort = if (sortDirection.uppercase() == "DESC") {
      Sort.by(sortBy).descending()
    } else {
      Sort.by(sortBy).ascending()
    }

    val pageable = PageRequest.of(page, size, sort)
    val products = productSearchService.findAll(pageable)
    return ResponseEntity.ok(products)
  }

  @DeleteMapping("/{id}")
  fun deleteProduct(@PathVariable id: String): ResponseEntity<Void> {
    logger.info { "Deleting product with id: $id" }
    productSearchService.deleteById(id)
    return ResponseEntity.noContent().build()
  }

  @GetMapping("/search")
  fun searchProducts(
    @RequestParam query: String,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Searching products with query: $query" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByKeyword(query, pageable)
    return ResponseEntity.ok(results)
  }

  @GetMapping("/search/category")
  fun searchByCategory(
    @RequestParam category: String,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Searching products by category: $category" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByCategory(category, pageable)
    return ResponseEntity.ok(results)
  }

  @GetMapping("/search/price")
  fun searchByPriceRange(
    @RequestParam minPrice: Double,
    @RequestParam maxPrice: Double,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Searching products by price range: $minPrice - $maxPrice" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByPriceRange(minPrice, maxPrice, pageable)
    return ResponseEntity.ok(results)
  }

  @GetMapping("/search/advanced")
  fun advancedSearch(
    @RequestParam query: String,
    @RequestParam(defaultValue = "0") minPrice: Double,
    @RequestParam(defaultValue = "999999") maxPrice: Double,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Advanced search - query: $query, price: $minPrice-$maxPrice" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.advancedSearch(query, minPrice, maxPrice, pageable)
    return ResponseEntity.ok(results)
  }

  @GetMapping("/search/fuzzy")
  fun fuzzySearch(@RequestParam term: String): ResponseEntity<List<Product>> {
    logger.info { "Fuzzy search for term: $term" }
    val results = productSearchService.fuzzySearch(term)
    val products = results.searchHits.map { it.content }
    return ResponseEntity.ok(products)
  }

  @GetMapping("/search/partial")
  fun partialSearch(@RequestParam term: String): ResponseEntity<List<Product>> {
    logger.info { "Partial search for term: $term" }
    val results = productSearchService.partialSearch(term)
    val products = results.searchHits.map { it.content }
    return ResponseEntity.ok(products)
  }

  @GetMapping("/suggest")
  fun suggestProducts(@RequestParam prefix: String): ResponseEntity<List<String>> {
    logger.info { "Getting suggestions for prefix: $prefix" }
    val suggestions = productSearchService.suggestProducts(prefix)
    return ResponseEntity.ok(suggestions)
  }

  @GetMapping("/stats/categories")
  fun getCategoryStats(): ResponseEntity<Map<String, Long>> {
    logger.info { "Getting category statistics" }
    val stats = productSearchService.aggregateByCategory()
    return ResponseEntity.ok(stats)
  }

  // ==================== Nori 한글 형태소 분석 검색 APIs ====================

  /**
   * Nori 형태소 분석기를 사용한 검색
   *
   * 예시: GET /api/products/search/nori?query=냉장
   * 결과: "삼성 비스포크 냉장고"가 검색됨
   */
  @GetMapping("/search/nori")
  fun searchByNoriAnalyzer(
    @RequestParam query: String,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Nori analyzer search with query: $query" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByNoriAnalyzer(query, pageable)
    return ResponseEntity.ok(results)
  }

  /**
   * 부분 문자열 매칭 검색 (Edge N-gram) - Nori 버전
   *
   * 예시: GET /api/products/search/nori-partial?query=갤럭
   * 결과: "삼성 갤럭시 S24 Ultra"가 검색됨
   */
  @GetMapping("/search/nori-partial")
  fun searchByNoriPartialMatch(
    @RequestParam query: String,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Nori partial match search with query: $query" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByNoriPartialMatch(query, pageable)
    return ResponseEntity.ok(results)
  }

  /**
   * 혼합 분석 검색 (정확한 매칭 + 형태소 분석 + 부분 매칭)
   *
   * 가장 포괄적인 한글 검색 방식 - 추천!
   */
  @GetMapping("/search/nori-mixed")
  fun searchByNoriMixedAnalysis(
    @RequestParam query: String,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int
  ): ResponseEntity<Page<Product>> {
    logger.info { "Nori mixed analysis search with query: $query" }
    val pageable = PageRequest.of(page, size)
    val results = productSearchService.searchByNoriMixedAnalysis(query, pageable)
    return ResponseEntity.ok(results)
  }

  /**
   * 고급 Nori 검색
   */
  @GetMapping("/search/nori-advanced")
  fun advancedNoriSearch(@RequestParam query: String): ResponseEntity<List<Product>> {
    logger.info { "Advanced Nori search with query: $query" }
    val results = productSearchService.advancedNoriSearch(query)
    val products = results.searchHits.map { it.content }
    return ResponseEntity.ok(products)
  }

  /**
   * 한글 자동완성 제안
   *
   * 예시: GET /api/products/suggest/nori?prefix=냉
   * 결과: ["냉장고", "냉동고", ...] 등 냉으로 시작하는 제품명 반환
   */
  @GetMapping("/suggest/nori")
  fun getNoriSuggestions(@RequestParam prefix: String): ResponseEntity<List<String>> {
    logger.info { "Getting Korean suggestions for prefix: $prefix" }
    val suggestions = productSearchService.getNoriSuggestions(prefix)
    return ResponseEntity.ok(suggestions)
  }

  /**
   * 분석기 테스트용 API
   *
   * 예시: GET /api/products/analyze?text=삼성냉장고&analyzer=nori_standard
   */
  @GetMapping("/analyze")
  fun analyzeText(
    @RequestParam text: String,
    @RequestParam(defaultValue = "nori_standard") analyzer: String
  ): ResponseEntity<Map<String, Any>> {
    logger.info { "Analyzing text '$text' with analyzer '$analyzer'" }
    val result = productSearchService.analyzeText(text, analyzer)
    return ResponseEntity.ok(result)
  }
}