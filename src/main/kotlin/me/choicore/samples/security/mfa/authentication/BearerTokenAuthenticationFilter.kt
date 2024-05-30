package me.choicore.samples.security.mfa.authentication

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.filter.OncePerRequestFilter

class BearerTokenAuthenticationFilter : OncePerRequestFilter() {
    private val bearerTokenResolver: BearerTokenResolver = DefaultBearerTokenResolver()
    private val authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> =
        WebAuthenticationDetailsSource()
    private val securityContextHolderStrategy: SecurityContextHolderStrategy =
        SecurityContextHolder.getContextHolderStrategy()
    private val authenticationEntryPoint: AuthenticationEntryPoint = Http403ForbiddenEntryPoint()
    private val authenticationFailureHandler: AuthenticationFailureHandler =
        AuthenticationEntryPointFailureHandler {
                request: HttpServletRequest,
                response: HttpServletResponse,
                exception: AuthenticationException,
            ->
            run {
                this.authenticationEntryPoint.commence(request, response, exception)
            }
        }

    private val authenticationManager: AuthenticationManager = MfaAuthenticationManager()
    private val securityContextRepository: SecurityContextRepository = RequestAttributeSecurityContextRepository()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token: String?
        try {
            token = bearerTokenResolver.resolve(request)
        } catch (e: AuthenticationException) {
            authenticationFailureHandler.onAuthenticationFailure(request, response, e)
            return
        }
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        val authenticationRequest =
            BearerTokenAuthenticationToken(token).apply {
                details = authenticationDetailsSource.buildDetails(request)
            }

        val authentication: Authentication = authenticationManager.authenticate(authenticationRequest)
        val securityContext: SecurityContext = securityContextHolderStrategy.createEmptyContext()
        securityContext.authentication = authentication
        securityContextHolderStrategy.context = securityContext
        securityContextRepository.saveContext(securityContext, request, response)
        filterChain.doFilter(request, response)
    }
}
