package dev.semo.kafkaeskadapter;

import dev.semo.kafkaeskadapter.models.channels.numberplates.NumberPlate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GlobalMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, NumberPlate> numberplateKafkaTemplate;

    @Value(value = "${message.topic.name}")
    private String topicName;

    @Value(value = "${partitioned.topic.name}")
    private String partionedTopicName;

    @Value(value = "${numberplate.topic.name}")
    private String numberPlateTopicName;


    public void sendMessage(String message) {
        kafkaTemplate.send(topicName, message);
    }

    public void sendMessageToPartion(String message, String partition) {
        kafkaTemplate.send(partionedTopicName, partition, message);
    }

    public void sendNumberPlateMessage(NumberPlate numberPlate) {
        numberplateKafkaTemplate.send(numberPlateTopicName, numberPlate);
    }

}
