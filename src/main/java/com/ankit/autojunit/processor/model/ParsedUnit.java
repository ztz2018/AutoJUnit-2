package com.ankit.autojunit.processor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        "className",
        "classPackage",
        "imports",
        "classVariables",
        "autowiredObjects",
        "externalServices"})
public class ParsedUnit {

    private String className;
    private String classPackage;
    private List<Variable> autowiredObjects = new ArrayList<>();
    private List<String> imports = new ArrayList<>();
    private List<Variable> classVariables = new ArrayList<>();
    private List<MyMethodDeclaration> externalServices = new ArrayList<>();

    @JsonIgnore // ignoring because it is huge. ToDO : later, keep only what is necessary
    private List<MethodDeclaration> internalServices = new ArrayList<>();

}
