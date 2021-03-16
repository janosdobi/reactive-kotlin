package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.persistence.PersistentObjectRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable


@Controller
class ViewController(
    private val repo: PersistentObjectRepository
) {

    @RequestMapping("/")
    fun index(model: Model): String? {

        val reactiveDataDrivenMode = ReactiveDataDriverContextVariable(repo.findAll(), 1)
        model.addAttribute("entities", reactiveDataDrivenMode)

        return "home"
    }
}