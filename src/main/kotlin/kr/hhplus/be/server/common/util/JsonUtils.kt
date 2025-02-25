package kr.hhplus.be.server.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

fun Any.toJson(): String = objectMapper.writeValueAsString(this)
