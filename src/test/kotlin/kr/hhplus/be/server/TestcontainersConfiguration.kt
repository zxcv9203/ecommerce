package kr.hhplus.be.server

import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration
class TestcontainersConfiguration {
    @PreDestroy
    fun preDestroy() {
        if (mySqlContainer.isRunning) mySqlContainer.stop()
        if (redisContainer.isRunning) redisContainer.stop()
    }

    companion object {
        val mySqlContainer: MySQLContainer<*> =
            MySQLContainer(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("hhplus")
                .withUsername("test")
                .withPassword("test")
                .apply {
                    start()
                }
        val redisContainer: GenericContainer<*> =
            GenericContainer<Nothing>(DockerImageName.parse("redis:7.4.2"))
                .apply {
                    withExposedPorts(6379)
                    start()
                }

        init {
            System.setProperty(
                "spring.datasource.url",
                mySqlContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC",
            )
            System.setProperty("spring.datasource.username", mySqlContainer.username)
            System.setProperty("spring.datasource.password", mySqlContainer.password)
            System.setProperty("spring.datasource.hikari.maximum-pool-size", "3")
            System.setProperty("spring.datasource.hikari.connection-timeout", "10000")
            System.setProperty("spring.datasource.hikari.max-lifetime", "60000")
        }
    }
}
