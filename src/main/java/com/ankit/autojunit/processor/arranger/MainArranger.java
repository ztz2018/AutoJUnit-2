package com.ankit.autojunit.processor.arranger;

import com.ankit.autojunit.processor.model.ParsedUnit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainArranger extends ArrangeService{

    @Override
    public void arrange(ParsedUnit parsedUnit) {

        prepareBoilerPlate();
        arrangeImports(parsedUnit.getImports());
        prepareSetupMethod();
        prepareTestMethod();
        prepareClassAsString();

    }

    @Override
    protected void prepareBoilerPlate() {

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
}
