package com.study.kstream
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudyKafkaStreamsApplication

fun main(args: Array<String>) {
    runApplication<StudyKafkaStreamsApplication>(*args)
}
