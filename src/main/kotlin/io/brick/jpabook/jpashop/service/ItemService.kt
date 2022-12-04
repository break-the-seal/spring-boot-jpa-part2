package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.item.Book
import io.brick.jpabook.jpashop.domain.item.Item
import io.brick.jpabook.jpashop.exception.NotFoundException
import io.brick.jpabook.jpashop.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ItemService(
    private val itemRepository: ItemRepository
) {
    @Transactional
    fun saveItem(item: Item) {
        itemRepository.save(item)
    }

    @Transactional
    fun updateItem(
        itemId: Long,
        name: String,
        price: Int,
        stockQuantity: Int,
    ) {
        val findItem = itemRepository.findOne(itemId)
            ?: throw NotFoundException("존재하지 않는 상품입니다. ${itemId}")

        // 변경 감지 사용(dirty checking)
        findItem.apply {
            this.name = name
            this.price = price
            this.stockQuantity = stockQuantity
        }
    }

    fun findItems(): List<Item> {
        return itemRepository.findAll()
    }

    fun findOne(itemId: Long): Item {
        return itemRepository.findOne(itemId)
            ?: throw NotFoundException("존재하지 않는 상품입니다. ${itemId}")
    }
}