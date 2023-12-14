package com.study.kstream.config

import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper
import org.springframework.kafka.support.serializer.JsonSerde

class FixJsonSerde<T>: JsonSerde<T>() {

    override fun typeMapper(mapper: Jackson2JavaTypeMapper): JsonSerde<T> {
        print("ddddddddddddddddddddddddddddddddddddd")
        mapper.addTrustedPackages("*")
        super.typeMapper(mapper)
        return this
    }
}
