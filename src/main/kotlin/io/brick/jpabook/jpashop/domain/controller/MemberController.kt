package io.brick.jpabook.jpashop.domain.controller

import io.brick.jpabook.jpashop.domain.Address
import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.service.MemberService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import javax.validation.Valid

@Controller
class MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/members/new")
    fun createForm(model: Model): String {
        model["memberForm"] =  MemberForm()
        return "members/createMemberForm"
    }

    @PostMapping("/members/new")
    fun create(
        @Valid form: MemberForm,
        bindingResult: BindingResult,
    ): String {
        if (bindingResult.hasErrors()) {
            return "members/createMemberForm"
        }

        val address = Address(
            city = form.city!!,
            street = form.street!!,
            zipcode = form.zipcode!!
        )

        val member = Member.createMember(
            name = form.name!!,
            address = address
        )

        memberService.join(member)

        return "redirect:/"
    }

    @GetMapping("/members")
    fun list(model: Model): String {
        val members = memberService.findMembers()
        model["members"] = members
        return "members/memberList"
    }
}