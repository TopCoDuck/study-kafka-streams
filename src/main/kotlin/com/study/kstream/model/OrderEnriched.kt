package com.study.kstream.model

data class OrderEnriched(
    val id: String,
    val customerId: Long,
    val customerLevel: String,
)
