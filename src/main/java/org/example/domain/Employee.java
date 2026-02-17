package org.example.domain;

public class Employee implements Comparable<Employee> {
    private String employeeName;
    private String employeeID;
    private String designation;
    private double salary;

    private Employee() {
        super();
    }

    // Constructors
    public Employee(String employeeName, String employeeID, String designation, double salary) {
        this.employeeName = employeeName;
        this.employeeID = employeeID;
        this.designation = designation;
        this.salary = salary;
    }

    // Getters and Setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    // toString Method
    @Override
    public String toString() {
        return "Employee{" +
                "employeeName='" + employeeName + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", designation='" + designation + '\'' +
                ", salary=" + salary +
                '}';
    }

    public void salaryIncrement(int hikePercent) {
        this.salary += (salary * hikePercent/100);
    }

    public static Employee getInstance() {
        Employee employee = new Employee();
        employee.setEmployeeID("NA");
        return employee;
    }

    @Override public int compareTo(Employee other) { return this.employeeName.compareTo(other.employeeName); }
}
