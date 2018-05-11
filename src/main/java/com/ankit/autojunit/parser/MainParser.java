package com.ankit.autojunit.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.FileReader;
import java.util.Optional;

public class MainParser {

    public static void main(String args[]) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader("src/main/java/sample_project/MainClass.java"));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName("MainClass");
            classMain.get();
            classMain.toString();
        } catch (Exception e) {
                    e.printStackTrace();
        }

    }

}
