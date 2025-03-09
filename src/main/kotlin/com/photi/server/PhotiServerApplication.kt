package com.photi.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PhotiServerApplication

fun main(args: Array<String>) {
    runApplication<PhotiServerApplication>(*args)
}
