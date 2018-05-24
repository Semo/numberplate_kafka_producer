package dev.semo.kafkaeskadapter.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.semo.kafkaeskadapter.models.NumberPlate;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class NumberPlateSerializer implements Serializer<NumberPlate> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {}

    /**
     * Required Serializer to work with custom objects.
     * @param s
     * @param numberPlate
     * @return
     */
    @Override
    public byte[] serialize(String s, NumberPlate numberPlate) {
        byte[] serializedBytes = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            serializedBytes = mapper.writeValueAsString(numberPlate).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializedBytes;
    }

    @Override
    public void close() {
    }
}
