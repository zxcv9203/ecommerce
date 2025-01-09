package kr.hhplus.be.server.domain.product

import jakarta.persistence.*
import kr.hhplus.be.server.common.constant.ErrorCode
import kr.hhplus.be.server.common.exception.BusinessException
import kr.hhplus.be.server.common.model.BaseEntity
import org.hibernate.annotations.Comment

@Entity
@Table(name = "products")
class Product(
    @Column(name = "name", nullable = false)
    @Comment("상품 이름")
    val name: String,
    @Column(name = "description", nullable = false)
    @Comment("상품 설명")
    val description: String,
    @Column(name = "price", nullable = false)
    @Comment("상품 가격")
    val price: Long,
    @Column(name = "stock", nullable = false)
    @Comment("상품 재고")
    var stock: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun ensureAvailableStock(quantity: Int) {
        if (stock < quantity) throw BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK)
    }
}
