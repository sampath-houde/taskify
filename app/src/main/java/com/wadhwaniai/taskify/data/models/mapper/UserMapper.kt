package com.wadhwaniai.taskify.data.models.mapper

import com.wadhwaniai.taskify.data.models.entity.UserEntity
import com.wadhwaniai.taskify.data.models.remote.UserDTO

class UserMapper : Mapper<UserDTO, UserEntity> {
    override fun toDomainModel(network: UserDTO): UserEntity = UserEntity(
        username = network.username,
        email = network.email,
        profileImage = network.profile_image,
        hasCustomImage = network.profile_image != "",
    )


    override fun toDomainList(network: List<UserDTO>): List<UserEntity> =
        network.map { toDomainModel(it) }

    override fun toNetwork(domain: UserEntity): UserDTO = UserDTO(
        username = domain.username,
        email = domain.email,
        profile_image = domain.profileImage
    )
}