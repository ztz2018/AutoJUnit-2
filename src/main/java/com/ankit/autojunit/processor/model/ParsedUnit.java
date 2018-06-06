package com.ankit.autojunit.processor.model;

import com.ankit.autojunit.processor.model.child.MyMethodDeclaration;
import com.ankit.autojunit.processor.model.child.Variable;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParsedUnit {

    @JsonPropertyOrder({"className", "imports", "classVariables",
        "autowiredObjects", "externalServices"})

    private String className;
    private List<Variable> autowiredObjects = new ArrayList<>();
    private List<String> imports = new ArrayList<>();
    private List<Variable> classVariables = new ArrayList<>();
    private List<MyMethodDeclaration> externalServices = new ArrayList<>();

}
