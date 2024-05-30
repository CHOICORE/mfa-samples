package me.choicore.samples.security.mfa.protectedreosurce.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProtectedResourceApi {
    /**
     * 보호된 리소스를 반환합니다.
     */
    @PostMapping("/v1/protected-resources")
    fun getProtectedResource(): Map<String, String> {
        return mapOf("message" to "Hello, World!")
    }
}
