package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.CommonUtil;
import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.parser.MainParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.Optional;

@Service
public class ReaderServiceImpl implements ReaderService {

    @Autowired
    MainParser mainParser;

    @Value("${reader.rootDir}")
    private String rootDir;

    @Value(("${reader.companyDomain}"))
    private String companyDomain;

    @Override
    public ParsedUnit parseFileWithName(String classPackage, String className) {
        return mainParser.parseTheDeclaration(compileClass(classPackage, className));
    }

    @Override
    public ClassOrInterfaceDeclaration compileClass(String classPackage, String className) {
        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(CommonUtil.processFilePath(rootDir, companyDomain, classPackage, className, true)));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(className);
            if (!classMain.isPresent()) {
                classMain = compilationUnit.getInterfaceByName(className);
            }
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = classMain.get();
                return classOrInterfaceDeclaration;
            } else {
                throw new ClassNotFoundException("Class \"" + className + "\" not found in the package \"" + classPackage + "\"");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
