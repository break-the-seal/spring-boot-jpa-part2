package io.brick.jpabook.jpashop.domain.item

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("B")
class Book : Item() {
    var artist: String? = null
    var isbn: String? = null
}