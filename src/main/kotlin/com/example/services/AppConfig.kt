package com.example.services

import org.slf4j.LoggerFactory

import jakarta.servlet.ServletContext
import jakarta.servlet.SessionTrackingMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.Collections
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.DeferredSecurityContext
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken 
import org.springframework.security.core.*
import jakarta.servlet.http.Cookie

@Configuration
@EnableWebSecurity
class AppConfig {

    private val logger = LoggerFactory.getLogger(AppConfig::class.java)

    @Bean
    fun servletContextInitializer() = ServletContextInitializer {
        fun onStartup(context: ServletContext?) {
            context?.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE))
        }
    }

    @Bean
    @Order(0)
    fun checkRequestCredentials(http: HttpSecurity): SecurityFilterChain {
        logger.info("Registering sfc")
        http.authenticationManager(AuthenticationManager {
            logger.info("Authenticating request!")
            logger.info(it.principal.toString())
            logger.info(it.credentials.toString())
            it
        }).authorizeHttpRequests({
            logger.info("Matching request")
            it.requestMatchers("/login").hasRole("USER")
        }).securityContext({
            it.securityContextRepository(object: SecurityContextRepository {
                override fun loadContext(responseHolder: HttpRequestResponseHolder): SecurityContext? = null
                override fun containsContext(request: HttpServletRequest): Boolean {
                    logger.info("Checking if request contains context")
                    return true
                }
                override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {
                    logger.info("Saving context")
                }
                override fun loadDeferredContext(request: HttpServletRequest): DeferredSecurityContext {
                    logger.info("Loading deferred contex")
                    return object: DeferredSecurityContext {
                        override fun isGenerated() = true
                        override fun get() = object: SecurityContext {
                            override fun getAuthentication(): Authentication? {
                                logger.info("Getting authentication")
                                val cookies: Array<Cookie> = request.getCookies();
                                logger.info("Getting cookies");
                                for (cookie in cookies) {
                                    logger.info(cookie.name)
                                    logger.info(cookie.value)
                                }

                                return UsernamePasswordAuthenticationToken(request.getParameter("userId"), request.getParameter("password"), mutableListOf(SimpleGrantedAuthority("User")))
                            }
                            override fun setAuthentication(auth: Authentication) {
                                logger.info("Setting authentication");
                                logger.info(auth.name)
                                logger.info(auth.toString())
                            }
                        }
                    }
                }
            }).requireExplicitSave(true)
              .configure(http)
        })
        return http.build();
    }
}
