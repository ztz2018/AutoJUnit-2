package com.ankit.autojunit.processor.model;

import com.ankit.autojunit.processor.model.child.MethodDeclaration;
import com.ankit.autojunit.processor.model.child.Variable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParsedUnit {

    private String className;
    private List<Variable> autowiredObjects = new ArrayList<>();
    private List<String> imports = new ArrayList<>();
    private List<Variable> classVariables = new ArrayList<>();
    private List<MethodDeclaration> externalServices = new ArrayList<>();

}
