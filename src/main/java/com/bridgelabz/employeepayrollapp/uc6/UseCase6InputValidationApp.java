package com.bridgelabz.employeepayrollapp.uc6;

import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * ======================================================
 * USE CASE 6: INPUT VALIDATION
 * ======================================================
 *
 * Goal of this Use Case:
 * - Validate user input before it enters the system
 * - Centralize validation logic
 * - Learn how exceptions are used to handle invalid data
 *
 * New ideas introduced in UC6:
 * - Exception hierarchy
 * - Custom checked exceptions
 * - Fail-fast validation
 *
 * This use case brings together lessons from:
 * - UC1: Input handling
 * - UC2: Controlled program flow
 * - UC3–UC5: Clean separation of responsibilities
 */

// =============== Base Exception ==================
/*
 * ValidationException is the base class for all validation errors.
 *
 * Why this class exists:
 * - All validation-related problems belong to one category
 * - Allows a single catch block to handle all validation failures
 *
 * This introduces the idea of an exception hierarchy.
 */
class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

// =============== Child Exceptions ==================
/*
 * Each child exception represents a specific validation failure.
 *
 * Why separate exception types:
 * - Clear identification of error cause
 * - Better readability and maintainability
 */

class EmailValidationException extends ValidationException {
    public EmailValidationException(String message) {
        super(message);
    }
}

class PhoneValidationException extends ValidationException {
    public PhoneValidationException(String message) {
        super(message);
    }
}

class PasswordValidationException extends ValidationException {
    public PasswordValidationException(String message) {
        super(message);
    }
}

class EmployeeIdValidationException extends ValidationException {
    public EmployeeIdValidationException(String message) {
        super(message);
    }
}

// =============== Validation Service ==================
/*
 * ValidationService contains all input validation rules.
 *
 * Why this class exists:
 * - Validation logic should not be scattered
 * - Keeps main() clean and focused on flow
 *
 * This class represents a defensive boundary
 * between user input and application logic.
 */
class ValidationService {

    /*
     * Sanitizes input before validation.
     *
     * Purpose:
     * - Remove accidental spaces
     * - Ensure consistent validation behavior
     */
    private static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replace(" ", "");
    }

    /*
     * Validates email format.
     *
     * Rule:
     * - Must follow standard email structure
     *
     * Throws EmailValidationException if invalid.
     */
    public static void validateEmail(String email) throws EmailValidationException {

        email = sanitize(email);

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!Pattern.matches(regex, email)) {
            throw new EmailValidationException(
                    "❌ Invalid email format. Example: abc@gmail.com"
            );
        }
    }

    /*
     * Validates Indian phone number.
     *
     * Rule:
     * - Exactly 10 digits
     * - Must start with 6, 7, 8, or 9
     */
    public static void validatePhone(String phone) throws PhoneValidationException {

        phone = sanitize(phone);

        String regex = "^[6-9][0-9]{9}$";

        if (!Pattern.matches(regex, phone)) {
            throw new PhoneValidationException(
                    "❌ Invalid phone number. Must be 10 digits starting from 6–9"
            );
        }
    }

    /*
     * Validates password strength.
     *
     * Rules enforced:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     */
    public static void validatePassword(String password)
            throws PasswordValidationException {

        password = sanitize(password);

        String regex =
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%!]).{8,}$";

        if (!Pattern.matches(regex, password)) {
            throw new PasswordValidationException(
                    "❌ Weak password. Must contain:\n" +
                            "- 8 or more characters\n" +
                            "- uppercase letter\n" +
                            "- lowercase letter\n" +
                            "- number\n" +
                            "- special symbol (@ # $ % !)"
            );
        }
    }

    /*
     * Validates employee ID format.
     *
     * Rule:
     * - Must follow EMP-XXXX format
     */
    public static void validateEmployeeId(String empId)
            throws EmployeeIdValidationException {

        empId = sanitize(empId);

        String regex = "^EMP-[0-9]{4}$";

        if (!Pattern.matches(regex, empId)) {
            throw new EmployeeIdValidationException(
                    "❌ Invalid Employee ID. Expected format: EMP-1234"
            );
        }
    }
}

// =============== MAIN APP ==================
/*
 * Main runner class for Use Case 6.
 *
 * Role of main():
 * - Capture user input
 * - Delegate validation
 * - Handle validation failures gracefully
 *
 * main() does NOT perform validation itself.
 */
public class UseCase6InputValidationApp {

    /**
     * Entry point for input validation use case.
     *
     * Execution Flow:
     * 1. Read user inputs
     * 2. Validate each input
     * 3. Stop immediately if validation fails
     * 4. Proceed only when all inputs are valid
     *
     * @author Developer
     * @version 6.0
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== USE CASE 6: INPUT VALIDATION ===");

        try {
            System.out.print("Enter Employee ID (EMP-XXXX): ");
            String empId = sc.nextLine();
            ValidationService.validateEmployeeId(empId);

            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            ValidationService.validateEmail(email);

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();
            ValidationService.validatePhone(phone);

            System.out.print("Create Password: ");
            String password = sc.nextLine();
            ValidationService.validatePassword(password);

            System.out.println(
                    "\n✅ All inputs are VALID. Registration/Login can proceed."
            );

        } catch (ValidationException ex) {
            // Single catch block handles all validation failures
            System.out.println("\n⚠ Validation Failed:");
            System.out.println(ex.getMessage());
        }
    }
}

