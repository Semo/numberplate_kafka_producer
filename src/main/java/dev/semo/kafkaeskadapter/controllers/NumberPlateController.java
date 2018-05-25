package dev.semo.kafkaeskadapter.controllers;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;

@RestController
@MultipartConfig(fileSizeThreshold = 5500)
public class NumberPlateController {

    private static Logger log = LogManager.getLogger(NumberPlateController.class);

    @Autowired
    private NumberPlateSender numberPlateSender;

    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    public ResponseEntity<String> getSimpleOK() {
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity<String> receive(@RequestParam("sample") String sample) {
    	
    	log.info("Got a sample message: {}", sample);
    	
    	ResponseEntity<String> entity = new ResponseEntity<>(HttpStatus.ACCEPTED);
    	
    	String body = entity.getBody();
    	 MediaType contentType = entity.getHeaders().getContentType();
    	 HttpStatus statusCode = entity.getStatusCode();
    	
    	 System.out.println(contentType);
    	 System.out.println(statusCode);
    	 System.out.println(body);
    	 
    	return entity;
    }

    @PostMapping("/data/test")
    public ResponseEntity<String> postStuffTtest(@RequestParam("numplate") String plate, @RequestParam
            ("image") String bas64Image) {
        log.info("Received a plate: {}", plate + " " + bas64Image);
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }



    @PostMapping("/data")
    public ResponseEntity<String> postNumberPlate(@RequestParam("numplate") String plate, @RequestParam("image")
            MultipartFile
            multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return new ResponseEntity<String>("File missing.", HttpStatus.BAD_REQUEST);
        }

        NumberPlate np = new NumberPlate();
        log.trace("Received a plate: {}", np.getNumberString());
        try {
            np.setImageBlob(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        np.setNumberString(plate);
        numberPlateSender.sendNumberPlateMessage(np);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/data/unserialized", method = RequestMethod.POST)
    public ResponseEntity<String> postNumberPlateUnserialized(@RequestParam("numplate") String plate, @RequestParam
            ("image") String
             base64EncodedImage) {
        NumberPlate np = new NumberPlate();
        np.setImageBlob(Base64.getDecoder().decode(base64EncodedImage));
        np.setNumberString(plate);
        log.trace("Received a plate: {}", np.getNumberString() + " " + np.getImageBlob().toString());

        numberPlateSender.sendNumberPlateMessage(np);

        return new ResponseEntity<String>(HttpStatus.OK);
    }



    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
