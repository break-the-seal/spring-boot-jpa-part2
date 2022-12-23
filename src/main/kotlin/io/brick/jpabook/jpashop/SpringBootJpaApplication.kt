package io.brick.jpabook.jpashop

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SpringBootJpaApplication {
    /**
     * Hibernate5 모듈을 받아서 Bean을 설정해준다.
     * 기본전략은 LAZY loading이면 무시하는 것인데 다르게 설정가능
     *  - 그래서 아래 설정하고 api 호출하면 lazy에 대해서는 null이 나오는 것을 볼 수 있다.
     * @return Hibernate5Module
     */
    @Bean
    fun hibernate5Module(): Hibernate5Module {
        return Hibernate5Module().apply {
            /**
             * 위 방식대로 하면 LAZY 설정되어 있는 연관관계 필드에 대해서 LAZY 방식으로 조회하게 된다.
             * 이렇게 설정하면 lazy 방식으로 연관관계 필드에 대해서 전부 호출해서 값을 내려보내는 것을 알 수 있다.
             */
            // this.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true)
        }
    }
}


fun main(args: Array<String>) {
    runApplication<SpringBootJpaApplication>(*args)
}
