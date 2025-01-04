package kr.hhplus.be.server.api.product.response

data class PopularProductResponse(
    val id: Long,
    val rank: Int,
    val name: String,
    val price: Long,
    val totalSales: Long,
)

data class PopularProductsResponse(
    val products: List<PopularProductResponse>,
)
