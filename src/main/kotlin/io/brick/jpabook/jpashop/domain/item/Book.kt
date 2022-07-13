package io.brick.jpabook.jpashop.domain.item

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("B")
data class Book(
    val artist: String,
    val isbn: String
): Item()