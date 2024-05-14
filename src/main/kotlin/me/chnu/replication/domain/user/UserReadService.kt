package me.chnu.replication.domain.user

import me.chnu.replication.domain.user.entity.UserRepository
import me.chnu.replication.util.annotation.ReadService
import org.springframework.data.repository.findByIdOrNull

@ReadService
class UserReadService(private val userRepository: UserRepository) {

    fun get(id: Long) = userRepository.findByIdOrNull(id)
        ?: throw IllegalArgumentException("User with id $id not found")

    fun getAll() = userRepository.findAll()
}