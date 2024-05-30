package me.choicore.samples.security.mfa.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication

class MfaAuthenticationToken(
    private val original: Authentication,
) : AbstractAuthenticationToken(listOf()) {
    override fun getCredentials(): Any {
        return original.credentials
    }

    override fun getPrincipal(): Any {
        return original.principal
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    fun getOriginal(): Authentication {
        return original
    }
}
