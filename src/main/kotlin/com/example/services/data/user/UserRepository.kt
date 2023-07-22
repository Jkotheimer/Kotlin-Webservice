package com.example.services.data.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, String> {
	fun findByEmail(email: String): User
	fun findByLastName(lastName: String): Iterable<User>
}
