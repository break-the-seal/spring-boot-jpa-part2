package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.item.Item
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

    fun findItems(): List<Item> {
        return itemRepository.findAll()
    }

    fun findOne(itemId: Long): Item {
        return itemRepository.findOne(itemId)
            ?: throw RuntimeException("존재하지 않는 상품입니다. ${itemId}")
    }
}