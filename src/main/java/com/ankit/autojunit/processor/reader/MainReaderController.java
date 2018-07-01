package com.ankit.autojunit.processor.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MainReaderController {

    @Autowired
    ReaderService readerService;

    @Value("${reader.classPackage}")
    private String classPackage;

    @Value("${reader.className}")
    private String className;

    @GetMapping("/read")
    public ResponseEntity reader() {
        return ResponseEntity.ok(readerService.parseFileWithName(classPackage, className));
    }

}
