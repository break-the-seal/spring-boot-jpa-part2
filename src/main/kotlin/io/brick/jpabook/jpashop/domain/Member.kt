package io.brick.jpabook.jpashop.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
class Member protected constructor(
    name: String,
    address: Address? = null
){
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    var id: Long = 0L

    @NotEmpty
    var name: String = name

    @Embedded
    var address: Address? = address

    // 반대편 연관관계 주인이 되는 곳과 매핑(read-only 개념)
    // 여기서 orders 변경해도 order 테이블의 FK 변경 X
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    var orders: MutableList<Order> = ArrayList()

    companion object {
        fun createMember(name: String, address: Address? = null): Member {
            return Member(
                name = name,
                address = address
            )
        }
    }
}
