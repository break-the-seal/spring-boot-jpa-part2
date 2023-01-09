package io.brick.jpabook.jpashop.repository.order.query

import com.fasterxml.jackson.annotation.JsonIgnore

data class OrderItemQueryDto(
    @JsonIgnore
    val orderId: Long,
    val itemName: String,
    val orderPrice: Int,
    val count: Int
)
