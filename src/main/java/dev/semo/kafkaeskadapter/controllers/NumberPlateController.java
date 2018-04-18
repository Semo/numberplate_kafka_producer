package dev.semo.kafkaeskadapter.controllers;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@MultipartConfig(fileSizeThreshold = 5500)
public class NumberPlateController {

    private static Logger log = LogManager.getLogger(NumberPlateController.class);


    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    public ResponseEntity getSimpleOK() {
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    public ResponseEntity postNumberPlate(@RequestParam("numplate") String plate, @RequestParam("image") MultipartFile
            multipartFile) {
        NumberPlate np = new NumberPlate();
        try {
            np.setImageBlob(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        np.setNumberString(plate);

        log.info("Received a plate: {}", np.getNumberString());


        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
