package me.chnu.replication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReplicationApplication

fun main(args: Array<String>) {
    runApplication<ReplicationApplication>(*args)
}
