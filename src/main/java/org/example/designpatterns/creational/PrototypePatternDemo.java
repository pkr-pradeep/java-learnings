package org.example.designpatterns.creational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>PROTOTYPE DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Creational Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * High-Performance E-Commerce Product Catalog / Complex Custom Report Template Generator.
 * Creating a comprehensive Document/Report template requires querying dozens of database tables, fetching dynamic branding rules, and initializing complex object graphs (taking ~500ms per instantiation).
 * When generating thousands of personalized reports concurrently, instantiating new objects via `new` is too slow.
 * Prototype allows cloning an already-initialized prototype instance in memory (< 1ms).
 *
 * <h2>Problem Solved:</h2>
 * Creates new objects by copying an existing instance rather than instantiating from scratch via costly database or network operations.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses standard `java.lang.Cloneable` blindly without understanding shallow vs deep copy pitfalls.</li>
 *   <li><b>Pitfall:</b> Performing a shallow copy clones object references. Modifying a list in the cloned report inadvertently mutates the original template report!</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Prefers <b>Copy Constructors</b> or explicit deep cloning methods over Java's flawed default `Object.clone()` mechanism.</li>
 *   <li>Maintains a <b>Prototype Registry / Cache</b> for fast dynamic retrieval and cloning of pre-configured templates.</li>
 *   <li>Real-world Framework Examples: Spring Scope `prototype` beans, `java.lang.Object#clone()`.</li>
 * </ul>
 */
public class PrototypePatternDemo {

    public static void main(String[] args) {
        System.out.println("=== CREATIONAL PATTERN: PROTOTYPE DEMO ===");

        // Initialize registry with expensive base prototypes
        ReportTemplateRegistry registry = new ReportTemplateRegistry();
        registry.loadPrototypes();

        System.out.println("--- 1. Cloning Financial Audit Report Prototype ---");
        ReportTemplate auditReport1 = registry.getTemplate("FINANCIAL_AUDIT");
        auditReport1.setTitle("Q3 2026 Executive Financial Audit");
        auditReport1.addSection("Executive Summary: Revenue up by 18%.");
        auditReport1.getBrandingConfig().setPrimaryColor("#003366");

        System.out.println("Cloned Audit Report 1:\n" + auditReport1);

        System.out.println("\n--- 2. Cloning another Financial Audit Report (Deep Copy Verification) ---");
        ReportTemplate auditReport2 = registry.getTemplate("FINANCIAL_AUDIT");
        auditReport2.setTitle("Q4 2026 Board Financial Audit");
        auditReport2.addSection("Risk Assessment: Inflation hedges effective.");

        System.out.println("Cloned Audit Report 2:\n" + auditReport2);

        System.out.println("--------------------------------------------------");
        System.out.println("Verification:");
        System.out.println("Report 1 Sections Count: " + auditReport1.getSections().size()); // Should be 3 (2 base + 1 added)
        System.out.println("Report 2 Sections Count: " + auditReport2.getSections().size()); // Should be 3 (2 base + 1 added)
        System.out.println("Are branding configs independent objects? " + 
                (auditReport1.getBrandingConfig() != auditReport2.getBrandingConfig()));
    }

    // -------------------------------------------------------------
    // Prototype Interface
    // -------------------------------------------------------------

    public interface Prototype<T> {
        T clonePrototype();
    }

    // -------------------------------------------------------------
    // Inner Supporting Class: Branding Configuration
    // -------------------------------------------------------------

    public static class BrandingConfig implements Prototype<BrandingConfig> {
        private String logoPath;
        private String primaryColor;

        public BrandingConfig(String logoPath, String primaryColor) {
            this.logoPath = logoPath;
            this.primaryColor = primaryColor;
        }

        // Copy constructor for deep copy
        public BrandingConfig(BrandingConfig other) {
            this.logoPath = other.logoPath;
            this.primaryColor = other.primaryColor;
        }

        @Override
        public BrandingConfig clonePrototype() {
            return new BrandingConfig(this);
        }

        public String getLogoPath() { return logoPath; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
        public String getPrimaryColor() { return primaryColor; }
        public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

        @Override
        public String toString() {
            return String.format("[Logo='%s', PrimaryColor='%s']", logoPath, primaryColor);
        }
    }

    // -------------------------------------------------------------
    // Concrete Prototype: Report Template
    // -------------------------------------------------------------

    public static class ReportTemplate implements Prototype<ReportTemplate> {
        private String templateType;
        private String title;
        private List<String> sections;
        private BrandingConfig brandingConfig;

        public ReportTemplate(String templateType, String title, List<String> sections, BrandingConfig brandingConfig) {
            this.templateType = templateType;
            this.title = title;
            this.sections = sections;
            this.brandingConfig = brandingConfig;
        }

        /**
         * DEEP COPY implementation: prevents references from being shared between clones.
         */
        @Override
        public ReportTemplate clonePrototype() {
            // Deep copy of sections list
            List<String> clonedSections = new ArrayList<>(this.sections);
            // Deep copy of mutable branding config object
            BrandingConfig clonedBranding = this.brandingConfig.clonePrototype();

            return new ReportTemplate(this.templateType, this.title, clonedSections, clonedBranding);
        }

        public void addSection(String section) {
            this.sections.add(section);
        }

        public String getTemplateType() { return templateType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public List<String> getSections() { return sections; }
        public BrandingConfig getBrandingConfig() { return brandingConfig; }

        @Override
        public String toString() {
            return String.format("ReportTemplate [Type=%s, Title='%s', Branding=%s, Sections=%s]",
                    templateType, title, brandingConfig, sections);
        }
    }

    // -------------------------------------------------------------
    // Prototype Registry / Cache Manager
    // -------------------------------------------------------------

    public static class ReportTemplateRegistry {
        private final Map<String, ReportTemplate> registry = new HashMap<>();

        /**
         * Simulates expensive initial loading (e.g. database lookups, layout calculations).
         */
        public void loadPrototypes() {
            System.out.println("[Registry] Simulating heavy DB read & compiling base report templates...");

            List<String> finSections = new ArrayList<>();
            finSections.add("Section 1: Balance Sheet");
            finSections.add("Section 2: Cash Flow Statement");
            BrandingConfig finBranding = new BrandingConfig("/assets/logos/finance_header.png", "#1A365D");

            ReportTemplate financialTemplate = new ReportTemplate(
                    "FINANCIAL_AUDIT", "Standard Corporate Audit Template", finSections, finBranding);

            registry.put("FINANCIAL_AUDIT", financialTemplate);

            List<String> hrSections = new ArrayList<>();
            hrSections.add("Section 1: Employee Performance Metrics");
            hrSections.add("Section 2: Retention Analytics");
            BrandingConfig hrBranding = new BrandingConfig("/assets/logos/hr_header.png", "#2B6CB0");

            ReportTemplate hrTemplate = new ReportTemplate(
                    "HR_ANNUAL", "Standard HR Annual Template", hrSections, hrBranding);

            registry.put("HR_ANNUAL", hrTemplate);
            System.out.println("[Registry] Prototypes cached successfully in registry.");
        }

        public ReportTemplate getTemplate(String type) {
            ReportTemplate prototype = registry.get(type);
            if (prototype == null) {
                throw new IllegalArgumentException("No prototype registered for type: " + type);
            }
            // Return a cloned copy so the master prototype remains untouched!
            return prototype.clonePrototype();
        }
    }
}
