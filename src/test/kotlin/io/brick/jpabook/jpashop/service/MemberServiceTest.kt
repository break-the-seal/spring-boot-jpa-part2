package io.brick.jpabook.jpashop.service

import io.brick.jpabook.jpashop.domain.Member
import io.brick.jpabook.jpashop.repository.MemberRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class MemberServiceTest {

    @Autowired
    lateinit var memberService: MemberService
    @Autowired
    lateinit var memberRepository: MemberRepository

    // 같은 tx 안에서는 같은 pc를 공유
    @Test
    // @Rollback(false)
    fun 회원가입() {
        // given
        val member = Member().apply { name = "beanie" }

        // when
        val savedId = memberService.join(member)

        // then
        assertEquals(member, memberRepository.findOne(savedId))
    }

    @Test
    fun 중복_회원_예외() {
        // given
        val member1 = Member().apply { name = "beanie" }
        val member2 = Member().apply { name = "beanie" }

        // when
        memberService.join(member1)
        assertThrows<IllegalStateException> {
            memberService.join(member2) // duplicate exception 발생해야 함
        }

        // then
        // 중간에 에러가 발생해야 한다. (but JUnit5 assertThrows로 커버 가능)
        // fail("예외가 발생해야 합니다.")
    }
}