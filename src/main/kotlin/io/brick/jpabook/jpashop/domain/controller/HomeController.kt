package io.brick.jpabook.jpashop.domain.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class HomeController {
    companion object: KLogging()

    @RequestMapping("/")
    fun home(): String {
        logger.info { "home controller" }
        return "home"
    }
}