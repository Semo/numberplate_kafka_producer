package dev.semo.kafkaeskadapter.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.semo.kafkaeskadapter.models.NumberPlate;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class NumberPlateDeserializer implements Deserializer<NumberPlate> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public NumberPlate deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        NumberPlate numberPlate = null;

        try {
            numberPlate = mapper.readValue(bytes, NumberPlate.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numberPlate;
    }

    @Override
    public void close() {
    }
}
