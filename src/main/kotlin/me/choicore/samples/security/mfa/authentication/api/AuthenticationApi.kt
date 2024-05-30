package me.choicore.samples.security.mfa.authentication.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationApi {
    data class AuthenticationRequest(
        val username: String,
        val password: String,
    )

    /**
     * 사용자 이름과 암호를 사용하여 인증을 수행합니다.
     */
    @PostMapping("/v1/authenticate")
    fun signIn(
        @RequestBody authentication: AuthenticationRequest,
    ): Map<String, String> {
        return mapOf("access_token" to "abcdefghijklmnopqrstuvwxyz")
    }

    /**
     * 인증 코드를 사용하여 다중 인증을 수행합니다.
     */
    @PostMapping("/v1/authenticate/multi-factor")
    fun mfa(code: String): Map<String, String> {
        return mapOf("access_token" to "zyxwvutsrqponmlkjihgfedcba")
    }
}
