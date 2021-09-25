package io.github.nambach.excelutil.util;

import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

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
            System.err.println("Some error happened: " + e.getMessage());
            e.printStackTrace();
        }

        if (closeQuietly) {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static InputStream readFromDisk(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }
}
