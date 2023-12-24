package com.study.kstream.model

import com.study.kstream.config.NoArg

@NoArg
data class OrderValidation(
    val orderId: String?,
    val checkType: OrderValidationType?,
    val validationResult: OrderValidationResult?,
)