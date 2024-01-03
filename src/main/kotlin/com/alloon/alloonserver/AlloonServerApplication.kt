package com.alloon.alloonserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AlloonServerApplication

fun main(args: Array<String>) {
	runApplication<AlloonServerApplication>(*args)
}
