package io.brick.jpabook.jpashop.domain.controller

import javax.validation.constraints.NotEmpty

data class BookForm(
    var id: Long? = null,
    @field:NotEmpty(message = "상품 이름은 필수 입니다.")
    var name: String? = null,
    var price: Int = 0,
    var stockQuantity: Int = 0,
    var author: String? = null,
    var isbn: String? = null,
)