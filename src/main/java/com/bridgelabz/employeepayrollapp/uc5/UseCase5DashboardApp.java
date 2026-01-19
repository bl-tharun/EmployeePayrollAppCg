package com.bridgelabz.employeepayrollapp.uc5;

import java.util.*;

/*
 * ======================================================
 * USE CASE 5: DASHBOARD DISPLAY
 * ======================================================
 *
 * Goal of this Use Case:
 * - Display different dashboards based on user role
 * - Introduce interfaces and runtime behavior selection
 * - Show how the same data can be presented differently
 *
 * New ideas introduced in UC5:
 * - Interface
 * - Multiple implementations
 * - Factory for object creation
 *
 * This use case builds on:
 * - UC3: Payslip data
 * - UC4: Read-only views
 */

// =============== Payslip Class ===================
/*
 * Payslip represents a simplified salary record.
 *
 * This version contains only:
 * - Month
 * - Net Pay
 *
 * Why simplified:
 * - Dashboard does not need full payslip details
 * - Keeps focus on display logic
 */
class Payslip {
    private String month;
    private double netPay;

    public Payslip(String month, double netPay) {
        this.month = month;
        this.netPay = netPay;
    }

    public String getMonth() {
        return month;
    }

    public double getNetPay() {
        return netPay;
    }

    public String toString() {
        return month + " : " + netPay;
    }
}

// =============== Employee Class ===================
/*
 * Employee represents the logged-in user.
 *
 * Only identity details are required for dashboard display.
 */
class Employee {
    private String empId;
    private String name;

    public Employee(String empId, String name) {
        this.empId = empId;
        this.name = name;
    }

    public String getEmpId() { return empId; }
    public String getName() { return name; }
}

// =============== Dashboard Interface ===================
/*
 * Dashboard defines a common contract for all dashboards.
 *
 * Key idea introduced:
 * - Interface
 *
 * Why an interface:
 * - Different dashboards exist
 * - All dashboards must provide a display() method
 * - Caller should not depend on concrete implementations
 */
interface Dashboard {
    void display(ArrayList payslips, Employee employee);
}

// =============== Employee Dashboard ===================
/*
 * EmployeeDashboard provides a personal view of payslip data.
 *
 * Focus:
 * - Recent payslips
 * - Year-to-date earnings
 */
class EmployeeDashboard implements Dashboard {

    /*
     * Displays employee-specific dashboard.
     *
     * Steps performed:
     * - Sort payslips
     * - Display top entries
     * - Calculate total earnings
     */
    public void display(ArrayList payslips, Employee employee) {

        System.out.println("\n=== EMPLOYEE DASHBOARD ===");
        System.out.println("Welcome, " + employee.getName());

        // Display runtime implementation information
        System.out.println("Dashboard Type: " + this.getClass().getName());

        // Sort payslips in descending order of net pay
        Collections.sort(payslips, new Comparator() {
            public int compare(Object o1, Object o2) {
                Payslip p1 = (Payslip) o1;
                Payslip p2 = (Payslip) o2;
                return (int)(p2.getNetPay() - p1.getNetPay());
            }
        });

        // Display only top 3 payslips
        System.out.println("\nRecent Payslips (Top 3):");
        int count = 0;
        Iterator it = payslips.iterator();
        while (it.hasNext() && count < 3) {
            Payslip p = (Payslip) it.next();
            System.out.println(p);
            count++;
        }

        // Calculate Year-To-Date earnings
        double total = 0;
        Iterator it2 = payslips.iterator();
        while (it2.hasNext()) {
            Payslip p = (Payslip) it2.next();
            total += p.getNetPay();
        }

        System.out.println("\nYear-To-Date Earnings: " + total);
    }
}

// =============== Manager Dashboard ===================
/*
 * ManagerDashboard provides an aggregate view.
 *
 * Focus:
 * - Overall earnings
 * - Summary-level information
 */
class ManagerDashboard implements Dashboard {

    /*
     * Displays manager-specific dashboard.
     *
     * Only aggregation logic is applied here.
     */
    public void display(ArrayList payslips, Employee employee) {

        System.out.println("\n=== MANAGER DASHBOARD ===");
        System.out.println("Manager: " + employee.getName());

        System.out.println("Dashboard Type: " + this.getClass().getName());

        // Calculate total team earnings
        double total = 0;
        Iterator it = payslips.iterator();
        while (it.hasNext()) {
            Payslip p = (Payslip) it.next();
            total += p.getNetPay();
        }

        System.out.println("\nTeam Total YTD Earnings: " + total);
    }
}

// =============== Dashboard Factory ===================
/*
 * DashboardFactory is responsible for creating dashboards.
 *
 * Key idea introduced:
 * - Factory pattern
 *
 * Why this is needed:
 * - Object creation logic is centralized
 * - main() does not need to know concrete classes
 */
class DashboardFactory {

    public static Dashboard getDashboard(String role) {

        if ("EMPLOYEE".equals(role)) {
            return new EmployeeDashboard();

        } else if ("MANAGER".equals(role)) {
            return new ManagerDashboard();
        }

        return null;
    }
}

// =============== Main App ===================
/*
 * Main runner class for Use Case 5.
 *
 * Role of main():
 * - Collect user input
 * - Prepare data
 * - Request appropriate dashboard
 * - Display dashboard
 */
public class UseCase5DashboardApp {

    /**
     * Entry point for dashboard display.
     *
     * Execution Flow:
     * 1. Capture employee details
     * 2. Prepare payslip data
     * 3. Select dashboard at runtime
     * 4. Display dashboard output
     *
     * @author Developer
     * @version 5.0
     */
    public static void main(String[] args) {

        System.out.println("=== USE CASE 5: DASHBOARD DISPLAY ===");

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Employee ID: ");
        String id = sc.nextLine();

        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Role (EMPLOYEE/MANAGER): ");
        String role = sc.nextLine();

        Employee emp = new Employee(id, name);

        // Sample payslip data for demonstration
        ArrayList payslips = new ArrayList();
        payslips.add(new Payslip("Jan", 30000));
        payslips.add(new Payslip("Feb", 32000));
        payslips.add(new Payslip("Mar", 31000));
        payslips.add(new Payslip("Apr", 33000));
        payslips.add(new Payslip("May", 34000));

        // Request dashboard based on role
        Dashboard dashboard = DashboardFactory.getDashboard(role);

        if (dashboard != null) {
            dashboard.display(payslips, emp);
        } else {
            System.out.println("Invalid Role");
        }
    }
}

