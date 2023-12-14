package com.study.kstream.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.DefaultKafkaHeaderMapper
import org.springframework.kafka.support.converter.MessagingMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper


@Configuration
class KafkaConfig {



    //@Bean
    fun kafkaHeaderMapper(): DefaultKafkaHeaderMapper {
        //val mapper = DefaultKafkaHeaderMapper()
        //mapper.addTrustedPackages("*")
        return KafkaHeaderMapperNoSpringHeader()
    }

    //@Bean
    fun converter(): MessagingMessageConverter {
        val converter = MessagingMessageConverter()
        val mapper = DefaultKafkaHeaderMapper()
        mapper.addTrustedPackages("*")
        mapper.setEncodeStrings(true)
        converter.setHeaderMapper(mapper)
        return converter
    }
}
