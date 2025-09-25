package com.photi.apis.enduser.config.security

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
    val customAccessDeniedHandler: CustomAccessDeniedHandler,
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
                it.requestMatchers(PATCH, "/api/app-version").hasRole("MASTER")
                it.requestMatchers(DELETE, "/api/challenges/{challengeId}").authenticated()
                it.requestMatchers(
                    "/api/users",
                    "/api/users/password",
                    "/api/users/image",
                    "/api/users/challenge-history",
                    "/api/users/feeds",
                    "/api/users/challenges",
                    "/api/users/feeds-by-date",
                    "/api/users/feed-history",
                    "/api/users/ended-challenges",
                    "/api/users/my-challenges",
                    "/api/users/challenges/{challengeId}/prove",
                    "/api/challenges/{challengeId}/info",
                    "/api/challenges/{challengeId}/invitation-code",
                    "/api/challenges/{challengeId}/challenge-members/goal",
                    "/api/challenges/{challengeId}/challenge-members",
                    "/api/challenges/{challengeId}/join",
                    "/api/challenges/{challengeId}/feeds",
                    "/api/challenges/{challengeId}/feeds/{feedId}",
                    "/api/challenges/{challengeId}/feeds/{feedId}/like",
                    "/api/challenges/{challengeId}/feeds/{feedId}/comments",
                    "/api/challenges/{challengeId}/feed-members",
                    "/api/challenges/{challengeId}/feed-existence",
                    "/api/challenges/feeds/{feedId}/comments",
                    "/api/inquiries",
                    "/api/reports/{targetId}"
                ).authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(
                customAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .build()
    }
}
