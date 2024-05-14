package me.chnu.replication.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories(basePackages = ["me.chnu.replication.domain"])
class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    fun primaryDataSource(): DataSource = DataSourceBuilder
        .create()
        .type(HikariDataSource::class.java)
        .build()

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.standby")
    fun standbyDataSource(): DataSource = DataSourceBuilder
        .create()
        .type(HikariDataSource::class.java)
        .build()

    @Bean
    @DependsOn("primaryDataSource", "standbyDataSource")
    fun routeDataSource() = DataSourceRouter().also {
        val primaryDataSource = primaryDataSource()
        val standbyDataSource = standbyDataSource()

        HashMap<Any, Any>().apply {
            put(DataSourceType.PRIMARY, primaryDataSource)
            put(DataSourceType.STANDBY, standbyDataSource)
        }.let(it::setTargetDataSources)
        it.setDefaultTargetDataSource(primaryDataSource)
    }

    @Bean
    @Primary
    @DependsOn("routeDataSource")
    fun dataSource() = LazyConnectionDataSourceProxy(routeDataSource())

    class DataSourceRouter : AbstractRoutingDataSource() {
        public override fun determineCurrentLookupKey() =
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly())
                DataSourceType.STANDBY else DataSourceType.PRIMARY
    }

    enum class DataSourceType {
        PRIMARY,
        STANDBY
    }
}



