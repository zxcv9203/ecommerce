package kr.hhplus.be.server.api.product.response

import kr.hhplus.be.server.domain.product.Product

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

fun Product.toResponse() =
    ProductResponse(
        id = id,
        name = name,
        price = price,
        stock = stock,
    )
