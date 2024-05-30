package me.choicore.samples.security.mfa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MfaApplication

fun main(args: Array<String>) {
    runApplication<MfaApplication>(*args)
}
