package com.study.kstream.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.DefaultKafkaHeaderMapper
@Configuration
class KafkaConfig {
    @Bean
    fun kafkaHeaderMapper(): DefaultKafkaHeaderMapper {
        //val mapper = DefaultKafkaHeaderMapper()
        //mapper.addTrustedPackages("*")
        return KafkaHeaderMapperNoSpringHeader()
    }
}
