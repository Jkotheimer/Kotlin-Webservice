package com.example.services.data.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.springframework.ui.Model
import org.springframework.http.HttpStatusCode

@RestController
@RequestMapping("/users")
class UserController(private val repository: UserRepository) {

	@GetMapping("/")
	fun findAll(model: Model): ModelAndView {
		val users = repository.findAll()
		model.addAttribute("title", "All Users");
		model.addAttribute("users", users)
		return ModelAndView("test", model.asMap(), HttpStatusCode.valueOf(200))
	}

	@GetMapping("/users/{id}")
	fun findById(@PathVariable id:String) = repository.findById(id)
}