package dev.semo.kafkaeskadapter;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@DisplayName("Testing GlobalMessageTest")
@DirtiesContext
public class NumberPlateSenderTest {

    private static Logger log = LogManager.getLogger(NumberPlateSenderTest.class);

    @Autowired
    KafkaeskAdapterApplication kafkaeskAdapterApplication;

    @Autowired
    private NumberPlateSender numberPlateSender;

    private KafkaMessageListenerContainer<String, NumberPlate> container;
    private BlockingQueue<ConsumerRecord<String, NumberPlate>> records;

    private static final String SENDER_TOPIC = "numberplate_test_topic";

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, SENDER_TOPIC);

    @Before
    public void setUp() throws Exception {
        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);

        // create a Kafka consumer factory
        DefaultKafkaConsumerFactory<String, NumberPlate> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);

        // set the topic that needs to be consumed
        ContainerProperties containerProperties = new ContainerProperties(SENDER_TOPIC);

        // create a Kafka MessageListenerContainer
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        // create a thread safe queue to store the received message
        records = new LinkedBlockingQueue<>();

        // setup a Kafka message listener
        container.setupMessageListener((MessageListener<String, NumberPlate>) record -> {
            log.info("URGENT: Message Listener received message='{}'", record.toString());
            records.add(record);
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @DisplayName("Should send a Message to a Producer")
    @Test
    public void TestProducer() throws InterruptedException {
        //Test instance of Numberplate to send
        NumberPlate localNumberplate = new NumberPlate();
        byte[] bytes = "0x33".getBytes();
        localNumberplate.setImageBlob(bytes);
        localNumberplate.setNumberString("ABC123");
        log.info(localNumberplate.toString());

        //Send it
        numberPlateSender.sendNumberPlateMessage(localNumberplate);

        //Retrieve it
        ConsumerRecord<String, NumberPlate> received = records.poll(1, TimeUnit.SECONDS);
        log.info("Received the following content of ConsumerRecord: {}", received);

        if (received == null) {
            assert false;
        } else {
            NumberPlate retrNumberplate = received.value();
            Assert.assertEquals(retrNumberplate, localNumberplate);
        }
    }

    @After
    public void tearDown() {
        // stop the container
        container.stop();
    }

}
