package dev.semo.kafkaeskadapter;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.net.SocketException;
import java.net.UnknownHostException;

@ComponentScan
@SpringBootApplication
public class KafkaeskAdapterApplication {

    private static Logger log = LogManager.getLogger(KafkaeskAdapterApplication.class);

    public static void main(String[] args) throws UnknownHostException, SocketException {
        log.info("Starting Kafkaesk Application");
        ConfigurableApplicationContext capc = SpringApplication.run(KafkaeskAdapterApplication.class, args);

        NumberPlateSender numberPlateProducer = capc.getBean(NumberPlateSender.class);
        
        NumberPlate n1 = new NumberPlate();
        n1.setNumberString("123");
        byte[] imageBlob1 = {123};
		n1.setImageBlob(imageBlob1);

		NumberPlate n2 = new NumberPlate();
		n2.setNumberString("345");
		byte[] imageBlob2 = {123};
		n2.setImageBlob(imageBlob2);

		NumberPlate n3 = new NumberPlate();
		n3.setNumberString("567");
		byte[] imageBlob3 = {123};
		n3.setImageBlob(imageBlob3);
        
        numberPlateProducer.sendNumberPlateMessage(n1);
        numberPlateProducer.sendNumberPlateMessage(n2);
        numberPlateProducer.sendNumberPlateMessage(n3);
        numberPlateProducer.sendNumberPlateMessage(n1);
        numberPlateProducer.sendNumberPlateMessage(n2);
        numberPlateProducer.sendNumberPlateMessage(n3);
        numberPlateProducer.sendNumberPlateMessage(n1);
        numberPlateProducer.sendNumberPlateMessage(n2);
        numberPlateProducer.sendNumberPlateMessage(n3);
        numberPlateProducer.sendNumberPlateMessage(n1);
        numberPlateProducer.sendNumberPlateMessage(n2);
        numberPlateProducer.sendNumberPlateMessage(n3);
        numberPlateProducer.sendNumberPlateMessage(n1);
        numberPlateProducer.sendNumberPlateMessage(n2);
    }
}
