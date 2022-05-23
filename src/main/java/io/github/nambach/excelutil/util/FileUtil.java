package io.github.nambach.excelutil.util;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@Log4j2
public class FileUtil {
    private FileUtil() {
    }

    public static void writeToDisk(String path, InputStream inputStream, boolean closeQuietly) {
        File targetFile = new File(path);

        try {
            java.nio.file.Files.copy(
                    inputStream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Some error happened while writing file to disk.", e);
        }

        if (closeQuietly) {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static InputStream readFromDisk(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }
}
