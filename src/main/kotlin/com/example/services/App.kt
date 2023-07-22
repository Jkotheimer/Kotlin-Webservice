package com.example.services

import com.example.services.data.user.User
import com.example.services.data.user.UserRepository

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

import org.springframework.dao.DataIntegrityViolationException

@SpringBootApplication
class App {

    private val log = LoggerFactory.getLogger(App::class.java)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<App>()
        }
    }

    @Bean
    fun init(repository: UserRepository) = CommandLineRunner {
        // save a couple of Users
        try {
            repository.saveAll(mutableListOf(
                User("jack@gmail.com", "jbaur"),
                User("chloe@gmail.com"),
                User("jim@yahoo.com", "jimbo22"),
            ))
        } catch (ex: DataIntegrityViolationException) {
            log.info("Failed to create records")
            log.info(ex.toString())
        }

        // fetch all Users
        log.info("Users found with findAll():")
        log.info("-------------------------------")
        repository.findAll().forEach {
            log.info(it.id.toString() + ": " + it.email)
        }
        log.info("")

        // fetch Users by last name
        log.info("User found with findByEmail('jack@gmail.com'):")
        log.info("--------------------------------------------")
        val usr: User = repository.findByEmail("jack@gmail.com")
        log.info(usr.toString())
        log.info("")
    }
}