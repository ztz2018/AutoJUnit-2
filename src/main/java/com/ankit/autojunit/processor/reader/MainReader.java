package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.parser.MainParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileReader;
import java.util.Optional;


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
            response = ResponseEntity.ok(parseFileWithName(fileLocation, fileName, fileExtenstion));
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    private ParsedUnit parseFileWithName(String fileLocation, String fileName, String fileExtension) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(fileLocation + fileName + "." + fileExtension));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(fileName);
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration operationServicempl = classMain.get();
                return mainParser.parseTheDeclaration(operationServicempl);
            } else {
                throw new NullPointerException("Not found : " + fileName + "!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
