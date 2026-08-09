package org.example.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.domain.Employee;

/**
 * Demonstrates Java Stream operations: filtering, mapping, sorting, grouping,
 * partitioning, statistics, and null-safe comparator construction.
 */
public class StreamImplementation {

    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Alice", "E001", "Investment Advisor", 75000));
        employees.add(new Employee("Bob", "E002", "Developer", 55000));
        employees.add(new Employee("Pradeep", "E003", "Personal Banker", 39000));
        employees.add(new Employee("David", "E004", "QA Engineer", 39000));
        employees.add(new Employee("Sunita", "E005", "Branch Manager", 60001));
        employees.add(new Employee("Frank", "E006", "Developer", 55001));
        employees.add(new Employee("Damayanti", "E007", "Wealth Manager", 80000));
        employees.add(new Employee("Prasanna", "E008", "Compliance Officer", 55000));
        employees.add(new Employee("Ivan", "E009", "Scrum Master", 62000));
        employees.add(new Employee("Soumyarani", "E010", "Risk Manager", 55000));

        // 1. High-salary filter before increments
        System.out.println("--- Employees with Salary > 60k ---");
        employees.stream()
                .filter(x -> x.getSalary() > 60000)
                .forEach(x -> System.out.print(x.getEmployeeName() + ", "));
        System.out.println();

        // 2. In-place salary increment calculation
        employees.forEach(e -> {
            if (e.getSalary() > 60000) {
                e.salaryIncrement(2);
            } else if (e.getSalary() > 50000 && e.getSalary() < 60000) {
                e.salaryIncrement(10);
            }
        });

        // 3. Null-safe Sorting using Comparator.nullsLast
        employees.sort(Comparator.comparing(Employee::getSalary));
        System.out.println("\n--- Sorted Employees by Salary ---");
        employees.forEach(e -> System.out.printf("%s (%s): $%.2f%n", e.getEmployeeName(), e.getEmployeeID(), e.getSalary()));

        // Null-safe reverse sorting on Employee Name
        List<Employee> reversedByName = employees.stream()
                .sorted(Comparator.comparing(Employee::getEmployeeName, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        System.out.println("\nFirst high-earner after reverse name sort: " +
                reversedByName.stream()
                        .filter(x -> x.getSalary() > 60000)
                        .findFirst()
                        .map(Employee::getEmployeeID)
                        .orElse("NONE"));

        // 4. Grouping & Partitioning
        Map<Double, Long> salaryFrequencyMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getSalary, Collectors.counting()));
        System.out.println("\nSalary Frequency: " + salaryFrequencyMap);

        Map<Boolean, Long> partitionBySalary = employees.stream()
                .collect(Collectors.partitioningBy(x -> x.getSalary() > 60000, Collectors.counting()));
        System.out.println("Partitioned by Salary > 60k: " + partitionBySalary);

        // 5. Min, Max, Sum, and Summarizing Statistics
        DoubleSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.printf("%nSalary Stats -> Count: %d, Min: $%.2f, Max: $%.2f, Avg: $%.2f, Sum: $%.2f%n",
                stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage(), stats.getSum());

        // 6. Partitioning vs Grouping Comparison
        System.out.println("\n--- Partitioning vs Grouping ---");
        Map<Boolean, Long> evenOddPartition = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() % 2 == 0, Collectors.counting()));
        System.out.println("Partition (Even Salary boolean): " + evenOddPartition);

        Map<String, Long> evenOddGroup = employees.stream()
                .collect(Collectors.groupingBy(e -> (e.getSalary() % 2 == 0) ? "EVEN" : "ODD", Collectors.counting()));
        System.out.println("Grouping (Even/Odd String): " + evenOddGroup);
    }
}