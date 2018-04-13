package dev.semo.kafkaeskadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class KafkaeskAdapterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext capc = SpringApplication.run(KafkaeskAdapterApplication.class, args);

        NumberPlateSender numberPlateProducer = capc.getBean(NumberPlateSender.class);
        numberPlateProducer.sendMessage("Hi there.");
    }
}
