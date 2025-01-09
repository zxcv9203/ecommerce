package kr.hhplus.be.server.stub

import kr.hhplus.be.server.domain.product.Product

object ProductFixture {
    fun create(
        id: Long = 1L,
        name: String = "product",
        price: Long = 1000L,
        description: String = "description",
        stock: Int = 1,
    ) = Product(
        id = id,
        name = name,
        price = price,
        description = description,
        stock = stock,
    )
}
