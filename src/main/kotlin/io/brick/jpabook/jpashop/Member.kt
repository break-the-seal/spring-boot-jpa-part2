package io.brick.jpabook.jpashop

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Member(
    @Id
    @GeneratedValue
    val id: Long = 0L,

    val username: String
)