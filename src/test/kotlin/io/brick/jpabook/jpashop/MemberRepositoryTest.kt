package io.brick.jpabook.jpashop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
internal class MemberRepositoryTest {

   /* @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @Transactional
    @Rollback(false)
    fun testMember() {
        // given
        val member = Member(username = "memberA")

        // when
        val savedId = memberRepository.save(member)
        val findMember = memberRepository.find(savedId)

        // then
        assertEquals(findMember?.id, member.id)
        assertEquals(findMember?.username, member.username)

        assertEquals(findMember, member)
        assertSame(findMember, member)
        // insert만 있고 select는 없다.
        // 1차 캐시에 이미 member entity 저장,
        // 이후 조회에서는 id값 가지고 1차 캐시에서 조회하기에 select 쿼리 X
        println("findMember == member : ${findMember == member}")
    }*/
}