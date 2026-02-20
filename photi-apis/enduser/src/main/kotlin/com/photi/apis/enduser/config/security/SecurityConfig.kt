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
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(PATCH, "/api/v2/app-version").hasRole("ADMIN")
                it.requestMatchers(
                    "/api/v2/users/image/pre-signed-url",
                    "/api/v2/feeds/image/pre-signed-url",
                    "/api/v2/challenges/image/pre-signed-url",
                ).permitAll()
                it.requestMatchers(
                    GET,
                    "/api/v2/auth/validate/token",
                    "/api/v2/challenges/{challengeId}/invitation-code",
                    "/api/v2/challenges/{challengeId}/intro",
                    "/api/v2/challenges/{challengeId}/feed",
                ).authenticated()
                it.requestMatchers(
                    PATCH,
                    "/api/v2/auth",
                    "/api/v2/auth/password",
                    "/api/v2/challenges/{challengeId}",
                ).authenticated()
                it.requestMatchers(
                    POST,
                    "/api/v2/challenges",
                    "/api/v2/challenges/{challengeId}/join",
                    "/api/v2/oauth/username",
                ).authenticated()
                it.requestMatchers(DELETE, "/api/v2/challenges/{challengeId}").authenticated()
                it.requestMatchers(
                    "/api/v2/users/**",
                    "/api/v2/reports/**",
                    "/api/v2/inquires/**",
                    "/api/v2/feed-likes/**",
                    "/api/v2/feed-comments/**",
                    "/api/v2/feeds/**",
                ).authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .build()
    }
}
