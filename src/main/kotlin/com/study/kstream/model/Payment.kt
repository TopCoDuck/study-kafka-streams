package com.study.kstream.model

data class Payment (val id: String,
    val orderId: String,
    val ccy: String,
    val amount: Double,
)