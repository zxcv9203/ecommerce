package kr.hhplus.be.server.application.product.info

data class PopularProductInfo(
    val id: Long,
    val name: String,
    val price: Long,
    val totalSales: Long,
)

data class PopularProductsInfo(
    val products: List<PopularProductInfo>,
)
