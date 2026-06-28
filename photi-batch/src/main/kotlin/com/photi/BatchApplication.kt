package com.photi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackages = ["com.photi"]
)
@EnableScheduling
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}
