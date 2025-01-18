package kr.hhplus.be.server.common.config

import kr.hhplus.be.server.common.interceptor.UserAuthenticationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    val userAuthenticationInterceptor: UserAuthenticationInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(userAuthenticationInterceptor)
            .addPathPatterns("/api/v1/users/**")
            .addPathPatterns("/api/v1/orders")
            .addPathPatterns("/api/v1/payments")
    }
}
