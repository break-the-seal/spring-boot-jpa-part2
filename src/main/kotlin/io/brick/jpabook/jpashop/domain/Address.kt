package io.brick.jpabook.jpashop.domain

import javax.persistence.Embeddable

// 기본 생성자가 있어야 한다.
@Embeddable
data class Address(
    private val city: String,
    private val street: String,
    private val zipcode: String
)