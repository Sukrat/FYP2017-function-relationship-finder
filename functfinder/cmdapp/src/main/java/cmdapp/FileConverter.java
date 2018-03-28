package cmdapp;

import com.beust.jcommander.IStringConverter;

import java.io.File;
import java.text.Format;

public class FileConverter implements IStringConverter<File> {

    @Override
    public File convert(String value) {
        File file = new File(value);
        if (!file.exists()) {
            throw new RuntimeException(String.format("%s - not found", file.getAbsolutePath()));
        } else if (!file.canRead()) {
            throw new RuntimeException(String.format("%s - cannot read", file.getAbsolutePath()));
        }
        return file;
    }
}