package dev.semo.kafkaeskadapter;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateDeserializer;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author semo, Artem Bilan With many kudos for Artem Bilan, solving a mean
 * bug.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KafkaeskAdapterApplication.class)
@DirtiesContext
public class NumberPlateSenderTest {

    private static final String SENDER_TOPIC = "numberplate_test_topic";

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, SENDER_TOPIC);

    private static Logger log = LogManager.getLogger(NumberPlateSenderTest.class);

    @Autowired
    KafkaeskAdapterApplication kafkaeskAdapterApplication;

    @Autowired
    private NumberPlateSender numberPlateSender;
    private KafkaMessageListenerContainer<String, NumberPlate> container;
    private BlockingQueue<ConsumerRecord<String, NumberPlate>> records;

    @BeforeClass
    public static void beforeSetUp() {
        System.setProperty("spring.kafka.producer.bootstrap-servers", embeddedKafka.getBrokersAsString());
    }

    @Before
    public void setUp() throws Exception {

        System.setProperty("spring.kafka.producer.bootstrap-servers", embeddedKafka.getBrokersAsString());

        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NumberPlateDeserializer.class);

        // create a Kafka consumer factory
        DefaultKafkaConsumerFactory<String, NumberPlate> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProperties);

        // set the topic that needs to be consumed
        ContainerProperties containerProperties = new ContainerProperties(SENDER_TOPIC);

        // create a Kafka MessageListenerContainer
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        // create a thread safe queue to store the received message
        records = new LinkedBlockingQueue<>();

        // setup a Kafka message listener
        container.setupMessageListener((MessageListener<String, NumberPlate>) record -> {
            log.info("Message Listener received message='{}'", record.toString());
            records.add(record);
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    // @DisplayName("Should send a Message to a Producer and retrieve it")
    @Test
    public void testProducer() throws InterruptedException {
        // Test instance of Numberplate to send
        NumberPlate localNumberplate = new NumberPlate();
        byte[] bytes = "0x33".getBytes();
        localNumberplate.setImageBlob(bytes);
        localNumberplate.setNumberString("ABC123");
        log.info(localNumberplate.toString());

        // Send it
        numberPlateSender.sendNumberPlateMessage(localNumberplate);

        // Retrieve it
        ConsumerRecord<String, NumberPlate> received = records.poll(1, TimeUnit.SECONDS);
        log.info("Received the following content of ConsumerRecord: {}", received);

        System.out.println("HEADERS  " + received.headers().toString());

        // Assert it
        if (received == null) {
            assert false;
        } else {
            NumberPlate retrNumberplate = received.value();
            assertEquals(retrNumberplate, localNumberplate);
        }
    }

    @After
    public void tearDown() {
        // stop the container
        records.clear();
        records = null;
        container.stop();
        container = null;
    }

}
