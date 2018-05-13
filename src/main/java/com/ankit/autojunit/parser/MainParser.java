package com.ankit.autojunit.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.io.FileReader;
import java.util.Optional;

public class MainParser {

    public static void main(String args[]) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader
                    ("src/main/java/com/ankit/autojunit/sample_project/OperationsServiceImpl.java"));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName("OperationsServiceImpl");
            ClassOrInterfaceDeclaration operationServicempl = classMain.get();
            Optional<AnnotationExpr>  annotation = operationServicempl.getAnnotationByName("Autowired");
            System.out.println("Autowired present ? : " + annotation.isPresent());
        } catch (Exception e) {
                    e.printStackTrace();
        }

    }

}
