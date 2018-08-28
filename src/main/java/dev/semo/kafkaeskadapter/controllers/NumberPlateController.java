package dev.semo.kafkaeskadapter.controllers;

import dev.semo.kafkaeskadapter.models.NumberPlate;
import dev.semo.kafkaeskadapter.producer.NumberPlateSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@MultipartConfig(fileSizeThreshold = 5500)
public class NumberPlateController {

    private static Logger log = LogManager.getLogger(NumberPlateController.class);

    @Autowired
    private NumberPlateSender numberPlateSender;

    @PostMapping("/data")
    public ResponseEntity<String> postNumberPlate(@RequestParam("numplate") String plate, @RequestParam("image")
            MultipartFile
            multipartFile) throws IOException {

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

//    @RequestMapping(value = "/data/unserialized", method = RequestMethod.POST)
//    public ResponseEntity<String> postNumberPlateUnserialized(@RequestParam("numplate") String plate, @RequestParam
//            ("image") String
//            base64EncodedImage) {
//        NumberPlate np = new NumberPlate();
//        np.setImageBlob(Base64.getDecoder().decode(base64EncodedImage));
//        np.setNumberString(plate);
//        log.trace("Received a plate: {}", np.getNumberString() + " " + np.getImageBlob().toString());
//
//        numberPlateSender.sendNumberPlateMessage(np);
//
//        return new ResponseEntity<String>(HttpStatus.OK);
//    }


    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
