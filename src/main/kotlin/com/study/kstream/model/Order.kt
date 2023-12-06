package com.study.kstream.model

import com.study.kstream.config.NoArg

@NoArg
data class Order (
    val id: String,
    val customerId: Long?,
    val state: OrderState?,
    val product: Product,
    val quantity: Int,
    val price: Double,
)