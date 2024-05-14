package me.chnu.replication.domain.user.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import me.chnu.replication.domain.BaseEntity

@Entity
@Table(name = "users")
class User(
    var username: String,
    var password: String,
) : BaseEntity()