package org.suyeong.springelasticsearchapp.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.*
import java.time.LocalDateTime

@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/settings/nori-settings.json")
data class Product(
  @Id
  val id: String? = null,

  @MultiField(
    mainField = Field(type = FieldType.Text, analyzer = "nori_standard", searchAnalyzer = "nori_search"),
    otherFields = [
      InnerField(suffix = "standard", type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard"),
      InnerField(suffix = "keyword", type = FieldType.Keyword),
      InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "edge_ngram", searchAnalyzer = "standard")
    ]
  )
  val name: String,

  @MultiField(
    mainField = Field(type = FieldType.Text, analyzer = "nori_standard", searchAnalyzer = "nori_search"),
    otherFields = [
      InnerField(suffix = "standard", type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    ]
  )
  val description: String,

  @Field(type = FieldType.Keyword)
  val category: String,

  @Field(type = FieldType.Double)
  val price: Double,

  @Field(type = FieldType.Integer)
  val stock: Int,

  @Field(type = FieldType.Keyword)
  val brand: String? = null,

  @Field(type = FieldType.Keyword)
  val tags: List<String> = emptyList(),

  @Field(
    type = FieldType.Date,
    format = [DateFormat.date_hour_minute_second_millis, DateFormat.date_hour_minute_second, DateFormat.date]
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Field(
    type = FieldType.Date,
    format = [DateFormat.date_hour_minute_second_millis, DateFormat.date_hour_minute_second, DateFormat.date]
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  val updatedAt: LocalDateTime = LocalDateTime.now()
)