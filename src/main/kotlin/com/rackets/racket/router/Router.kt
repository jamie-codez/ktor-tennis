package com.rackets.racket.router

import com.rackets.racket.domain.Racket
import com.rackets.racket.repo.RacketsRepository
import com.rackets.racket.repo.RacketsRepositoryImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Serializable
import mu.KotlinLogging

private const val RACKETS_ENDPOINT = "/api/v1/rackets"
private val logger = KotlinLogging.logger {}
private val racketsRepository: RacketsRepository = RacketsRepositoryImpl()
fun Application.racketRoutes() {
    routing {
        route(RACKETS_ENDPOINT) {
            get {
                logger.info("GET $RACKETS_ENDPOINT -> get all rackets")
                // Query parameters
                val page = call.request.queryParameters["page"]?.toIntOrNull()
                val size = call.request.queryParameters["size"]?.toIntOrNull()

                if (page != null && page > 0 && size != null && size > 0) {
                    logger.info("GET $RACKETS_ENDPOINT?page=$page&size=$size")
                    racketsRepository.findAllPageable(page, size).let { rackets ->
                        val payload = PageResponse(page, size, rackets.toList())
                        call.respond(HttpStatusCode.OK, payload)
                    }
                } else {
                    logger.info("GET $RACKETS_ENDPOINT -> get all rackets")
                    racketsRepository.findAll().let { rackets ->
                        call.respond(HttpStatusCode.OK, rackets.toList())
                    }
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                logger.info("GET $RACKETS_ENDPOINT/${id} -> get racket by id")
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                } else {
                    racketsRepository.findById(id).let { racket ->
                        if (racket == null) {
                            call.respond(HttpStatusCode.NotFound, "Racket with id $id not found")
                        } else {
                            call.respond(HttpStatusCode.OK, racket)
                        }
                    }
                }
            }

            get("/brand/{brand}") {
                val brand = call.parameters["brand"]
                logger.info("GET $RACKETS_ENDPOINT/brand/${brand} -> get rackets by brand")
                if (brand == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid brand")
                } else {
                    racketsRepository.findByBrand(brand).let { rackets ->
                        call.respond(HttpStatusCode.OK, rackets.toList())
                    }
                }
            }
            post {
                val racket = call.receive<Racket>()
                logger.info("POST $RACKETS_ENDPOINT -> save racket")
                racketsRepository.save(racket).let { savedRacket ->
                    call.respond(HttpStatusCode.Created, savedRacket)
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                val racket = call.receive<Racket>()
                logger.info("PUT $RACKETS_ENDPOINT/${id} -> update racket")
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                } else {
                    racketsRepository.update(id, racket).let { updatedRacket ->
                        call.respond(HttpStatusCode.OK, updatedRacket)
                    }
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                logger.info("DELETE $RACKETS_ENDPOINT/${id} -> delete racket by id")
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                } else {
                    racketsRepository.deleteById(id).let { deleted ->
                        if (deleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Racket with id $id not found")
                        }
                    }
                }
            }

            delete {
                logger.info("DELETE $RACKETS_ENDPOINT -> delete all rackets")
                racketsRepository.deleteAll().let { deleted ->
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No rackets found")
                    }
                }
            }
        }
    }
}


@Serializable
data class PageResponse<T>(
    val page: Int,
    val size: Int,
    val data: List<T>
)
