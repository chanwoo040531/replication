package me.chnu.replication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ReplicationApplication

fun main(args: Array<String>) {
    runApplication<ReplicationApplication>(*args)
}
