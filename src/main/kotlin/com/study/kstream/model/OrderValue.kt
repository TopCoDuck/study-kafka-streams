package com.study.kstream.model


class OrderValue(
    val order: Order? = null,
    val value: Double = 0.0,
) {
    companion object { //aggreate 초기화 해주어야 해서 사용
        fun empty() = OrderValue()
    }
}