package io.brick.jpabook.jpashop.domain.api

import com.fasterxml.jackson.annotation.JsonInclude
import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.service.MemberService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@RestController
class MemberApiController(
    private val memberService: MemberService,
) {
    @GetMapping("/api/v1/members")
    fun membersV1(): Result<List<MemberDto>> {
        val result = memberService.findMembers().map { MemberDto(it.name) }
        return Result.data(result).count(result.size)
    }

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

class Result<T> (
    val data: T? = null
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var count: Int? = null
        private set

    companion object {
        fun <T> data(data: T): Result<T> {
            return Result(data = data)
        }
    }

    fun count(count: Int): Result<T> {
        this.count = count
        return this
    }
}

data class MemberDto(
    val name: String
)