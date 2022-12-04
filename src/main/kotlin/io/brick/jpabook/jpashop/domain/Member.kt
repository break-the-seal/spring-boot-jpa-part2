package io.brick.jpabook.jpashop.domain

import javax.persistence.*

@Entity
class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    var id: Long = 0L

    var name: String = ""

    @Embedded
    var address: Address? = null

    // 반대편 연관관계 주인이 되는 곳과 매핑(read-only 개념)
    // 여기서 orders 변경해도 order 테이블의 FK 변경 X
    @OneToMany(mappedBy = "member")
    var orders: MutableList<Order> = ArrayList()
}
