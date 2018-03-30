package cmdapp;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.text.Format;

public class FileConverter implements IStringConverter<File> {

    @Override
    public File convert(String value) {
        File file = new File(value);
        if (!file.exists()) {
            throw new ParameterException(String.format("File not found in the path: %s", file.getAbsolutePath()));
        } else if (!file.canRead()) {
            throw new ParameterException(String.format("Cannot read the file in the path: %s", file.getAbsolutePath()));
        }
        return file;
    }
}