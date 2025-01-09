package kr.hhplus.be.server.api.product

import kr.hhplus.be.server.api.product.response.ProductResponse
import kr.hhplus.be.server.common.constant.SuccessCode
import kr.hhplus.be.server.infrastructure.persistence.product.JpaProductRepository
import kr.hhplus.be.server.stub.ProductFixture
import kr.hhplus.be.server.template.IntegrationTest
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProductControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var jpaProductRepository: JpaProductRepository

    @BeforeEach
    fun setUp() {
        val products =
            (1..11L)
                .map {
                    ProductFixture.create(
                        name = "상품 $it",
                        price = 1000 * it,
                        stock = 10,
                    )
                }
        jpaProductRepository.saveAll(products)
    }

    @Nested
    @DisplayName("상품 목록 조회 API")
    inner class FindAll {
        @Test
        @DisplayName("[성공]상품 목록을 조회한다. (다음 페이지가 존재하는 경우)")
        fun testFindAllNextPage() {
            mockMvc
                .perform(
                    get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_QUERY.message))
                .andExpect(jsonPath("$.data.products").isArray)
                .andExpect(jsonPath("$.data.products", Matchers.hasSize<ProductResponse>(10)))
                .andExpect(jsonPath("$.data.hasNext").value(true))
        }

        @Test
        @DisplayName("[성공]상품 목록을 조회한다. (다음 페이지가 존재하지 않는 경우)")
        fun findAllLastPage() {
            mockMvc
                .perform(
                    get("/api/v1/products")
                        .param("page", "1")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_QUERY.status.value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_QUERY.message))
                .andExpect(jsonPath("$.data.products").isArray)
                .andExpect(jsonPath("$.data.products", Matchers.hasSize<ProductResponse>(1)))
                .andExpect(jsonPath("$.data.hasNext").value(false))
        }
    }
}
