package me.chnu.replication

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@ActiveProfiles("test")
@Import(DataSourceConfig::class)
@SpringBootTest
annotation class IntegrationTest

@TestConfiguration(proxyBeanMethods = false)
class DataSourceConfig {

    @Bean
    @ServiceConnection(name = "postgreSQLContainer")
    fun primaryDBContainer(): PostgreSQLContainer<*> =
        PostgreSQLContainer(POSTGRES_IMAGE)
            .withExposedPorts(5432)
            .withDatabaseName("primary")
            .withUsername("user")
            .withPassword("password")
            .withCommand(
                "postgres",
                "-c wal_level=replica",
                "-c hot_standby=on",
                "-c max_wal_senders=10",
                "-c max_replication_slots=10",
                "-c hot_standby_feedback=on",
            )
            .withInitScript("00_init.sql")
            .withReuse(true)
            .waitingFor(Wait.forListeningPort())

    @Bean
    @DependsOn("primaryDBContainer")
    @ServiceConnection(name = "postgreSQLContainer")
    fun standbyDBContainer(): PostgreSQLContainer<*> =
        PostgreSQLContainer(POSTGRES_IMAGE)
            .withExposedPorts(5433)
            .withDatabaseName("standby")
            .withUsername("user")
            .withPassword("password")
            .withEnv("PGUSER", "replicator")
            .withEnv("PGPASSWORD", "replicator_password")
            .withCommand(
                "bash -c;",
                "until pg_basebackup --pgdata=/var/lib/postgresql/data -R --slot=replication_slot --host=primary --port=5432;",
                "do;",
                "echo 'Waiting for primary to connect...';",
                "sleep 1s;",
                "done;",
                "echo 'Backup done, starting replica...';",
                "chmod 0700 /var/lib/postgresql/data;",
                "postgres;",
            )
            .withReuse(true)
            .waitingFor(Wait.forListeningPort())

    companion object {
        val POSTGRES_IMAGE: DockerImageName =
            DockerImageName
                .parse("postgres:15.7-alpine3.20")
                .asCompatibleSubstituteFor("postgres")
    }
}