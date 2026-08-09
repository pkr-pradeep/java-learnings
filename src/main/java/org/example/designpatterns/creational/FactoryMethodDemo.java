package org.example.designpatterns.creational;

/**
 * <h1>FACTORY METHOD DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Creational Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Multi-Cloud Document Storage System (AWS S3 vs. Azure Blob Storage vs. Local Disk Storage).
 * An enterprise application needs to upload and retrieve files without hardcoding cloud SDK specifics across the core application logic.
 *
 * <h2>Problem Solved:</h2>
 * Eliminates tight coupling between client code and concrete infrastructure implementations.
 * Avoids conditional `if-else` / `switch` blocks littered across business logic when instantiating providers.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Writes `if (provider.equals("AWS")) return new S3Service(); else if (...)` inside every controller or service.</li>
 *   <li><b>Pitfall:</b> When a new cloud provider (e.g. GCP) is introduced, dozens of files must be edited, violating the Open-Closed Principle (OCP).</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Delegates object creation to specialized factory subclasses.</li>
 *   <li>Enables dynamic runtime selection of implementations via configuration files or environment variables.</li>
 *   <li>Real-world Framework Examples: {@code java.util.Calendar.getInstance()}, {@code org.slf4j.LoggerFactory.getLogger()}, Spring's {@code FactoryBean}.</li>
 * </ul>
 */
public class FactoryMethodDemo {

    public static void main(String[] args) {
        System.out.println("=== CREATIONAL PATTERN: FACTORY METHOD DEMO ===");

        // Simulate application configuration (e.g., loaded from application.properties)
        String activeEnvironment = "AWS"; // Try changing to "AZURE" or "LOCAL"

        StorageServiceFactory factory = getStorageFactory(activeEnvironment);
        
        // High-level client code interacts strictly with abstractions
        CloudStorageService storageService = factory.createStorageService();
        
        storageService.uploadFile("invoice_2026_08.pdf", new byte[]{0x48, 0x45, 0x4C, 0x4C, 0x4F});
        storageService.downloadFile("invoice_2026_08.pdf");

        System.out.println("--------------------------------------------------");

        // Demonstrating seamless switching to another cloud provider
        StorageServiceFactory azureFactory = new AzureBlobStorageFactory();
        CloudStorageService azureStorage = azureFactory.createStorageService();
        azureStorage.uploadFile("report_q3.docx", new byte[]{0x01, 0x02, 0x03});
    }

    /**
     * Helper method acting as a simple dispatch strategy for factories.
     */
    private static StorageServiceFactory getStorageFactory(String env) {
        switch (env.toUpperCase()) {
            case "AWS":
                return new S3StorageFactory();
            case "AZURE":
                return new AzureBlobStorageFactory();
            case "LOCAL":
                return new LocalFileSystemStorageFactory();
            default:
                throw new IllegalArgumentException("Unknown storage provider: " + env);
        }
    }

    // -------------------------------------------------------------
    // Product Interface & Concrete Products
    // -------------------------------------------------------------

    public interface CloudStorageService {
        void uploadFile(String fileName, byte[] content);
        byte[] downloadFile(String fileName);
    }

    public static class S3StorageService implements CloudStorageService {
        @Override
        public void uploadFile(String fileName, byte[] content) {
            System.out.println("[AWS S3] Uploading file '" + fileName + "' to S3 Bucket 'prod-company-assets'...");
        }

        @Override
        public byte[] downloadFile(String fileName) {
            System.out.println("[AWS S3] Downloading file '" + fileName + "' from AWS S3 Bucket...");
            return new byte[0];
        }
    }

    public static class AzureBlobStorageService implements CloudStorageService {
        @Override
        public void uploadFile(String fileName, byte[] content) {
            System.out.println("[Azure Blob] Uploading file '" + fileName + "' to Azure Container 'enterprise-data'...");
        }

        @Override
        public byte[] downloadFile(String fileName) {
            System.out.println("[Azure Blob] Downloading file '" + fileName + "' from Azure Blob Storage...");
            return new byte[0];
        }
    }

    public static class LocalFileSystemStorageService implements CloudStorageService {
        @Override
        public void uploadFile(String fileName, byte[] content) {
            System.out.println("[Local Storage] Writing file '" + fileName + "' to disk directory '/var/app/uploads'...");
        }

        @Override
        public byte[] downloadFile(String fileName) {
            System.out.println("[Local Storage] Reading file '" + fileName + "' from disk directory...");
            return new byte[0];
        }
    }

    // -------------------------------------------------------------
    // Creator Abstraction & Concrete Factories (Factory Method Core)
    // -------------------------------------------------------------

    public static abstract class StorageServiceFactory {
        // Factory Method to be overridden by subclasses
        public abstract CloudStorageService createStorageService();

        // High-level template behavior using the product
        public void initializeAndTestStorage() {
            CloudStorageService service = createStorageService();
            System.out.println("Storage factory initialized. Testing ping...");
            service.uploadFile("healthcheck.txt", "OK".getBytes());
        }
    }

    public static class S3StorageFactory extends StorageServiceFactory {
        @Override
        public CloudStorageService createStorageService() {
            // Can include S3 specific configuration, region checks, IAM credentials here
            return new S3StorageService();
        }
    }

    public static class AzureBlobStorageFactory extends StorageServiceFactory {
        @Override
        public CloudStorageService createStorageService() {
            return new AzureBlobStorageService();
        }
    }

    public static class LocalFileSystemStorageFactory extends StorageServiceFactory {
        @Override
        public CloudStorageService createStorageService() {
            return new LocalFileSystemStorageService();
        }
    }
}
