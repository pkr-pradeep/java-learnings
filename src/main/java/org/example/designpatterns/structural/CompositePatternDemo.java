package org.example.designpatterns.structural;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>COMPOSITE DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Structural Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Enterprise Organizational Structure & Salary Calculation Engine.
 * An enterprise consists of individual employees (Developers, Designers, Managers) grouped into sub-departments (Engineering, Marketing), which are nested under larger divisions (Technology, Corporate HQ).
 * Requirements: Calculate total budget/salary costs across any node (individual employee, department, or entire enterprise) recursively.
 *
 * <h2>Problem Solved:</h2>
 * Composes objects into tree structures to represent part-whole hierarchies.
 * Enables clients to treat individual objects (Leaf nodes) and compositions of objects (Composite nodes) uniformly.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses conditional logic and explicit checks (`if (obj instanceof Department) ... else if (obj instanceof Employee)`) traversing nested loops.</li>
 *   <li><b>Pitfall:</b> Unmaintainable traversal code that breaks whenever new node types are added.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Defines a unified Component interface (`OrganizationComponent`) implemented by both Leaf (`Employee`) and Composite (`Department`).</li>
 *   <li>Real-world Framework Examples: HTML DOM tree nodes, File System trees (`java.io.File`), Java Swing/AWT UI containers ({@code Container#add(Component)}).</li>
 * </ul>
 */
public class CompositePatternDemo {

    public static void main(String[] args) {
        System.out.println("=== STRUCTURAL PATTERN: COMPOSITE DEMO ===");

        // 1. Create Leaf nodes (Employees)
        OrganizationComponent dev1 = new IndividualEmployee("EMP-101", "Alice Smith", "Senior Java Engineer", 120000);
        OrganizationComponent dev2 = new IndividualEmployee("EMP-102", "Bob Jones", "Backend Developer", 95000);
        OrganizationComponent dev3 = new IndividualEmployee("EMP-103", "Charlie Brown", "DevOps Specialist", 105000);

        OrganizationComponent qa1 = new IndividualEmployee("EMP-104", "Diana Prince", "QA Lead", 90000);
        OrganizationComponent designer1 = new IndividualEmployee("EMP-105", "Evan Wright", "UI/UX Designer", 85000);

        // 2. Create Composite nodes (Sub-Departments)
        Department backendDept = new Department("Backend Engineering Team");
        backendDept.add(dev1);
        backendDept.add(dev2);
        backendDept.add(dev3);

        Department qaDept = new Department("Quality Assurance Team");
        qaDept.add(qa1);

        Department designDept = new Department("Product Design Team");
        designDept.add(designer1);

        // 3. Create Higher-level Composite node (Engineering Division)
        Department engDivision = new Department("Global Engineering Division");
        engDivision.add(backendDept);
        engDivision.add(qaDept);

        // 4. Create Root Composite node (Company HQ)
        Department companyHq = new Department("Corporate HQ Enterprise");
        companyHq.add(engDivision);
        companyHq.add(designDept);

        // Uniform processing across tree structure
        System.out.println("--- 1. Single Leaf Node Rollup ---");
        System.out.printf("Salary cost for %s: $%.2f%n", dev1.getName(), dev1.calculateSalaryBudget());

        System.out.println("\n--- 2. Sub-Department Node Rollup ---");
        System.out.printf("Salary budget for %s: $%.2f%n", backendDept.getName(), backendDept.calculateSalaryBudget());

        System.out.println("\n--- 3. Full Enterprise Organization Tree Printout & Budget Rollup ---");
        companyHq.printDetails("");
        System.out.printf("%nTotal Enterprise Salary Budget: $%.2f%n", companyHq.calculateSalaryBudget());
    }

    // -------------------------------------------------------------
    // Component Interface
    // -------------------------------------------------------------

    public interface OrganizationComponent {
        String getName();
        double calculateSalaryBudget();
        void printDetails(String indent);

        // Default unsupported operations for Leaf nodes
        default void add(OrganizationComponent component) {
            throw new UnsupportedOperationException("Leaf nodes cannot contain child components.");
        }

        default void remove(OrganizationComponent component) {
            throw new UnsupportedOperationException("Leaf nodes cannot remove child components.");
        }
    }

    // -------------------------------------------------------------
    // Leaf Node (Individual Employee)
    // -------------------------------------------------------------

    public static class IndividualEmployee implements OrganizationComponent {
        private final String id;
        private final String name;
        private final String role;
        private final double salary;

        public IndividualEmployee(String id, String name, String role, double salary) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.salary = salary;
        }

        @Override
        public String getName() { return name; }

        @Override
        public double calculateSalaryBudget() {
            return salary;
        }

        @Override
        public void printDetails(String indent) {
            System.out.printf("%s- [Employee %s] %s (%s) - Salary: $%.2f%n", indent, id, name, role, salary);
        }
    }

    // -------------------------------------------------------------
    // Composite Node (Department / Division Container)
    // -------------------------------------------------------------

    public static class Department implements OrganizationComponent {
        private final String departmentName;
        private final List<OrganizationComponent> children = new ArrayList<>();

        public Department(String departmentName) {
            this.departmentName = departmentName;
        }

        @Override
        public void add(OrganizationComponent component) {
            children.add(component);
        }

        @Override
        public void remove(OrganizationComponent component) {
            children.remove(component);
        }

        @Override
        public String getName() { return departmentName; }

        /**
         * Recursively calculates total salary budget across all child leaves and sub-composites.
         */
        @Override
        public double calculateSalaryBudget() {
            double totalBudget = 0;
            for (OrganizationComponent child : children) {
                totalBudget += child.calculateSalaryBudget(); // Recursive delegation!
            }
            return totalBudget;
        }

        @Override
        public void printDetails(String indent) {
            System.out.printf("%s+ [Department] %s (Sub-components: %d, Total Budget: $%.2f)%n",
                    indent, departmentName, children.size(), calculateSalaryBudget());
            for (OrganizationComponent child : children) {
                child.printDetails(indent + "  "); // Recurse depth formatting
            }
        }
    }
}
