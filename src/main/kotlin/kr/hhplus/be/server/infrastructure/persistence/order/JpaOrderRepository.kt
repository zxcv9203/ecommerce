package kr.hhplus.be.server.infrastructure.persistence.order

import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.data.jpa.repository.JpaRepository

interface JpaOrderRepository :
    OrderRepository,
    JpaRepository<Order, Long>
