package com.ankit.autojunit.processor.parser;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.Optional;

@Component
public class MainParser {

    public ParsedUnit parseFileWithName(String fileLocation, String fileName, String fileExtension) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(fileLocation + fileName + "." + fileExtension));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(fileName);
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration operationServicempl = classMain.get();
                return parseDeclaration(operationServicempl);
            } else {
                throw new NullPointerException("Not found : " + fileName + "!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ParsedUnit parseDeclaration(ClassOrInterfaceDeclaration clazz) {

        ParsedUnit parsedUnit = new ParsedUnit();

        parsedUnit.setClassName(clazz.getNameAsString());
        

        return parsedUnit;
    }


}
