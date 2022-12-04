package io.brick.jpabook.jpashop.domain.controller

import io.brick.jpabook.jpashop.domain.item.Book
import io.brick.jpabook.jpashop.service.ItemService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import javax.validation.Valid

@Controller
class ItemController(
    private val itemService: ItemService,
) {
    @GetMapping("/items/new")
    fun createForm(model: Model): String {
        model["itemForm"] = BookForm()
        return "items/createItemForm"
    }

    @PostMapping("/items/new")
    fun create(
        @Valid @ModelAttribute("itemForm") form: BookForm,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            return "items/createItemForm"
        }

        // 원래는 static 생성자 메서드를 통해 entity 객체 생성하는 것이 좋다.
        // setter는 없애는 것이 좋다.
        val book = Book().apply {
            name = form.name!!
            price = form.price
            stockQuantity = form.stockQuantity
            author = form.author
            isbn = form.isbn
        }

        itemService.saveItem(book)

        return "redirect:/"
    }

    @GetMapping("/items")
    fun list(model: Model): String {
        val items = itemService.findItems()
        model["items"] = items

        return "items/itemList"
    }

    @GetMapping("/items/{itemId}/edit")
    fun updateItemForm(
        @PathVariable("itemId") itemId: Long,
        model: Model,
    ): String {
        val item = itemService.findOne(itemId) as Book
        val form = BookForm().apply {
            id = item.id
            name = item.name
            price = item.price
            stockQuantity = item.stockQuantity
            author = item.author
            isbn = item.isbn
        }

        model["form"] = form

        return "items/updateItemForm"
    }

    @PostMapping("/items/{itemId}/edit")
    fun updateItem(
        @PathVariable("itemId") itemId: Long,
        @ModelAttribute("form") form: BookForm,
    ): String {
//        val book = Book().apply {
//            id = form.id!!
//            name = form.name!!
//            price = form.price
//            stockQuantity = form.stockQuantity
//            author = form.author
//            isbn = form.isbn
//        }
//        itemService.saveItem(book)

        itemService.updateItem(
            itemId = itemId,
            name = form.name!!,
            price = form.price,
            stockQuantity = form.stockQuantity
        )

        return "redirect:/items"
    }
}