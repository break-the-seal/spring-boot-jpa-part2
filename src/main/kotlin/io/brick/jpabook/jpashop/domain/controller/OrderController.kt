package io.brick.jpabook.jpashop.domain.controller

import io.brick.jpabook.jpashop.repository.OrderSearch
import io.brick.jpabook.jpashop.service.ItemService
import io.brick.jpabook.jpashop.service.MemberService
import io.brick.jpabook.jpashop.service.OrderService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class OrderController(
    private val orderService: OrderService,
    private val memberService: MemberService,
    private val itemService: ItemService
) {
    @GetMapping("/order")
    fun createForm(model: Model): String {
        val members = memberService.findMembers()
        val items = itemService.findItems()

        model["members"] = members
        model["items"] = items

        return "order/orderForm"
    }

    @PostMapping("/order")
    fun order(
        @RequestParam("memberId") memberId: Long,
        @RequestParam("itemId") itemId: Long,
        @RequestParam("count") count: Int,
    ): String {
        orderService.order(
            memberId = memberId,
            itemId = itemId,
            count = count
        )

        return "redirect:/orders"
    }

    @GetMapping("/orders")
    fun orderList(
        @ModelAttribute("orderSearch") orderSearch: OrderSearch,
        model: Model
    ): String {
        val orders = orderService.findOrders(orderSearch)
        model["orders"] = orders

        return "order/orderList"
    }

    @PostMapping("/orders/{orderId}/cancel")
    fun cancelOrder(@PathVariable("orderId") orderId: Long): String {
        orderService.cancelOrder(orderId)
        return "redirect:/orders"
    }
}