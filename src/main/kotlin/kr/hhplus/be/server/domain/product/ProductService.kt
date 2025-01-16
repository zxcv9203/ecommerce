package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.application.product.info.PopularProductInfo
import kr.hhplus.be.server.application.product.info.ProductInfo
import kr.hhplus.be.server.application.product.info.toInfo
import kr.hhplus.be.server.application.order.command.OrderItemCommand
import kr.hhplus.be.server.application.order.command.toSortedProductIds
import kr.hhplus.be.server.application.product.info.OrderItemInfo
import kr.hhplus.be.server.application.product.info.toOrderProductInfo
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findAll(pageable: Pageable): Slice<ProductInfo> =
        productRepository
            .findAll(pageable)
            .map { it.toInfo() }

    fun findOrderableProductByIds(items: List<OrderItemCommand>): List<OrderItemInfo> {
        val productIds = items.map { it.productId }

        val products = productRepository.findAllByIds(productIds)
        if (products.size != productIds.size) {
            throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        }

        return items.map { item ->
            products
                .first { it.id == item.productId }
                .also { it.ensureAvailableStock(item.quantity) }
                .toOrderProductInfo(item.quantity)
        }
    }

    @Transactional
    fun reduceStock(orderItems: List<OrderItemCommand>) {
        val productIds = orderItems.toSortedProductIds()
        val products = productRepository.findAllByIdsWithLock(productIds)

        products.forEach { product ->
            val orderItem = orderItems.first { it.productId == product.id }
            product.reduceStock(orderItem.quantity)
        }

        productRepository.saveAll(products)
    }

    fun findPopularProducts(): List<PopularProductInfo> = productRepository.findPopularProducts()
}
