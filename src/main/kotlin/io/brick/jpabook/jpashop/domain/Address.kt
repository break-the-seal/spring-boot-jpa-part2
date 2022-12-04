package io.brick.jpabook.jpashop.domain

import javax.persistence.Embeddable

// 기본 생성자가 있어야 한다.
@Embeddable
data class Address(
    var city: String,
    var street: String,
    var zipcode: String
)