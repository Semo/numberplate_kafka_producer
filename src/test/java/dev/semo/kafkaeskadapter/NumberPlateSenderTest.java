package dev.semo.kafkaeskadapter;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;

@DisplayName("Testing GlobalMessageTest")
public class NumberPlateSenderTest {

    private MockProducer<String, String> mockProducer;

    @Before
    public void setUp() {
        mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
    }

    @DisplayName("Should send a Message to a Producer")
    @Test
    public void TestProducer() throws IOException {
        NumberPlateSender numberPlateSender;

    }

}
