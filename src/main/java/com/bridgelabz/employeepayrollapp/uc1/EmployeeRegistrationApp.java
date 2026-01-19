package com.bridgelabz.employeepayrollapp.uc1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Scanner;

/*
 * ======================================================
 * USE CASE 1: EMPLOYEE REGISTRATION
 * ======================================================
 *
 * Goal of this Use Case:
 * - Understand how multiple classes work together
 * - Learn how objects are created and used
 * - See how a real-world problem is broken into small parts
 *
 * At this stage, focus on:
 * - What each class represents
 * - How main() coordinates the flow
 */

/*
 * ---------------- Custom Exception ----------------
 *
 * This class represents a validation-related problem.
 *
 * Why this exists:
 * - Instead of stopping the program abruptly,
 *   we clearly communicate what went wrong.
 *
 * For now, think of this as:
 * "A special error we throw when input is invalid"
 */
class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

/*
 * ---------------- Validator Class ----------------
 *
 * This class is responsible ONLY for checking input correctness.
 *
 * Why we separate validation:
 * - Keeps main() clean and readable
 * - Avoids repeating validation logic
 *
 * Important idea:
 * - Validation logic does NOT belong to Employee
 * - Validation happens BEFORE objects are created
 */
class Validator {

    /*
     * Checks whether an email follows a valid format.
     *
     * If the format is wrong:
     * - A ValidationException is thrown
     * - Program flow jumps to the catch block in main()
     */
    public static void validateEmail(String email) throws ValidationException {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(regex, email)) {
            throw new ValidationException("Invalid Email Format!");
        }
    }

    /*
     * Validates Indian phone numbers.
     *
     * Rule:
     * - Must start with 6, 7, 8, or 9
     * - Must be exactly 10 digits
     */
    public static void validatePhone(String phone) throws ValidationException {
        String regex = "^[6-9]\\d{9}$";
        if (!Pattern.matches(regex, phone)) {
            throw new ValidationException("Invalid Indian Phone Number!");
        }
    }

    /*
     * Validates Employee ID format.
     *
     * Rule:
     * - Must follow EMP-XXXX where X is a digit
     */
    public static void validateEmpId(String empId) throws ValidationException {
        String regex = "EMP-\\d{4}";
        if (!Pattern.matches(regex, empId)) {
            throw new ValidationException("Invalid Employee ID! Expected format: EMP-XXXX");
        }
    }
}

/*
 * ---------------- UserAccount Class ----------------
 *
 * This class represents login-related information.
 *
 * Why this is a separate class:
 * - Employee details and login details are different concerns
 * - Keeps responsibilities small and clear
 *
 * This introduces the idea of COMPOSITION:
 * - An Employee HAS a UserAccount
 */
class UserAccount {
    private String username;
    private String password;   // Plain text for now (will improve later)

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /*
     * Getter exposes only what is necessary.
     * Internal data is protected.
     */
    public String getUsername() {
        return username;
    }

    /*
     * Used only for display purposes.
     */
    @Override
    public String toString() {
        return "UserAccount{username='" + username + "'}";
    }
}

/*
 * ---------------- Employee Class ----------------
 *
 * This class represents an Employee entity.
 *
 * Core OOP concept introduced here:
 * - Encapsulation
 *
 * Data is kept private and controlled through the class.
 */
class Employee {

    private String empId;
    private String name;
    private String email;
    private String phone;

    // Employee HAS a UserAccount
    private UserAccount account;

    /*
     * Constructor is used to create a fully initialized Employee object.
     *
     * Important idea:
     * - Object creation happens only after validation succeeds
     */
    public Employee(String empId, String name, String email, String phone, UserAccount account) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.account = account;
    }

    /*
     * Converts Employee data into a readable format.
     *
     * This avoids printing details manually in main().
     */
    @Override
    public String toString() {
        return "Employee Registered Successfully:\n" +
                "Employee ID : " + empId + "\n" +
                "Name        : " + name + "\n" +
                "Email       : " + email + "\n" +
                "Phone       : " + phone + "\n" +
                "Username    : " + account.getUsername();
    }

    /*
     * Saves employee data into a file.
     *
     * Purpose:
     * - Simulates persistence
     * - Shows that objects can manage their own data
     *
     */
    public void persist() throws IOException {
        FileWriter writer = new FileWriter("employee_data.txt", true);
        writer.write(empId + "," + name + "," + email + "," + phone + "," + account.getUsername() + "\n");
        writer.close();
    }
}

/*
 * ---------------- Main Class ----------------
 *
 * Entry point of Use Case 1.
 *
 * Execution Flow:
 * 1. Take input from user
 * 2. Validate input
 * 3. Create objects
 * 4. Persist data
 * 5. Display confirmation
 *
 * @author Developer
 * @version 1.0
 */
public class EmployeeRegistrationApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== USE CASE 1: EMPLOYEE REGISTRATION ===");

        try {
            System.out.print("Enter Employee ID (EMP-XXXX): ");
            String empId = sc.nextLine();
            Validator.validateEmpId(empId);

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            Validator.validateEmail(email);

            System.out.print("Enter Phone (10 digits starting 6-9): ");
            String phone = sc.nextLine();
            Validator.validatePhone(phone);

            System.out.print("Create Username: ");
            String username = sc.nextLine();

            System.out.print("Create Password: ");
            String password = sc.nextLine();

            /*
             * Objects are created only after all validations succeed.
             *
             * This ensures system consistency.
             */
            UserAccount account = new UserAccount(username, password);
            Employee employee = new Employee(empId, name, email, phone, account);

            // Save employee details
            employee.persist();

            // Confirmation output
            System.out.println("\n----------------------------------");
            System.out.println(employee);
            System.out.println("\nData persisted in file: employee_data.txt");
            System.out.println("----------------------------------");

        } catch (ValidationException e) {
            System.out.println("\nValidation Failed: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\nError saving employee data!");
        }
    }
}
