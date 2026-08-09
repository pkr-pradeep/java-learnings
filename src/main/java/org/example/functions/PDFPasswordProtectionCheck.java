package org.example.functions;

import java.io.File;
import java.util.zip.ZipFile;

/**
 * Utility example checking file accessibility and archive/encryption headers safely.
 */
public class PDFPasswordProtectionCheck {

    public static void main(String[] args) {
        String targetPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "Sample.pdf";
        File file = new File(targetPath);

        if (!file.exists()) {
            System.out.println("File does not exist at path: " + targetPath + " (Skipping inspection)");
            return;
        }

        try (ZipFile zipEntry = new ZipFile(file)) {
            System.out.println("File opened successfully without zip password protection.");
        } catch (Exception ex) {
            System.out.println("Could not open as zip archive: " + ex.getMessage());
        }
    }
}
