package kr.hhplus.be.server.application.product.info

import kr.hhplus.be.server.domain.product.Product

data class ProductInfo(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Int,
)

data class ProductsInfo(
    val products: List<ProductInfo>,
    val hasNext: Boolean,
)

fun Product.toInfo() =
    ProductInfo(
        id = id,
        name = name,
        price = price,
        stock = stock,
    )
