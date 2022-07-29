package io.brick.jpabook.jpashop

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HelloController {

    @GetMapping("/hello")
    fun hello(model: Model): String {
        model["data"] = "hello!!!"
        return "hello"
    }
}