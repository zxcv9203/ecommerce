package kr.hhplus.be.server.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.domain.product.PopularProductCacheRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisPopularProductCacheRepository(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : PopularProductCacheRepository {
    override fun save(products: List<PopularProductInfo>) {
        val json = objectMapper.writeValueAsString(products)
        redisTemplate.opsForValue()[KEY] = json
    }

    override fun findAll(): List<PopularProductInfo> =
        redisTemplate
            .opsForValue()[KEY]
            ?.let { objectMapper.readValue(it, Array<PopularProductInfo>::class.java).toList() }
            ?: throw NoSuchElementException("캐시 데이터가 존재하지 않습니다.")

    companion object {
        private const val KEY = "popular-products"
    }
}
