package com.study.kstream.model

data class OrderBean (
    val id: String,
    val customerId: Long?,
    val state: OrderState?,
    val product: Product,
    val quantity: Int,
    val price: Double,
    ) {
    companion object {
        fun toBean(order: Order) =
            OrderBean(order.id,
                order.customerId,
                order.state,
                order.product,
                order.quantity,
                order.price)

        fun formBean(orderBean: OrderBean) =
            Order(orderBean.id,
                orderBean.customerId,
                orderBean.state,
                orderBean.product,
                orderBean.quantity,
                orderBean.price,
            )
    }
}
