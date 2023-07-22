package com.example.services.data.user

import org.slf4j.LoggerFactory

import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Basic
import jakarta.persistence.Entity
import jakarta.persistence.Column
import jakarta.persistence.Transient
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.TableGenerator

import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.id.UUIDHexGenerator

import com.fasterxml.jackson.databind.ObjectMapper 

import java.util.UUID
import java.util.Date

import org.apache.commons.codec.binary.Hex

@Entity
class User(
    @Column(name="email", nullable=false, unique=true)
    var email: String,
    @Column(name="firstName", nullable=true)
    var firstName: String? = null,
    @Column(name="lastName", nullable=true)
    var lastName: String? = null
) {

    @Transient
    private val log = LoggerFactory.getLogger(User::class.java)

    @Id
    @Column(name="id", nullable=false)
    var id: ByteArray = Hex.decodeHex(UUID.randomUUID().toString().replace("-",""))

    @ColumnTransformer(read="HEX(token)", write="UNHEX(?)")
    @Column(name="token", nullable=true)
    var token: String = ""

    @CreationTimestamp
    @Column(name="createdDate", nullable=false)
    val createdDate: Date = Date()

    @UpdateTimestamp
    @Column(name="lastModifiedDate", nullable=false)
    val lastModifiedDate: Date = Date()

    init {
        log.info("User Initialized: " + this.email)
    }

    @Transient
    fun getId(): String = Hex.encodeHexString(this.id)

    @Transient
    override fun toString(): String = ObjectMapper().writeValueAsString(this)
}
