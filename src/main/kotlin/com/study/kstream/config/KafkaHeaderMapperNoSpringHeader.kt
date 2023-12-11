package com.study.kstream.config

import org.apache.kafka.common.header.Headers
import org.springframework.kafka.support.DefaultKafkaHeaderMapper
import org.springframework.messaging.MessageHeaders




class KafkaHeaderMapperNoSpringHeader: DefaultKafkaHeaderMapper() {
    override fun fromHeaders(headers: MessageHeaders, target: Headers) {
        super.fromHeaders(headers, target)
        target.remove("spring_json_header_types")
    }
}