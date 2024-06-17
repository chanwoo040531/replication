package me.chnu.replication

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.with
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.core.env.Environment
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestReplicationApplication {

    @Bean
    @ServiceConnection
    fun primaryContainer(environment: Environment) =
        PostgreSQLContainer(DockerImageName.parse("postgres:latest")).apply {
            dockerImageName = PRIMARY_DATASOURCE

            withDatabaseName("postgres_primary")
            withUsername("postgres")
            withPassword("password")
            withCommand(
                "postgres",
                "-c", "wal_level=replica",
                "-c", "hot_standby=on",
                "-c", "max_wal_senders=10",
                "-c", "max_replication_slots=10",
                "-c", "hot_standby_feedback=on",
            )
            addParameter("--auth-host", "sha-256")
            withInitScript("00_init.sql")
        }

    @Bean
    @DependsOn(PRIMARY_DATASOURCE)
    @ServiceConnection
    fun standbyContainer(environment: Environment) =
        PostgreSQLContainer(DockerImageName.parse("postgres:latest")).apply {
            dockerImageName = STANDBY_DATASOURCE
            withDatabaseName("postgres_standby")
            withUsername("postgres")
            withPassword("password")

        }

    companion object {
        const val PRIMARY_DATASOURCE = "primaryContainer"
        const val STANDBY_DATASOURCE = "standbyContainer"
    }
}

fun main(args: Array<String>) {
    fromApplication<ReplicationApplication>().with(TestReplicationApplication::class).run(*args)
}
