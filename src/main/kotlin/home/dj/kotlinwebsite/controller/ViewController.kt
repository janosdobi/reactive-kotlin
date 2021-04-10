package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.service.ViewAuthorizer
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException


@Controller
@RequestMapping("/view")
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
}