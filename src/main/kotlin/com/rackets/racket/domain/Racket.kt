package com.rackets.racket.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable
data class Racket(
    val id: Long = NEW_RACKET,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTennisPlayers: Int = 0,
    val image: String = DEFAULT_IMAGE,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false
) {
    companion object {
        const val NEW_RACKET = -1L
        const val DEFAULT_IMAGE = "https://www.tennis-warehouse.com/static/images/icons/NoImageIcon.jpg"
    }
}

class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toEpochSecond(ZoneOffset.UTC).toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.ofEpochSecond(decoder.decodeString().toLong(), 0, ZoneOffset.UTC)
    }
}