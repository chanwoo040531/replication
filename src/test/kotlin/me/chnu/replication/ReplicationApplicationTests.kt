package me.chnu.replication

import me.chnu.replication.domain.user.UserReadService
import me.chnu.replication.domain.user.UserWriteService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReplicationApplicationTests {
    @Autowired
    lateinit var userReadService: UserReadService

    @Autowired
    lateinit var userWriteService: UserWriteService

    @Test
    fun contextLoads() {
    }

    @Test
    fun `create user`() {
        userWriteService.create("test", "test")
    }

    @Test
    fun `get user`() {
        userReadService.getAll()
    }
}
