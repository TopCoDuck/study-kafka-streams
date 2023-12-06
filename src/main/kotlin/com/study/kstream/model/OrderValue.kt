package com.study.kstream.model

import com.study.kstream.config.NoArg


class OrderValue(
    val order: Order?,
    val value: Double,
) {
    companion object { //aggreate 초기화 해주어야 해서 사용
        fun empty()  = OrderValue(null, 0.0)
    }
}