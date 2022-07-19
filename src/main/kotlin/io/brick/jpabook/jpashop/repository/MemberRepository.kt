package io.brick.jpabook.jpashop.repository

import io.brick.jpabook.jpashop.domain.Member
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class MemberRepository(
    // spring-data-jpa에서 알아서 지원해줌 (autowired 만으로 주입 가능)
    // @PersistenceContext
    private val em: EntityManager,
) {
    fun save(member: Member) {
        em.persist(member)
    }

    fun findOne(id: Long): Member? {
        return em.find(Member::class.java, id)
    }

    fun findAll(): List<Member> {
        return em.createQuery("select m from Member m", Member::class.java)
            .resultList
    }

    fun findByName(name: String): List<Member> {
        return em.createQuery("select m from Member m where m.name = :name", Member::class.java)
            .setParameter("name", name)
            .resultList
    }
}