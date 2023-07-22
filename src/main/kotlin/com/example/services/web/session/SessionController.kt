package com.example.services.web.session

// import org.springframework.web.bind.annotation.RestController

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.support.SessionStatus
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/")
@SessionAttributes(names = ["userSession"])
class SessionController {

    private val logger = LoggerFactory.getLogger(SessionController::class.java)

    // @GetMapping
    // fun index(model: Model, request: HttpServletRequest): Any {
    //    return RedirectView("/login");
    // }

    @GetMapping("/login")
    fun getLoginPage(model: Model, request: HttpServletRequest): Any {
        val serverHostHeader = request.getHeaders("Host")
        logger.info("Got server host header")
        while (serverHostHeader.hasMoreElements()) {
            logger.info(serverHostHeader.nextElement())
        }
        logger.info("Added host attribute")
        model.addAttribute("title", "Login")
        val session: UserSession? = model.getAttribute("userSession") as? UserSession
        logger.info("Got user session")
        return if (null == session || !session.isActive) {
            logger.info("session is null")
            model.addAttribute("form", getLoginForm())
            ModelAndView("user/login", model.asMap(), HttpStatusCode.valueOf(403))
        } else {
            logger.info("session is not null")
            logger.info(session?.toString())
            ModelAndView("test", model.asMap(), HttpStatusCode.valueOf(200))
        }
    }

    @PostMapping("/login")
    fun login(model: Model, loginForm: LoginForm): ModelAndView {
        logger.info("Email: " + loginForm.email)
        logger.info("Password: " + loginForm.password)
        model.addAttribute("title", "Login")
        model.addAttribute("form", getLoginForm())
        return ModelAndView("user/login", model.asMap(), HttpStatusCode.valueOf(200))
    }

    @PostMapping("/logout")
    fun logout(sessionStatus: SessionStatus): String {
        sessionStatus.setComplete()
        return "redirect:/session-attr"
    }

    fun getLoginForm(): Map<String, Any> =
            mapOf(
                    "action" to "/login",
                    "method" to "post",
                    "inputs" to
                            arrayOf(
                                    Input("email", "Email", "", "email"),
                                    Input("password", "Password", "", "password"),
                                    Input("submit", "Login", "Submit", "submit"),
                                    Input("rememberMe", "Remeber Me", false, "checkbox")
                            )
            )
}

data class Input(val id: String, val label: String, val value: Any, val type: String)

data class LoginForm(
        val email: String? = null,
        val password: String? = null,
        val rememberMe: Boolean = false
)

data class UserSession(val isActive: Boolean = false, val id: String? = null)
