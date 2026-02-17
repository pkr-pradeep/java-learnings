package org.example.functions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class PDFPasswordProtectionCheck {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\pkrpr\\Downloads\\Resume.pdf");
        try (ZipFile zipEntry = new ZipFile(file)) {

            System.out.println("no password");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
