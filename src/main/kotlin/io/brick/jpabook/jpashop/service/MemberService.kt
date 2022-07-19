package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {
    /**
     * 회원 가입
     */
    @Transactional
    fun join(member: Member): Long {
        validateDuplicateMember(member) // 중복 회원 검증
        memberRepository.save(member)
        return member.id
    }

    private fun validateDuplicateMember(member: Member) {
        // Exception
        val members = memberRepository.findByName(member.name)
        if (members.isNotEmpty()) {
            throw IllegalStateException("이미 존재하는 회원입니다.")
        }
    }

    /**
     * 회원 전체 조회
     */
    fun findMembers(): List<Member> {
        return memberRepository.findAll()
    }

    fun findOne(memberId: Long): Member {
        return memberRepository.findOne(memberId)
            ?: throw RuntimeException("존재하지 않는 회원입니다. ${memberId}")
    }
}