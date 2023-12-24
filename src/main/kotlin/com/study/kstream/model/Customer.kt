package com.study.kstream.model


data class Customer(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val level: String,
)