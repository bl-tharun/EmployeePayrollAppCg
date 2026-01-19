package com.bridgelabz.employeepayrollapp.uc3;

import java.util.Scanner;

/*
 * ======================================================
 * USE CASE 3: PAYSLIP GENERATION
 * ======================================================
 *
 * Goal of this Use Case:
 * - Understand how multiple objects collaborate
 * - Learn HAS-A relationships between classes
 * - Separate calculation logic from data representation
 *
 * New ideas introduced in UC3:
 * - Aggregation
 * - Composition
 * - Service class for business logic
 *
 * This use case builds on:
 * - UC1: Object creation
 * - UC2: Object roles and responsibilities
 */

// ============== Employee (Aggregation) =================
/*
 * Employee represents basic employee identity.
 *
 * Why this class exists:
 * - Payslip needs employee details
 * - Employee can exist independently of Payslip
 *
 * This introduces AGGREGATION:
 * - Payslip uses Employee
 * - Employee is NOT owned by Payslip
 */
class Employee {

    private String empId;
    private String name;

    public Employee(String empId, String name) {
        this.empId = empId;
        this.name = name;
    }

    public String getEmpId() {
        return empId;
    }

    public String getName() {
        return name;
    }
}

// ============== Salary Components (Composition) =================
/*
 * SalaryComponents groups all salary-related values.
 *
 * Why this class exists:
 * - Salary details belong together
 * - Keeps Payslip clean and readable
 *
 * This introduces COMPOSITION:
 * - Payslip owns SalaryComponents
 * - SalaryComponents has no meaning without Payslip
 */
class SalaryComponents {

    double basicSalary;
    double hra;
    double da;
    double allowances;
    double pf;
    double tax;
    double netPay;

    /*
     * Constructor initializes only earnings.
     *
     * Deductions and net pay are calculated later.
     */
    public SalaryComponents(double basicSalary, double hra, double da, double allowances) {
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.allowances = allowances;
    }
}

// ============== Payslip =================
/*
 * Payslip represents a monthly salary statement.
 *
 * It combines:
 * - Employee details (aggregation)
 * - Salary details (composition)
 *
 * Payslip acts as a READ-ONLY view once created.
 */
class Payslip {

    private Employee employee;               // Aggregation
    private SalaryComponents components;     // Composition
    private String month;

    public Payslip(Employee employee, SalaryComponents components, String month) {
        this.employee = employee;
        this.components = components;
        this.month = month;
    }

    /*
     * Formats payslip information into a readable output.
     *
     * This avoids printing logic in main() or service classes.
     */
    public String toString() {
        return "\n=========== PAYSLIP ===========\n"
                + "Month        : " + month + "\n"
                + "Employee ID  : " + employee.getEmpId() + "\n"
                + "Employee Name: " + employee.getName() + "\n\n"
                + "---- Earnings ----\n"
                + "Basic Salary  : " + components.basicSalary + "\n"
                + "HRA           : " + components.hra + "\n"
                + "DA            : " + components.da + "\n"
                + "Allowances    : " + components.allowances + "\n\n"
                + "---- Deductions ----\n"
                + "PF            : " + components.pf + "\n"
                + "Tax           : " + components.tax + "\n\n"
                + "Net Pay       : " + components.netPay + "\n"
                + "==============================\n";
    }
}

// ============== Payroll Service =================
/*
 * PayrollService contains salary calculation logic.
 *
 * Why this class exists:
 * - Business rules should not be inside main()
 * - Keeps calculations reusable and isolated
 *
 * This introduces the idea of a SERVICE class.
 */
class PayrollService {

    /*
     * Generates a payslip by:
     * - Creating salary components
     * - Applying calculation rules
     * - Returning a completed Payslip object
     */
    public Payslip generatePayslip(Employee employee, String month,
                                   double basic, double hra, double da, double allowances) {

        SalaryComponents sc = new SalaryComponents(basic, hra, da, allowances);

        // ---- Gross Salary Calculation ----
        double gross = basic + hra + da + allowances;

        // ---- Deductions ----
        sc.pf = basic * 0.12;         // Provident Fund (12%)
        sc.tax = gross * 0.10;        // Income Tax (10%) – demo rule

        // ---- Net Pay ----
        sc.netPay = gross - (sc.pf + sc.tax);

        return new Payslip(employee, sc, month);
    }
}

// ============== MAIN APP =================
/*
 * Main runner class for Use Case 3.
 *
 * Role of main():
 * - Collect input
 * - Create required objects
 * - Delegate calculations
 * - Display final output
 *
 * main() does NOT perform calculations itself.
 */
public class UseCase3PayslipApp {

    /**
     * Entry point for payslip generation.
     *
     * Execution Flow:
     * 1. Capture employee details
     * 2. Capture salary components
     * 3. Generate payslip via service
     * 4. Display formatted payslip
     *
     * @author Developer
     * @version 3.0
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== USE CASE 3: PAYSLIP GENERATION ===");

        // Employee details (assumed authenticated earlier)
        System.out.print("Enter Employee ID: ");
        String empId = sc.nextLine();

        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();

        Employee employee = new Employee(empId, name);

        System.out.print("Enter Month (e.g., January 2026): ");
        String month = sc.nextLine();

        // Salary inputs
        System.out.print("Enter Basic Salary: ");
        double basic = sc.nextDouble();

        System.out.print("Enter HRA: ");
        double hra = sc.nextDouble();

        System.out.print("Enter DA: ");
        double da = sc.nextDouble();

        System.out.print("Enter Allowances: ");
        double allowances = sc.nextDouble();

        PayrollService payrollService = new PayrollService();

        Payslip payslip = payrollService.generatePayslip(
                employee,
                month,
                basic,
                hra,
                da,
                allowances
        );

        // Display final payslip
        System.out.println(payslip);
    }
}

