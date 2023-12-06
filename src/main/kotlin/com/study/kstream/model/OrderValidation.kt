package com.study.kstream.model

data class OrderValidation (
    val orderId: String?,
    val checkType: OrderValidationType?,
    val validationResult: OrderValidationResult?,
)