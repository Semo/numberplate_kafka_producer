package dev.semo.kafkaeskadapter.producer;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NumberPlateSender {

    private static Logger log = LogManager.getLogger(NumberPlateSender.class);

    @Autowired
    private KafkaTemplate<String, NumberPlate> numberplateKafkaTemplate;

    @Value(value = "${numberplate.topic.name}")
    private String numberPlateTopicName;

    public void sendNumberPlateMessage(NumberPlate numberPlate) {
        log.info("sending payload='{}' to topic='{}'", numberPlate.toString(), numberPlateTopicName);
        numberplateKafkaTemplate.send(numberPlateTopicName, numberPlate);
    }

}
