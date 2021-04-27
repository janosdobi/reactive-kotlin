package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.service.ViewAuthorizer
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException


@Controller
class ViewController(
    private val viewAuthorizer: ViewAuthorizer
) {

    @GetMapping("/home")
    fun home(model: Model, @RequestParam(required = false) token: String?): String? {
        try {
            viewAuthorizer.authorize(token)
        } catch (ex: HttpClientErrorException) {
            return "error-yourfault"
        }
        return "home"
    }

    @GetMapping("/game/{code}")
    fun game(model: Model, @RequestParam(required = false) token: String?, @PathVariable code: String): String? {
        try {
            viewAuthorizer.authorize(token)
            model.addAttribute("gameCode", code)
        } catch (ex: HttpClientErrorException) {
            return "error-yourfault"
        }
        return "game"
    }
}