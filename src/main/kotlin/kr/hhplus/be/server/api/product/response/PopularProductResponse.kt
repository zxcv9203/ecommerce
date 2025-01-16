package kr.hhplus.be.server.api.product.response

import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.application.product.info.PopularProductsInfo

data class PopularProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val totalSales: Long,
)

data class PopularProductsResponse(
    val products: List<PopularProductResponse>,
)

fun PopularProductInfo.toResponse() =
    PopularProductResponse(
        id = id,
        name = name,
        price = price,
        totalSales = totalSales,
    )

fun PopularProductsInfo.toResponse() =
    PopularProductsResponse(
        products = products.map { it.toResponse() },
    )
