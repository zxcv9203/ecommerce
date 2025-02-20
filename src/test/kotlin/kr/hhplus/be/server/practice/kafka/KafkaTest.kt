package kr.hhplus.be.server.practice.kafka

import kr.hhplus.be.server.template.IntegrationTest
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

class KafkaTest : IntegrationTest() {
    @Autowired
    private lateinit var testKafkaConsumer: TestKafkaConsumer

    @Autowired
    private lateinit var testKafkaProducer: TestKafkaProducer

    @Nested
    @DisplayName("카프카 테스트")
    inner class Practice {
        @Test
        @DisplayName("토픽을 발행하고 소비한다.")
        fun test_kafka() {
            val testMessage = "Hello Kafka!"

            testKafkaProducer.sendMessage("test-topic", testMessage)

            Awaitility.await().atMost(5, TimeUnit.SECONDS).until {
                testKafkaConsumer.receivedMessages.contains(testMessage)
            }

            assertThat(testKafkaConsumer.receivedMessages).contains(testMessage)
        }
    }
}

@Service
class TestKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    fun sendMessage(
        topic: String,
        message: String,
    ) {
        kafkaTemplate.send(topic, message)
    }
}

@Service
class TestKafkaConsumer {
    val receivedMessages = mutableListOf<String>()

    @KafkaListener(topics = ["test-topic"], groupId = "test-group")
    fun consume(record: ConsumerRecord<String, String>) {
        receivedMessages.add(record.value())
    }
}
