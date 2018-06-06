package com.ankit.autojunit.processor.reader;

import com.ankit.autojunit.processor.model.ParsedUnit;

public interface ReaderService {

    ParsedUnit parseFileWithName(String rootDir, String fileLocation, String fileName);

}
