package me.choicore.samples.security.mfa.configuration

import me.choicore.samples.security.mfa.authentication.BearerTokenAuthenticationFilter
import me.choicore.samples.security.mfa.authentication.MfaAuthenticationToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher

@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer {
            it.ignoring()
                .requestMatchers(antMatcher("/v1/authenticate"))
                .requestMatchers(antMatcher("/error"))
        }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it ->
                it.frameOptions { it.sameOrigin() }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/v1/authenticate/multi-factor").access { authentication, _ ->
                        AuthorizationDecision(authentication.get() is MfaAuthenticationToken)
                    }
                    .anyRequest()
                    .authenticated()
            }
            .addFilterAt(BearerTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
