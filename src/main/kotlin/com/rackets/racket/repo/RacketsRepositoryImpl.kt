package com.rackets.racket.repo

import com.rackets.racket.domain.Racket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class RacketsRepositoryImpl : RacketsRepository {
    private val rackets = mutableListOf<Racket>()
    private val logger = KotlinLogging.logger {}

    init {
        rackets.add(Racket(1, "Wilson", "Pro Staff 97", 250.0, 0))
        rackets.add(Racket(2, "Babolat", "Pure Drive", 250.0, 0))
        rackets.add(Racket(3, "Head", "Graphene 360 Speed Pro", 250.0, 0))
        rackets.add(Racket(4, "Yonex", "EZONE 98", 250.0, 0))
        rackets.add(Racket(5, "Prince", "Phantom 100", 250.0, 0))
    }

    override suspend fun findAll(): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findAll()" }
        return@withContext rackets.asFlow()
    }

    override suspend fun findById(id: Long): Racket? = withContext(Dispatchers.IO) {
        logger.debug { "findById($id)" }
        return@withContext rackets.find { it.id == id }
    }

    override suspend fun findAllPageable(page: Int, size: Int): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable($page, $size)" }
        val limit = if (size > rackets.size) rackets.size else size
        val offset = (page - 1) * size
        return@withContext rackets.subList(offset, offset + limit).asFlow()
    }

    override suspend fun findByBrand(brand: String): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findByBrand($brand)" }
        return@withContext rackets.filter { it.brand == brand }.asFlow()
    }

    override suspend fun save(racket: Racket): Racket = withContext(Dispatchers.IO) {
        logger.debug { "save($racket)" }
        val id = if (rackets.isEmpty()) 1 else rackets.last().id + 1
        val newRacket = racket.copy(id = id)
        rackets.add(newRacket)
        return@withContext newRacket
    }

    override suspend fun saveAll(rackets: List<Racket>): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "saveAll($rackets)" }
        rackets.forEach { save(it) }
        return@withContext rackets.asFlow()
    }

    override suspend fun update(id: Long, racket: Racket): Racket = withContext(Dispatchers.IO) {
        logger.debug { "update($id, $racket)" }
        val index = rackets.indexOfFirst { it.id == id }
        if (index == -1) throw IllegalArgumentException("Racket with id $id not found")
        rackets[index] = racket.copy(id = id)
        return@withContext racket
    }

    override suspend fun deleteById(id: Long): Boolean = withContext(Dispatchers.IO) {
        logger.debug { "deleteById($id)" }
        val index = rackets.indexOfFirst { it.id == id }
        if (index == -1) return@withContext false
        rackets.removeAt(index)
        return@withContext true
    }

    override suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        logger.debug { "deleteAll()" }
        rackets.clear()
        return@withContext true
    }
}