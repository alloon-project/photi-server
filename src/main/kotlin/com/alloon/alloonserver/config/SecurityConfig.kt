package com.alloon.alloonserver.config

import com.alloon.alloonserver.config.auth.CustomAuthenticationEntryPoint
import com.alloon.alloonserver.config.auth.CustomAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
                it.requestMatchers(
                    "/api/users/token",
                    "/api/users/password",
                    "/api/missions",
                    "/api/missions/{missionId}/info",
                    "/api/missions/{missionId}/mission-members/goal",
                    "/api/missions/{missionId}/mission-members",
                ).authenticated()
                it.anyRequest().permitAll()
            }
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it.authenticationEntryPoint(customAuthenticationEntryPoint) }
            .build()
    }
}