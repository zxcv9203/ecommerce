package kr.hhplus.be.server.api.product.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Long,
)

data class ProductsResponse(
    val products: List<ProductResponse>,
    val hasNext: Boolean,
)
