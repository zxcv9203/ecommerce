package kr.hhplus.be.server.domain.order

import jakarta.persistence.*
import kr.hhplus.be.server.common.model.BaseEntity
import kr.hhplus.be.server.domain.product.Product
import org.hibernate.annotations.Comment

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Comment("주문")
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Comment("상품")
    val product: Product,
    @Column(name = "count", nullable = false)
    @Comment("상품 수량")
    val count: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
