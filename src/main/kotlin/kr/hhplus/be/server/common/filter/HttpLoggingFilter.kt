package kr.hhplus.be.server.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class HttpLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(HttpLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        try {
            filterChain.doFilter(requestWrapper, responseWrapper)

            val method = requestWrapper.method
            val uri = requestWrapper.requestURI
            val headers =
                requestWrapper.headerNames
                    .toList()
                    .joinToString { "$it: ${requestWrapper.getHeader(it)}" }
            val requestBody = String(requestWrapper.contentAsByteArray, Charsets.UTF_8).ifBlank { "No Body" }
            log.debug("HTTP Request: [Method: $method, URI: $uri, Headers: $headers, Body: $requestBody]")

            val status = responseWrapper.status
            val responseBody = String(responseWrapper.contentAsByteArray, Charsets.UTF_8).ifBlank { "No Body" }
            log.debug("HTTP Response: [Status: $status, Body: $responseBody]")
        } finally {
            responseWrapper.copyBodyToResponse()
        }
    }
}
