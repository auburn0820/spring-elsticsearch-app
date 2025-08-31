package org.suyeong.springelasticsearchapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.time.Duration

@Configuration
@EnableElasticsearchRepositories(basePackages = ["org.suyeong.springelasticsearchapp.repository"])
class ElasticsearchConfig : ElasticsearchConfiguration() {

  override fun clientConfiguration(): ClientConfiguration {
    return ClientConfiguration.builder()
      .connectedTo("localhost:9200")
      .withConnectTimeout(Duration.ofSeconds(5))
      .withSocketTimeout(Duration.ofSeconds(30))
      .build()
  }
}