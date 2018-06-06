package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.parser.MainParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.Optional;

@Service
public class ReaderServiceImpl implements ReaderService {

    @Autowired
    MainParser mainParser;

    @Override
    public  ParsedUnit parseFileWithName(String rootDir, String classPackage, String className) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(processFilePath(rootDir, classPackage, className)));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(className);
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration operationServicempl = classMain.get();
                return mainParser.parseTheDeclaration(operationServicempl);
            } else {
                throw new NullPointerException("Not found : " + className + "!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String processFilePath(String rootDir, String classPackage, String className) {

        StringBuilder path = new StringBuilder();
        rootDir = rootDir.contains(".") ? rootDir.replaceAll("[/.]", "/") : rootDir;
        classPackage = classPackage.contains(".") ? classPackage.replaceAll("[/.]", "/") : classPackage;
        path.append(rootDir);
        path.append("/");
        path.append(classPackage);
        path.append("/");
        path.append(className);
        path.append(".java");
        return path.toString();
    }

}
