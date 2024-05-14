package me.chnu.replication.domain.user

import me.chnu.replication.domain.user.entity.User
import me.chnu.replication.domain.user.entity.UserRepository
import me.chnu.replication.util.annotation.WriteService

@WriteService
class UserWriteService(private val userRepository: UserRepository) {

    fun create(username: String, password: String) = userRepository.save(User(username, password))
}