package com.study.kstream.model.email

import com.study.kstream.model.Customer
import com.study.kstream.model.Order
import com.study.kstream.model.Payment

class EmailTuple(val order: Order, val payment: Payment) {

    lateinit var customer: Customer

    fun setCustomer(customer: Customer) : EmailTuple {
        this.customer = customer
        return this
    }

}
