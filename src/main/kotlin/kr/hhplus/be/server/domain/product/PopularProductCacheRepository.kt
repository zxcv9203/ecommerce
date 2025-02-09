package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.application.product.info.PopularProductInfo

interface PopularProductCacheRepository {
    fun save(products: List<PopularProductInfo>)

    fun findAll(): List<PopularProductInfo>
}
