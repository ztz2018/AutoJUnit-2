package com.ankit.autojunit.processor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestClassIngredients {

    private String testClassName;
    private String classToBeTested;
    private List<String> imports;
    private MyMethodDeclaration setup;
    private List<Variable> mocks = new ArrayList<>();
    private List<TestMethodDefinition> testMethodDefinitions = new ArrayList<>();

}
