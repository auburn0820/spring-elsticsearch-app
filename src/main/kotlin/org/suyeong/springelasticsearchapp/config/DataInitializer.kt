package org.suyeong.springelasticsearchapp.config

import mu.KLogging
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.suyeong.springelasticsearchapp.domain.Product
import org.suyeong.springelasticsearchapp.repository.ProductRepository
import java.time.LocalDateTime

@Configuration
class DataInitializer {

  companion object : KLogging()

  @Bean
  @Profile("!test")
  fun initializeData(productRepository: ProductRepository): CommandLineRunner {
    return CommandLineRunner { _ ->
      logger.info { "Initializing sample data..." }

      // 기존 데이터 확인
      val existingCount = productRepository.count()
      if (existingCount > 0) {
        logger.info { "Data already exists. Skipping initialization. (Found $existingCount products)" }
        return@CommandLineRunner
      }

      val sampleProducts = listOf(
        // 전자제품 카테고리
        Product(
          name = "MacBook Pro 16인치",
          description = "Apple M3 Pro 칩, 18GB 통합 메모리, 512GB SSD 저장 장치를 탑재한 고성능 노트북",
          category = "전자제품",
          price = 3990000.0,
          stock = 15,
          brand = "Apple",
          tags = listOf("노트북", "맥북", "프로", "애플", "고성능"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "삼성 갤럭시 S24 Ultra",
          description = "200MP 카메라와 S펜을 탑재한 플래그십 스마트폰",
          category = "전자제품",
          price = 1698000.0,
          stock = 25,
          brand = "Samsung",
          tags = listOf("스마트폰", "갤럭시", "안드로이드", "5G"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "LG 올레드 TV 65인치",
          description = "4K UHD 해상도와 돌비 비전을 지원하는 OLED TV",
          category = "전자제품",
          price = 2890000.0,
          stock = 8,
          brand = "LG",
          tags = listOf("TV", "올레드", "4K", "스마트TV"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "소니 WH-1000XM5",
          description = "업계 최고 수준의 노이즈 캔슬링 무선 헤드폰",
          category = "전자제품",
          price = 449000.0,
          stock = 30,
          brand = "Sony",
          tags = listOf("헤드폰", "무선", "노이즈캔슬링", "블루투스"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "아이패드 프로 12.9",
          description = "M2 칩과 리퀴드 레티나 XDR 디스플레이를 탑재한 태블릿",
          category = "전자제품",
          price = 1729000.0,
          stock = 12,
          brand = "Apple",
          tags = listOf("태블릿", "아이패드", "애플", "M2"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 의류 카테고리
        Product(
          name = "나이키 에어맥스 270",
          description = "편안한 쿠셔닝과 스타일리시한 디자인의 운동화",
          category = "의류",
          price = 179000.0,
          stock = 50,
          brand = "Nike",
          tags = listOf("운동화", "스니커즈", "에어맥스", "신발"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "아디다스 울트라부스트 22",
          description = "최상의 쿠셔닝과 에너지 리턴을 제공하는 러닝화",
          category = "의류",
          price = 239000.0,
          stock = 35,
          brand = "Adidas",
          tags = listOf("운동화", "러닝화", "부스트", "신발"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "유니클로 히트텍 울트라웜",
          description = "발열 기능이 뛰어난 겨울 이너웨어",
          category = "의류",
          price = 29900.0,
          stock = 100,
          brand = "Uniqlo",
          tags = listOf("이너웨어", "발열", "겨울", "히트텍"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 가전제품 카테고리
        Product(
          name = "다이슨 V15 무선청소기",
          description = "레이저 먼지 감지 기능을 탑재한 무선 청소기",
          category = "가전제품",
          price = 999000.0,
          stock = 20,
          brand = "Dyson",
          tags = listOf("청소기", "무선", "다이슨", "가전"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "삼성 비스포크 냉장고",
          description = "맞춤형 디자인과 스마트 기능을 갖춘 4도어 냉장고",
          category = "가전제품",
          price = 3290000.0,
          stock = 5,
          brand = "Samsung",
          tags = listOf("냉장고", "비스포크", "주방가전", "4도어"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "LG 트롬 세탁기",
          description = "AI DD 기술로 섬유를 보호하는 드럼 세탁기",
          category = "가전제품",
          price = 1890000.0,
          stock = 10,
          brand = "LG",
          tags = listOf("세탁기", "드럼", "트롬", "AI"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 도서 카테고리
        Product(
          name = "클린 코드",
          description = "로버트 마틴의 애자일 소프트웨어 장인 정신",
          category = "도서",
          price = 33000.0,
          stock = 40,
          brand = "인사이트",
          tags = listOf("프로그래밍", "소프트웨어", "개발", "책"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "이펙티브 자바 3판",
          description = "자바 프로그래밍 언어 가이드",
          category = "도서",
          price = 36000.0,
          stock = 30,
          brand = "인사이트",
          tags = listOf("자바", "프로그래밍", "개발", "책"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "코틀린 인 액션",
          description = "코틀린 언어의 핵심을 다룬 실무 지침서",
          category = "도서",
          price = 32000.0,
          stock = 25,
          brand = "에이콘",
          tags = listOf("코틀린", "프로그래밍", "안드로이드", "책"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 식품 카테고리
        Product(
          name = "스타벅스 하우스 블렌드 원두",
          description = "균형 잡힌 맛과 향의 미디엄 로스트 커피 원두",
          category = "식품",
          price = 18000.0,
          stock = 60,
          brand = "Starbucks",
          tags = listOf("커피", "원두", "음료", "스타벅스"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "허쉬 초콜릿 바",
          description = "클래식한 맛의 밀크 초콜릿",
          category = "식품",
          price = 3500.0,
          stock = 150,
          brand = "Hershey's",
          tags = listOf("초콜릿", "과자", "디저트", "간식"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 가구 카테고리
        Product(
          name = "이케아 말름 침대 프레임",
          description = "심플한 디자인의 퀸사이즈 침대 프레임",
          category = "가구",
          price = 299000.0,
          stock = 15,
          brand = "IKEA",
          tags = listOf("침대", "침실가구", "이케아", "프레임"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "한샘 책상 1200",
          description = "넓은 작업 공간을 제공하는 사무용 책상",
          category = "가구",
          price = 189000.0,
          stock = 20,
          brand = "한샘",
          tags = listOf("책상", "사무가구", "데스크", "한샘"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),

        // 스포츠 카테고리
        Product(
          name = "윌슨 테니스 라켓",
          description = "초보자와 중급자를 위한 올라운드 테니스 라켓",
          category = "스포츠",
          price = 159000.0,
          stock = 25,
          brand = "Wilson",
          tags = listOf("테니스", "라켓", "운동", "스포츠용품"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        ),
        Product(
          name = "나이키 축구공 프리미어리그",
          description = "프리미어리그 공식 경기구",
          category = "스포츠",
          price = 45000.0,
          stock = 40,
          brand = "Nike",
          tags = listOf("축구", "축구공", "운동", "나이키"),
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now()
        )
      )

      try {
        val savedProducts = productRepository.saveAll(sampleProducts)
        logger.info { "Successfully initialized ${savedProducts.count()} sample products" }

        // 카테고리별 개수 출력
        val categoryCounts = savedProducts.groupBy { it.category }
          .mapValues { it.value.size }

        logger.info { "Product distribution by category:" }
        categoryCounts.forEach { (category, count) ->
          logger.info { "  - $category: $count products" }
        }

      } catch (e: Exception) {
        logger.error(e) { "Failed to initialize sample data" }
      }
    }
  }
}