package com.ankit.autojunit.processor.parser;

import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.model.child.Variable;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MainParser {

    public ParsedUnit parseFileWithName(String fileLocation, String fileName, String fileExtension) {

        try {
            CompilationUnit compilationUnit = JavaParser.parse(new FileReader(fileLocation + fileName + "." + fileExtension));
            Optional<ClassOrInterfaceDeclaration> classMain = compilationUnit.getClassByName(fileName);
            if (classMain.isPresent()) {
                ClassOrInterfaceDeclaration operationServicempl = classMain.get();
                return parseTheDeclaration(operationServicempl);
            } else {
                throw new NullPointerException("Not found : " + fileName + "!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ParsedUnit parseTheDeclaration(ClassOrInterfaceDeclaration clazz) {

        List<String> allImports = getRequiredImports(clazz);
        String currentPackageName = ((PackageDeclaration)((CompilationUnit) clazz.getParentNode().get())
                .getPackageDeclaration().get()).toString();

        ParsedUnit parsedUnit = new ParsedUnit();

        parsedUnit.setClassName(clazz.getNameAsString());
        parsedUnit.setImports(allImports);
        parsedUnit.setAutowiredObjects(getAutowiredObjects(clazz, allImports, currentPackageName));
        parsedUnit.setClassVariables(getGlobalVariables(clazz, parsedUnit.getImports(), currentPackageName));


        return parsedUnit;
    }

    /**
     *  Gets all the imports
     *  ToDo : As of now, it takes all the imports. Later, change to only required ones.
     */
    private List<String> getRequiredImports(ClassOrInterfaceDeclaration clazz)
    {
        List<String> imports = new ArrayList<>();
        ((CompilationUnit)clazz.getParentNode().get()).getImports().stream().forEach(imp ->{
            imports.add(imp.getNameAsString());
        });
        return imports;
    }

    /**
     *  Gets following details about autowired objects:
     *      1. Class name
     *      2. Identifier name
     *      3. Class package
     *      4. Initialization string (if any)
     */
    private List<Variable> getAutowiredObjects( ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
    {
        List<Variable> autowiredObjects = new ArrayList<>();
        for (Node node : clazz.getChildNodes()) {
            if(node instanceof FieldDeclaration) {
                for (AnnotationExpr annotation : ((FieldDeclaration) node).getAnnotations()) {
                    if(annotation.getNameAsString().equals("Autowired")) {
                        Variable autowiredVariable = new Variable();
                        autowiredVariable.setClassName(((FieldDeclaration) node).getVariables().get(0).getType().asString());
                        autowiredVariable.setIdentifierName(((FieldDeclaration) node).getVariables().get(0).getNameAsString());
                        autowiredVariable.setClassPackage(searchImportsForClass(allImports, currentPackageName, autowiredVariable.getClassName()));
                        autowiredObjects.add(autowiredVariable);
                    }
                }
            }
        }
        return autowiredObjects;
    }

    /**
     * Take only those field declarations which
     *   #1> do not have any annotations
     */
    private List<Variable> getGlobalVariables(ClassOrInterfaceDeclaration clazz, List<String> allImports, String currentPackageName)
    {
        List<Variable> variables = new ArrayList<>();
        clazz.getChildNodes().stream().forEach(node -> {
            if (node instanceof FieldDeclaration && CollectionUtils.isEmpty(((FieldDeclaration) node).getAnnotations())) {
                Variable variable = new Variable();
                variable.setClassName(((FieldDeclaration) node).getVariables().get(0).getTypeAsString());
                variable.setClassPackage(searchImportsForClass(allImports, currentPackageName, variable.getClassName()));
                variable.setIdentifierName(((FieldDeclaration) node).getVariables().get(0).getNameAsString());
                variables.add(variable);
            }
        });
        return variables;
    }

    /**
     *  Searches imports for a particular class, Returns current package if not found.
     *  ToDo : This API will fail if any import is of type import blabla.bla.*
     */
    private String searchImportsForClass(List<String> allImports, String currentPackageName, String className)
    {
        String searchImport = currentPackageName.split(" ")[1];
        List<String> anyImport = allImports.stream().filter(currentImport -> currentImport.contains(className)).collect(Collectors.toList());
        if (anyImport != null && anyImport.size() != 0)
        {
            searchImport = anyImport.get(0);
        }
        return searchImport;
    }

    /**
     *  This API summarizes all the calls that are made to external services.
     *  Keeps track of the class name, method name, return type, arguments, method access
     */
    private List<MethodDeclaration> getExternalServices(ClassOrInterfaceDeclaration clazz, List<String> allImports,
            String currentPackageName)
    {
        List<MethodDeclaration> externalServices = new ArrayList<>();

//        Object obj = ((MethodCallExpr) ((ExpressionStmt) ((BlockStmt) ((MethodDeclaration)clazz.getChildNodes().get(6)).getBody().get()).getStatements().get(2)).getExpression());

        return  externalServices;
    }

}
