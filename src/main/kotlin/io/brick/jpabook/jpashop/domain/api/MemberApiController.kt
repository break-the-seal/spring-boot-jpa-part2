package io.brick.jpabook.jpashop.domain.api

import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.service.MemberService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@RestController
class MemberApiController(
    private val memberService: MemberService,
) {
    @PostMapping("/api/v1/members")
    fun saveMemberV1(@RequestBody @Valid member: Member): CreateMemberResponse {
        val id = memberService.join(member)
        return CreateMemberResponse(id)
    }

    @PostMapping("/api/v2/members")
    fun saveMemberV2(@RequestBody @Valid request: CreateMemberRequest): CreateMemberResponse {
        val id = memberService.join(Member().apply {
            this.name = request.name!!
        })
        return CreateMemberResponse(id)
    }

    @PutMapping("/api/v2/members/{id}")
    fun updateMemberV2(
        @PathVariable("id") id: Long,
        @RequestBody @Valid request: UpdateMemberRequest,
    ): UpdateMemberResponse {
        // command와 query를 분리
        memberService.update(id, request.name)  // command
        val findMember = memberService.findOne(id)  // query
        return UpdateMemberResponse(findMember.id, findMember.name)
    }
}

data class CreateMemberRequest(
    @field:NotEmpty
    val name: String? = null,
)

data class CreateMemberResponse(
    val id: Long,
)

data class UpdateMemberRequest(
    val name: String
)

data class UpdateMemberResponse(
    val id: Long,
    val name: String
)