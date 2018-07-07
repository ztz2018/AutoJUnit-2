package com.ankit.autojunit.processor.arranger;

import com.ankit.autojunit.processor.CommonUtil;
import com.ankit.autojunit.processor.Constants;
import com.ankit.autojunit.processor.model.ParsedUnit;
import com.ankit.autojunit.processor.model.Variable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ankit.autojunit.processor.CommonUtil.createVariableNameOfType;

@Service
public class MainArranger extends ArrangeService{

    @Value("${arranger.destDir}")
    private String destDir;

    @Value("${reader.companyDomain}")
    private String companyDomain;

    @Override
    public void arrange(ParsedUnit parsedUnit) {

        final StringBuilder classAsString = new StringBuilder();
        List<String> allImports = new ArrayList<>();

        String allMocks = mockTheAutowiredObjects(parsedUnit, allImports);



        String boilerPlate = prepareBoilerPlate(parsedUnit, allImports);

        classAsString.append(boilerPlate);
        classAsString.append(allMocks);

        classAsString.append("}");
        callWriter(classAsString.toString(), parsedUnit.getClassPackage(), parsedUnit.getClassName());
    }

    @Override
    protected String prepareBoilerPlate(ParsedUnit parsedUnit, List<String> allImports) {

        String className = parsedUnit.getClassName();
        String classPackage = parsedUnit.getClassPackage();

        allImports.add(Constants.IMPORT_FOR_INJECT_MOCKS);

        StringBuilder boilerPlateStr = new StringBuilder();
        boilerPlateStr.append(Constants.PACKAGE + " " + classPackage + ";\n\n");

        for(String eachImport : allImports) {
            boilerPlateStr.append(Constants.IMPORT + " " + eachImport + ";\n");
        }
        boilerPlateStr.append("\n");
        boilerPlateStr.append("/* " + Constants.CREDITS + new Date() + " */\n");
        boilerPlateStr.append(Constants.PUBLIC_CLASS + " " + className + Constants.TEST + " {\n\n");
        boilerPlateStr.append("\t" + Constants.AT_INJECT_MOCKS + "\n");
        boilerPlateStr.append("\t" + className + " " + createVariableNameOfType(className) + ";\n");

        return boilerPlateStr.toString();
    }

    @Override
    protected String mockTheAutowiredObjects(ParsedUnit parsedUnit, List<String> allImports) {
        StringBuilder allMocks = new StringBuilder("\n");

        allImports.add(Constants.IMPORT_FOR_MOCK);

        for (Variable ao : parsedUnit.getAutowiredObjects()) {
            allMocks.append("\t" + Constants.AT_MOCK +  "\n");
            allMocks.append("\t" + ao.getClassName() + " " + createVariableNameOfType(ao.getClassName() + ";\n\n"));
            if (!parsedUnit.getClassPackage().equals(ao.getClassPackage())) {
                allImports.add(ao.getClassPackage());
            }
        }

        return allMocks.toString();
    }

    @Override
    protected String prepareSetupMethod(ParsedUnit parsedUnit, List<String> allImports) {
        return null;
    }

    @Override
    protected String prepareAllTestMethod(ParsedUnit parsedUnit, List<String> allImports) {
        return null;
    }

    @Override
    protected void callWriter(String classAsString, String classPackage, String className) {
        String destDir = CommonUtil.processFilePath(this.destDir, companyDomain, classPackage, className, false);
        try {
            File file = new File(destDir);
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file);
            writer.println(classAsString);
            writer.close();
        } catch (FileNotFoundException e) {

        }
    }

}
