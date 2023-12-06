package com.study.kstream.model

enum class OrderState {
    CREATED,
    VALIDATED,
    FAILED,
    SHIPPED;
}