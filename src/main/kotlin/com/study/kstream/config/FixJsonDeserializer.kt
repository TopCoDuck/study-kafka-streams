package com.study.kstream.config

import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper
import org.springframework.kafka.support.serializer.JsonDeserializer

class FixJsonDeserializer<T> : JsonDeserializer<T>(){

        init {
            this.addTrustedPackages("*")
        }
}