package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.OrderStatus

data class OrderSearch(
    var memberName: String? = null, // 회원 이름
    var orderStatus: OrderStatus? = null // 주문 상태[ORDER, CANCEL]
)
