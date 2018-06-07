package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public interface ReaderService {

    ParsedUnit parseFileWithName(String fileLocation, String fileName);

    String processFilePath(String rootDir, String classPackage, String className);

    ClassOrInterfaceDeclaration compileClass(String classPackage, String className);
}
