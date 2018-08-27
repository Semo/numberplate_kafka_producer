package dev.semo.kafkaeskadapter.producer;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class NumberPlateSender {

    private static Logger log = LogManager.getLogger(NumberPlateSender.class);

    @Autowired
    private KafkaTemplate<String, NumberPlate> numberplateKafkaTemplate;

    @Value(value = "${numberplate.topic.name}")
    private String numberPlateTopicName;

    public void sendNumberPlateMessage(NumberPlate numberPlate) {
        log.info("sending payload='{}' to topic='{}'", numberPlate.toString(), numberPlateTopicName);
        numberplateKafkaTemplate.send(numberPlateTopicName, numberPlate).addCallback(new ListenableFutureCallback<SendResult<String, NumberPlate>>() {

            @Override
            public void onFailure(final Throwable throwable) {
                log.error("Could not send message: " + numberPlate.toString() + " because of: {}", throwable);
            }

            @Override
            public void onSuccess(SendResult<String, NumberPlate> stringNumberPlateSendResult) {
                log.info("Sent message with offset: {}", stringNumberPlateSendResult.getRecordMetadata().offset());
            }
        });
    }

}
