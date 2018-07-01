package com.ankit.autojunit.processor.arranger;

import com.ankit.autojunit.processor.CommonUtil;
import com.ankit.autojunit.processor.model.ParsedUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class MainArranger extends ArrangeService{

    @Value("${arranger.destDir}")
    private String destDir;

    @Value("${reader.companyDomain}")
    private String companyDomain;

    @Override
    public void arrange(ParsedUnit parsedUnit) {

        final  StringBuilder classAsString = new StringBuilder();
        classAsString.append(prepareBoilerPlate(parsedUnit));



        classAsString.append("}");
        callWriter(classAsString.toString(), parsedUnit.getClassPackage(), parsedUnit.getClassName());
    }

    @Override
    protected String prepareBoilerPlate(ParsedUnit parsedUnit) {

        String className = parsedUnit.getClassName();
        String classPackage = parsedUnit.getClassPackage();

        StringBuilder boilerPlateStr = new StringBuilder();
        boilerPlateStr.append("package " + classPackage + ";\n\n");
        boilerPlateStr.append("import org.mockito.InjectMocks;\n\n");
        boilerPlateStr.append("public class " + className + "Test {\n\n");
        boilerPlateStr.append("\t@InjectMocks\n");
        boilerPlateStr.append("\t" + className + " " + createVariableNameOfType(className) + ";\n\n");


        return boilerPlateStr.toString();
    }

    @Override
    protected void mockTheAutowiredObjects() {

    }

    @Override
    protected List<String> arrangeImports(List<String> allImports) {
        return null;
    }

    @Override
    protected void prepareSetupMethod() {

    }

    @Override
    protected void prepareTestMethod() {

    }

    @Override
    protected void prepareClassAsString() {

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

    private String createVariableNameOfType(String type) {
        String first = type.substring(0,1);
        first = first.toLowerCase();
        return first + type.substring(1);
    }
}
