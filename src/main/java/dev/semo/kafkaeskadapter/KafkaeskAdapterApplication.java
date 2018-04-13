package dev.semo.kafkaeskadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class KafkaeskAdapterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext capc = SpringApplication.run(KafkaeskAdapterApplication.class, args);

        GlobalMessageProducer producer = capc.getBean(GlobalMessageProducer.class);
        producer.sendMessage("Hi there.");
    }
}
