package org.example.streams;

import org.example.domain.Employee;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Demonstrates several Java Stream operations on a sample list of employees.
 *
 * The class shows filtering, mapping, sorting, grouping, partitioning, and
 * statistical collection operations using the Java Streams API.
 */
public class StreamImplementation {
    /**
     * Entry point for the Stream demonstration.
     *
     * Builds a sample employee list, applies salary updates, and prints
     * outputs that illustrate common stream and collection operations.
     */
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(null, "E001", "Investment Advisor", 75000));
        employees.add(new Employee("Bob", "E002", "Developer", 55000));
        employees.add(new Employee("Pradeep", "E003", "Personal Banker", 39000));
        employees.add(new Employee("David", "E004", "QA Engineer", 39000));
        employees.add(new Employee("Sunita", "E005", "Branch Manager", 60001));
        employees.add(new Employee("Frank", "E006", "Developer", 55001));
        employees.add(new Employee("Damayanti", "E007", "Wealth Manager", 80000));
        employees.add(new Employee("Prasanna", "E008", "Compliance Officer", 55000));
        employees.add(new Employee("Ivan", "E009", "Scrum Master", 62000));
        employees.add(new Employee("Soumyarani", "E010", "Risk Manager", 55000));

        // Print high-salary employees before salary updates.
        employees.stream().filter(x -> x.getSalary() > 60000)
                .forEach(x -> System.out.print(x.getEmployeeName() + ", "));

        // Apply salary increments in-place based on the current salary band.
        employees.forEach(e -> {
            if (e.getSalary() > 60000)
                e.salaryIncrement(2);
            else if (e.getSalary() > 50000 && e.getSalary() < 60000) e.salaryIncrement(10);
        });

        // Print congratulatory messages for employees who still earn above 60k.
        employees.stream().filter(x -> x.getSalary() > 60000)
                .forEach(x -> System.out.print("\nCongratulation " + x.getEmployeeName() +
                        " with your salary " + x.getSalary() +
                        ", You can avail pioneer credit card."));

        //This line needs Comparable interface to be implemented by Employee.
        /*Stream<Employee> sortedEmployees = employees.stream().sorted();
        System.out.print("\n--------------\n");
        sortedEmployees.forEach(x -> System.out.println(x.getEmployeeName()));
        System.out.print("\n--------------\n");
        //reverse sort on the fly using Comparator*/
        employees.sort(Comparator.comparing(Employee::getSalary));
        System.out.println(employees);
        Stream<Employee> reversedSortedEmployees = employees.stream().sorted(Comparator.comparing(Employee::getEmployeeName).reversed());

        Employee employee = reversedSortedEmployees.filter(x -> x.getSalary() > 60000).findFirst().orElse(Employee.getInstance());
        System.out.println(employee.getEmployeeID());

        Map<Double, Long> employeeMapBasedOnSalary = employees.stream()
                .collect(Collectors.groupingBy(Employee::getSalary, Collectors.counting()));
        System.out.println(employeeMapBasedOnSalary);

        Map<Boolean, Long> employeesGreaterThan60KSalary = employees.stream()
                .collect(Collectors.partitioningBy(x -> x.getSalary() > 60000, Collectors.counting()));
        System.out.println(employeesGreaterThan60KSalary);

        Comparator<Employee> salaryComparator = Comparator.comparing(Employee::getSalary);

        Employee minSal = employees.stream().min(salaryComparator).orElse(null);
        System.out.println(minSal);

        System.out.println(employees.stream().mapToDouble(Employee::getSalary).sum());
        System.out.println(employees.stream().mapToDouble(Employee::getSalary).reduce(10000, Double::sum));
        System.out.println(employees.stream()
                .collect(Collectors.summarizingDouble(Employee::getSalary)));

        /**
         * When to use which
         * - Use partitioningBy when the classifier is boolean and you want a guaranteed two-way split.
         * It’s clearer and communicates intent better.
         * - Use groupingBy when the classifier can have more than two values
         * (e.g., department, salary range, job title). It’s more general.
         *
         * partitioningBy
         * - Special case of grouping optimized for boolean classifiers.
         * - Always produces a Map<Boolean, ...> with two keys: true and false.
         * - Even if one bucket is empty, the key is still present.
         * - Best when you know you’re splitting into two categories (like odd/even, pass/fail, active/inactive).
         * groupingBy
         * - General-purpose grouping by any classifier (String, Enum, Integer, etc.).
         * - Produces a Map<K, ...> where K is whatever your function returns.
         * - Keys are created only for values that actually occur.
         * - More flexible — can handle multiple groups, not just two
         */
        System.out.println(employees.stream().collect(
                Collectors.partitioningBy(e -> e.getSalary() % 2 == 0, Collectors.mapping(Employee::getEmployeeName, Collectors.counting()))));
        System.out.println(employees.stream().collect(
                Collectors.groupingBy(e -> {
                    if(e.getSalary() % 2 == 0) return "even";
                    else return "odd";
                }, Collectors.mapping(Employee::getEmployeeID, Collectors.counting()))));


    }
}