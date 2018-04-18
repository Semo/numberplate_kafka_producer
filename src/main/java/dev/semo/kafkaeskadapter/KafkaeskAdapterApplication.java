package dev.semo.kafkaeskadapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("dev.semo.kafkaeskadapter.controllers")
@SpringBootApplication
public class KafkaeskAdapterApplication {

    private static Logger log = LogManager.getLogger(KafkaeskAdapterApplication.class);


    public static void main(String[] args) {
        log.info("Starting Kafkaesk Application");
        ConfigurableApplicationContext capc = SpringApplication.run(KafkaeskAdapterApplication.class, args);

//        NumberPlateSender numberPlateProducer = capc.getBean(NumberPlateSender.class);
    }
}
