import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileEncryptor {

    public static void processFile(File inputFile, File outputFile, int key) {
        try (
                FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile)) {
            int data;
            while ((data = fis.read()) != -1) {
                fos.write(data ^ key);
            }
        } catch (IOException e) {
            System.err.println("❌ Error processing file: " + e.getMessage());
        }
    }
}
