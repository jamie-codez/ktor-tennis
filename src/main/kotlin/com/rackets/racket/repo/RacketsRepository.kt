package com.rackets.racket.repo

import com.rackets.racket.domain.Racket
import kotlinx.coroutines.flow.Flow

interface RacketsRepository {
    suspend fun findAll(): Flow<Racket>
    suspend fun findById(id: Long): Racket?
    suspend fun findAllPageable(page: Int, size: Int): Flow<Racket>
    suspend fun findByBrand(brand: String): Flow<Racket>
    suspend fun save(racket: Racket): Racket
    suspend fun saveAll(rackets: List<Racket>): Flow<Racket>
    suspend fun update(id: Long, racket: Racket): Racket
    suspend fun deleteById(id: Long): Boolean
    suspend fun deleteAll(): Boolean
}