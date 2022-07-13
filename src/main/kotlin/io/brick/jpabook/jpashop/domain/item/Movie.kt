package io.brick.jpabook.jpashop.domain.item

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("M")
data class Movie(
    val director: String,
    val actor: String
): Item()