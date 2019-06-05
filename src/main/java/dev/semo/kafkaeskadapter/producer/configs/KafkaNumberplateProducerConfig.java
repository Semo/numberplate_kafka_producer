package dev.semo.kafkaeskadapter.producer.configs;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaNumberplateProducerConfig {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.reconnect.backoff}")
    private String backoffTime;

    @Value(value = "${spring.kafka.producer.batch-size}")
    private int batchsize;

    @Value(value = "${spring.kafka.producer.linger-ms-config}")
    private int linger;

    @Bean
    public ProducerFactory<String, NumberPlate> numberPlateProducerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NumberPlateSerializer.class);
        configProps.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, backoffTime);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchsize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        try {
            configProps.put(ProducerConfig.CLIENT_ID_CONFIG, NetworkInterface.getNetworkInterfaces()
                    .nextElement()
                    .getInetAddresses().nextElement().toString());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, NumberPlate> kafkaTemplate() {
        return new KafkaTemplate<>(numberPlateProducerFactory());
    }

    @Bean
    public NumberPlate numberPlate() {
        return new NumberPlate();
    }
}
