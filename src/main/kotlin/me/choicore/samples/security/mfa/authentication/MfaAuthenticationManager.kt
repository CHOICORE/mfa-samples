package me.choicore.samples.security.mfa.authentication

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken

class MfaAuthenticationManager : AuthenticationManager {
    override fun authenticate(authentication: Authentication): Authentication {
        val original: BearerTokenAuthenticationToken = authentication as BearerTokenAuthenticationToken
        return when (original.token) {
            "abcdefghijklmnopqrstuvwxyz" -> MfaAuthenticationToken(original)
            else -> authentication.apply { isAuthenticated = true }
        }
    }
}
