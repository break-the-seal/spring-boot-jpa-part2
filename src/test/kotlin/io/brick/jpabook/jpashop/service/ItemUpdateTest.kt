package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.item.Book
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager

@SpringBootTest
internal class ItemUpdateTest {
    @Autowired
    lateinit var em: EntityManager

    @Test
    fun updateTest() {
        val book = em.find(Book::class.java, 1L)

        // TX
        book.name = "update name"

        // 변경 감지 == dirty checking
        // TX commit
    }

}