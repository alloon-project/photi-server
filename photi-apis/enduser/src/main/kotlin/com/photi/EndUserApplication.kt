package com.photi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EndUserApplication

fun main(args: Array<String>) {
    runApplication<EndUserApplication>(*args)
}
