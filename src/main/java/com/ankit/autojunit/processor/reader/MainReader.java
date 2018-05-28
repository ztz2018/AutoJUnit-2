package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.parser.MainParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * This class is temporary input source. I have to figure out some other and cool ways of taking input.
 * This class just passes the name of the class to be parsed to the parser
 */

@Controller
@RequestMapping("/autojunit/main")
public class MainReader {

    @Autowired
    MainParser mainParser;

    @GetMapping("/read")
    public ResponseEntity reader() {

        String fileLocation = "src/main/java/com/ankit/autojunit/sample_project/";
        String fileName = "OperationsServiceImpl";
        String fileExtenstion = "java";

        ResponseEntity response;

        try {
            response = ResponseEntity.ok(mainParser.parseFileWithName(fileLocation, fileName, fileExtenstion));
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

}
