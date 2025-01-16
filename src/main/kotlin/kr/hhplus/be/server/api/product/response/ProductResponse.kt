package kr.hhplus.be.server.api.product.response

import kr.hhplus.be.server.application.product.info.ProductInfo
import kr.hhplus.be.server.application.product.info.ProductsInfo

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Int,
)

data class ProductsResponse(
    val products: List<ProductResponse>,
    val hasNext: Boolean,
)

fun ProductInfo.toResponse() =
    ProductResponse(
        id = id,
        name = name,
        price = price,
        stock = stock,
    )

fun ProductsInfo.toResponse() =
    ProductsResponse(
        products = products.map { it.toResponse() },
        hasNext = hasNext,
    )
