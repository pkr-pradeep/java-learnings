package org.example.designpatterns.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>TEMPLATE METHOD DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Enterprise Data Ingestion & Data Mining Pipeline (ETL: Extract, Transform, Load).
 * Regardless of input format (CSV File vs. JSON REST Payload vs. SQL Database Table), every ingestion workflow must follow an identical multi-stage pipeline algorithm:
 * <ol>
 *   <li><b>Step 1:</b> Open & Connect to Resource (Invariant)</li>
 *   <li><b>Step 2:</b> Extract Raw Data (Variant - Subclass specific)</li>
 *   <li><b>Step 3:</b> Transform & Validate Data (Variant - Subclass specific)</li>
 *   <li><b>Step 4:</b> Persist Records into Data Warehouse (Invariant)</li>
 *   <li><b>Step 5:</b> Cleanup & Close Connections (Invariant)</li>
 *   <li><b>Hook:</b> Send Success Notification (Optional Subclass Hook)</li>
 * </ol>
 *
 * <h2>Problem Solved:</h2>
 * Defines the skeleton of an algorithm in an operation, deferring some steps to subclasses.
 * Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Duplicates connection, resource cleanup, and database insertion code across CSV, JSON, and DB parser classes.</li>
 *   <li><b>Pitfall:</b> Code duplication leads to inconsistent resource leak bugs when cleanup logic is missed in one of the implementations.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Declares the template method as `final` in the abstract base class to enforce execution order invariants.</li>
 *   <li>Uses optional <b>Hook Methods</b> (methods with empty default implementations in superclass) allowing optional subclass extensions.</li>
 *   <li>Real-world Framework Examples: Spring {@code JdbcTemplate}, {@code HttpServlet#service()} (which delegates to {@code doGet()}, {@code doPost()}), JUnit test setup lifecycle hooks ({@code @BeforeEach}, {@code @AfterEach}).</li>
 * </ul>
 */
public class TemplateMethodDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: TEMPLATE METHOD DEMO ===");

        // 1. Process CSV Ingestion Pipeline
        System.out.println("--- 1. Executing CSV Ingestion Data Pipeline ---");
        DataIngestionPipeline csvPipeline = new CsvDataIngestionPipeline("users_export_2026.csv");
        csvPipeline.processIngestionPipeline();

        System.out.println("\n--- 2. Executing JSON Ingestion Data Pipeline ---");
        DataIngestionPipeline jsonPipeline = new JsonDataIngestionPipeline("https://api.partner.com/v2/products.json");
        jsonPipeline.processIngestionPipeline();
    }

    // -------------------------------------------------------------
    // Abstract Superclass containing Template Method
    // -------------------------------------------------------------

    public static abstract class DataIngestionPipeline {
        protected final String sourcePath;

        public DataIngestionPipeline(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        /**
         * THE TEMPLATE METHOD.
         * Enforces invariant execution sequence for all ingestion pipelines.
         * Declared final so subclasses cannot override algorithm skeleton order!
         */
        public final void processIngestionPipeline() {
            System.out.println("[ETL Pipeline] Starting automated data ingestion lifecycle...");
            
            // Hook call (Optional setup before processing)
            onBeforeStart();

            // Step 1: Open Resource
            openSourceConnection();

            // Step 2: Extract (Variant)
            List<String> rawRecords = extractRawRecords();
            System.out.printf("[ETL Pipeline] Extracted %d raw records from %s.%n", rawRecords.size(), sourcePath);

            // Step 3: Transform & Cleanse (Variant)
            List<String> cleansedRecords = transformAndValidate(rawRecords);
            System.out.printf("[ETL Pipeline] Cleansed & validated %d records.%n", cleansedRecords.size());

            // Step 4: Load into Warehouse (Invariant)
            loadIntoDataWarehouse(cleansedRecords);

            // Step 5: Close Resource (Invariant)
            closeSourceConnection();

            // Hook call (Optional teardown / notification)
            onSuccess(cleansedRecords.size());

            System.out.println("[ETL Pipeline] Ingestion lifecycle completed successfully.\n");
        }

        // Invariant Steps (Common implementation across all subclasses)
        private void openSourceConnection() {
            System.out.println("  [Step 1] Opening I/O stream connection to " + sourcePath);
        }

        private void loadIntoDataWarehouse(List<String> records) {
            System.out.printf("  [Step 4] Batch inserting %d records into PostgreSQL Data Warehouse table 'staging_ingest'...%n", records.size());
        }

        private void closeSourceConnection() {
            System.out.println("  [Step 5] Closing I/O handles and freeing memory buffers.");
        }

        // Abstract Variant Steps (Must be implemented by concrete subclasses)
        protected abstract List<String> extractRawRecords();
        protected abstract List<String> transformAndValidate(List<String> rawRecords);

        // HOOK METHODS (Optional overridable methods with default no-op implementation)
        protected void onBeforeStart() {
            // Default no-op hook
        }

        protected void onSuccess(int processedCount) {
            // Default no-op hook
        }
    }

    // -------------------------------------------------------------
    // Concrete Class 1: CSV File Ingestion Pipeline
    // -------------------------------------------------------------

    public static class CsvDataIngestionPipeline extends DataIngestionPipeline {
        public CsvDataIngestionPipeline(String filePath) {
            super(filePath);
        }

        @Override
        protected List<String> extractRawRecords() {
            System.out.println("  [Step 2 - CSV] Parsing comma-separated lines from CSV file...");
            List<String> csvLines = new ArrayList<>();
            csvLines.add("id,name,email");
            csvLines.add("1,John Doe,john@example.com");
            csvLines.add("2,Jane Smith,jane@example.com");
            return csvLines;
        }

        @Override
        protected List<String> transformAndValidate(List<String> rawRecords) {
            System.out.println("  [Step 3 - CSV] Stripping headers, sanitizing whitespace, validating email format...");
            List<String> transformed = new ArrayList<>();
            for (String line : rawRecords) {
                if (!line.startsWith("id")) { // Skip header
                    transformed.add(line.toUpperCase());
                }
            }
            return transformed;
        }

        @Override
        protected void onBeforeStart() {
            System.out.println("  [CSV Hook] Pre-allocating CSV memory buffer scanner...");
        }
    }

    // -------------------------------------------------------------
    // Concrete Class 2: JSON REST API Ingestion Pipeline
    // -------------------------------------------------------------

    public static class JsonDataIngestionPipeline extends DataIngestionPipeline {
        public JsonDataIngestionPipeline(String url) {
            super(url);
        }

        @Override
        protected List<String> extractRawRecords() {
            System.out.println("  [Step 2 - JSON] Fetching HTTP REST payload and parsing JSON Array...");
            List<String> jsonObjects = new ArrayList<>();
            jsonObjects.add("{\"sku\": \"PROD-1\", \"price\": 19.99}");
            jsonObjects.add("{\"sku\": \"PROD-2\", \"price\": 49.99}");
            return jsonObjects;
        }

        @Override
        protected List<String> transformAndValidate(List<String> rawRecords) {
            System.out.println("  [Step 3 - JSON] Unmarshalling JSON nodes into Domain Objects...");
            List<String> transformed = new ArrayList<>();
            for (String json : rawRecords) {
                transformed.add("TransformedRecord[" + json + "]");
            }
            return transformed;
        }

        @Override
        protected void onSuccess(int processedCount) {
            System.out.printf("  [JSON Hook] Dispatching Slack Notification: Ingested %d API records.%n", processedCount);
        }
    }
}
