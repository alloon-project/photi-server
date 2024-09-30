package com.alloon.alloonserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AlloonServerApplication

fun main(args: Array<String>) {
    runApplication<AlloonServerApplication>(*args)
}
