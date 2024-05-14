package me.chnu.replication.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class TransactionTests {
    @Autowired
    lateinit var router: DataSourceConfig.DataSourceRouter

    @Test
    @Transactional(readOnly = false)
    fun `datasource should be primary`() {
        val key = router.determineCurrentLookupKey()
        assertThat(key).isEqualTo(DataSourceConfig.DataSourceType.PRIMARY)
    }

    @Test
    @Transactional(readOnly = true)
    fun `datasource should be standby`() {
        val key = router.determineCurrentLookupKey()
        assertThat(key).isEqualTo(DataSourceConfig.DataSourceType.STANDBY)
    }
}