package com.rba.CreditCardService.kafka.producer;

import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * To create messages, we first need to configure a ProducerFactory. This sets the strategy for creating Kafka Producer instances
 */
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, CardStatusUpdateMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); //to set the server address on which Kafka is running
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //to deserialize the and value from the Kafka Queue
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); //to deserialize the value from the Kafka Queue

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * We need a KafkaTemplate, which wraps a Producer instance and
     * provides convenience methods for sending messages to Kafka topics.
     *
     * Producer instances are thread safe. So, using a single instance throughout an application context will give higher performance.
     * Consequently, KakfaTemplate instances are also thread safe, and use of one instance is recommended
     */
    @Bean
    public KafkaTemplate<String, CardStatusUpdateMessage> kafkaTemplate(ProducerFactory<String, CardStatusUpdateMessage> producerFactory) {
        return new KafkaTemplate<>(producerFactory());
    }
}
