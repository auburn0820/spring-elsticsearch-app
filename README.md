# Spring Boot Elasticsearch Application

Kotlin 기반 Spring Boot와 Elasticsearch를 활용한 **한글 최적화 검색 애플리케이션**입니다.

## 주요 기능

✨ **Nori 한글 형태소 분석기** - 한글 검색 최적화  
🔍 **다양한 검색 방식** - 정확한 검색부터 퍼지 검색까지  
⚡ **실시간 자동완성** - 빠른 검색 제안  
📊 **통계 및 집계** - 카테고리별 분석

## 시작하기

### 1. Elasticsearch 실행

```bash
docker-compose up -d
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

## API 엔드포인트

### 제품 관리

- `POST /api/products` - 제품 생성
- `POST /api/products/bulk` - 대량 제품 생성
- `GET /api/products/{id}` - 제품 조회
- `GET /api/products` - 전체 제품 목록
- `DELETE /api/products/{id}` - 제품 삭제

### 검색 기능

#### 기본 검색 (Standard Analyzer)
- `GET /api/products/search?query={keyword}` - 키워드 검색
- `GET /api/products/search/category?category={category}` - 카테고리별 검색  
- `GET /api/products/search/price?minPrice={min}&maxPrice={max}` - 가격 범위 검색
- `GET /api/products/search/advanced?query={query}&minPrice={min}&maxPrice={max}` - 고급 검색
- `GET /api/products/search/fuzzy?term={term}` - 퍼지 검색
- `GET /api/products/search/partial?term={term}` - 부분 문자열 검색

#### 한글 최적화 검색 (Nori Analyzer) 🔥
- `GET /api/products/search/nori?query={keyword}` - Nori 형태소 분석 검색
- `GET /api/products/search/nori-partial?query={keyword}` - Nori 부분 매칭 검색
- `GET /api/products/search/nori-mixed?query={keyword}` - **🎯 추천! 혼합 분석 검색**
- `GET /api/products/search/nori-advanced?query={keyword}` - 고급 Nori 검색

#### 기타 기능
- `GET /api/products/suggest?prefix={prefix}` - 자동완성 제안
- `GET /api/products/suggest/nori?prefix={prefix}` - 한글 자동완성 제안
- `GET /api/products/stats/categories` - 카테고리별 통계
- `GET /api/products/analyze?text={text}&analyzer={analyzer}` - 분석기 테스트

## 예제 요청

### 제품 생성

```json
{
  "name": "노트북",
  "description": "고성능 게이밍 노트북",
  "category": "전자제품",
  "price": 1500000,
  "stock": 10,
  "brand": "삼성",
  "tags": [
    "게이밍",
    "노트북",
    "고성능"
  ]
}
```

## Elasticsearch 접속

- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601