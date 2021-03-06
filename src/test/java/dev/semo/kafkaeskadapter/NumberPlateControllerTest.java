package dev.semo.kafkaeskadapter;


import dev.semo.kafkaeskadapter.controllers.NumberPlateController;
import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateDeserializer;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KafkaeskAdapterApplication.class)
@AutoConfigureMockMvc
@DirtiesContext
public class NumberPlateControllerTest {

    private static final String SENDER_TOPIC = "sender.t";
    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, SENDER_TOPIC);

    private static Logger log = LogManager.getLogger(NumberPlateSenderTest.class);

    @Autowired
    KafkaeskAdapterApplication kafkaeskAdapterApplication;
    @Autowired
    NumberPlateController npc;
    @Autowired
    private NumberPlateSender numberPlateSender;
    private KafkaMessageListenerContainer<String, NumberPlate> container;
    private BlockingQueue<ConsumerRecord<String, NumberPlate>> records;
    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void beforeSetUp() {
        System.out.println("##########################################");
        System.out.println(embeddedKafka.getBrokersAsString());

        System.setProperty("spring.kafka.producer.bootstrap-servers", embeddedKafka.getBrokersAsString());
    }

    @Before
    public void setUp() throws Exception {
        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NumberPlateDeserializer.class);

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
            log.info("Message Listener received message='{}'", record.toString());
            records.add(record);
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @Test
    public void shouldPostNumberPlateDirectly() {
        try {
            File tempFile = File.createTempFile("temp-file-name", ".tmp");
            byte[] b = new byte[(int) tempFile.length()];
            MockMultipartFile mockFile = new MockMultipartFile("data", tempFile.getName(), "text/plain", b);
            npc.postNumberPlate("ABC123", mockFile);

            //Retrieve it
            ConsumerRecord<String, NumberPlate> received = null;
            try {
                received = records.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Received the following content of ConsumerRecord: {}", received);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This fancy test mocks a webservice request.
     */
    @Test
    public void shouldPostPostNumberPlateSuccessfully() {
        try {
            File tempFile = File.createTempFile("another_Temp_File", ".tmp");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));
            writer.append("Hallo Welt.");
            writer.close();
            byte[] b = new byte[(int) tempFile.length()];
            MockMultipartFile mockFile = new MockMultipartFile("image", tempFile.getName(), "text/plain", tempFile.getName().getBytes());

            mvc.perform(
                    MockMvcRequestBuilders.multipart("/data")
                            .file(mockFile)
                            .param("numplate", "ABC")
            )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is(200));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldReturnBadRequestWithoutUploadFile() throws Exception {
        MockMultipartFile mockFile;
        mvc.perform(
                post("/data")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content("blabla")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(JUnitMatchers.containsString("Required String parameter 'numplate' is not present")));
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
