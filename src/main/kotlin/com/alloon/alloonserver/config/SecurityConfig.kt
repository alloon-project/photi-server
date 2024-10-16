package com.alloon.alloonserver.config

import com.alloon.alloonserver.config.auth.CustomAuthenticationEntryPoint
import com.alloon.alloonserver.config.auth.CustomAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val customAuthenticationFilter: CustomAuthenticationFilter,
    val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(POST, "/api/challenges").authenticated()
                it.requestMatchers(PATCH, "/api/challenges/{challengeId}").authenticated()
                it.requestMatchers(DELETE, "/api/challenges/{challengeId}").authenticated()
                it.requestMatchers(
                    "/api/users",
                    "/api/users/token",
                    "/api/users/password",
                    "/api/users/image",
                    "/api/users/challenge-history",
                    "/api/users/feeds",
                    "/api/users/challenges",
                    "/api/challenges/{challengeId}/info",
                    "/api/challenges/{challengeId}/challenge-members/goal",
                    "/api/challenges/{challengeId}/challenge-members",
                    "/api/challenges/{challengeId}/feeds",
                    "/api/challenges/{challengeId}/feeds/{feedId}",
                    "/api/challenges/{challengeId}/feeds/{feedId}/comments",
                ).authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(
                customAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling { it.authenticationEntryPoint(customAuthenticationEntryPoint) }
            .build()
    }
}