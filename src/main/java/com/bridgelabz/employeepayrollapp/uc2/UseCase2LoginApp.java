package com.bridgelabz.employeepayrollapp.uc2;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.security.MessageDigest;

/*
 * ======================================================
 * USE CASE 2: EMPLOYEE AUTHENTICATION & LOGIN
 * ======================================================
 *
 * Goal of this Use Case:
 * - Introduce inheritance and polymorphism
 * - Show how different user types share common behavior
 * - Demonstrate a simple authentication flow
 *
 * New ideas introduced here:
 * - Abstract class
 * - Method overriding
 * - Runtime decision-making
 *
 * This use case builds directly on UC1.
 */

// ================= Password Hash Utility ==================
/*
 * This utility class handles password hashing.
 *
 * Why this exists:
 * - Passwords should not be stored or compared directly
 * - We convert passwords into a secure representation
 *
 * At this stage:
 * - Focus on the idea of hashing
 * - Do NOT worry about cryptography details
 */
class PasswordUtil {

    /*
     * Converts a plain-text password into a hashed value.
     *
     * Same input → same hash
     * Original password cannot be derived back
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(
                        Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                                .substring(1)
                );
            }
            return sb.toString();

        } catch (Exception e) {
            // Simple fallback to keep demo running
            return password;
        }
    }
}

// ================= Abstract User Class ==================
/*
 * User represents a generic system user.
 *
 * Key concept introduced:
 * - Abstract class
 *
 * Why this class exists:
 * - Different users exist (Employee, Manager)
 * - They share common data and behavior
 * - But authentication logic must be defined by each type
 */
abstract class User {

    protected String username;
    protected String passwordHash;
    protected String role;

    /*
     * Constructor initializes shared user data.
     *
     * Password is immediately hashed.
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.passwordHash = PasswordUtil.hash(password);
        this.role = role;
    }

    /*
     * Abstract method:
     * - Forces subclasses to provide authentication logic
     * - Enables runtime method selection
     */
    public abstract boolean authenticate(String username, String password);

    public String getRole() {
        return role;
    }
}

// ================= Regular Employee ==================
/*
 * Represents a regular employee user.
 *
 * Inheritance:
 * - RegularEmployee IS-A User
 *
 * This class provides its own implementation
 * of the authenticate() method.
 */
class RegularEmployee extends User {

    public RegularEmployee(String username, String password) {
        super(username, password, "EMPLOYEE");
    }

    /*
     * Authentication logic specific to regular employees.
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username)
                && this.passwordHash.equals(PasswordUtil.hash(password));
    }
}

// ================= Manager ==================
/*
 * Represents a manager user.
 *
 * This class also extends User,
 * but is treated differently based on role.
 */
class Manager extends User {

    public Manager(String username, String password) {
        super(username, password, "MANAGER");
    }

    /*
     * Manager authentication logic.
     *
     * Method signature is same as parent,
     * but implementation belongs to this class.
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username)
                && this.passwordHash.equals(PasswordUtil.hash(password));
    }
}

// ================= Session Management ==================
/*
 * Session represents a logged-in user state.
 *
 * Why this class exists:
 * - Login is not permanent
 * - Session has a lifetime
 *
 * This introduces the idea of time-based state.
 */
class Session {

    private String username;
    private long loginTime;
    private long timeoutMillis;

    public Session(String username) {
        this.username = username;
        this.loginTime = System.currentTimeMillis();
        this.timeoutMillis = 2 * 60 * 1000;   // 2 minutes
    }

    /*
     * Checks whether the session is still valid.
     */
    public boolean isExpired() {
        long current = System.currentTimeMillis();
        return (current - loginTime) > timeoutMillis;
    }

    public String toString() {
        return "Session active for user: " + username;
    }
}

// ================= Authentication Service ==================
/*
 * AuthenticationService handles login-related operations.
 *
 * Why this class exists:
 * - main() should not contain authentication logic
 * - Centralizes login rules and limits
 */
class AuthenticationService {

    /*
     * Map stores users using username as key.
     *
     * This simulates a data store.
     */
    private Map<String, User> users = new HashMap();
    private int maxAttempts = 3;

    public AuthenticationService() {

        /*
         * Predefined users for demonstration.
         *
         * Important idea:
         * - Different object types stored as User
         * - Actual behavior depends on runtime type
         */
        users.put("emp1", new RegularEmployee("emp1", "Emp@1234"));
        users.put("manager1", new Manager("manager1", "Mng@1234"));
    }

    /*
     * Handles the complete login flow.
     *
     * Steps:
     * 1. Accept credentials
     * 2. Validate using polymorphism
     * 3. Create session
     * 4. Show dashboard
     */
    public Session login() {

        Scanner sc = new Scanner(System.in);
        int attempts = 0;

        while (attempts < maxAttempts) {

            System.out.print("\nEnter Username: ");
            String username = sc.nextLine();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            User user = users.get(username);

            /*
             * Polymorphism in action:
             * - authenticate() call is resolved at runtime
             */
            if (user != null && user.authenticate(username, password)) {

                System.out.println("\nLogin Successful!");
                System.out.println("Role: " + user.getRole());

                Session session = new Session(username);

                showDashboard(user.getRole());

                return session;
            }

            attempts++;
            System.out.println("Login Failed. Attempts remaining: " + (maxAttempts - attempts));
        }

        System.out.println("\nAccount temporarily locked due to 3 failed attempts.");
        return null;
    }

    /*
     * Displays dashboard based on user role.
     *
     * Role-based behavior is introduced here
     * in a simple and readable way.
     */
    private void showDashboard(String role) {

        System.out.println("\n======= DASHBOARD =======");

        if ("EMPLOYEE".equals(role)) {
            System.out.println("Employee Dashboard");
            System.out.println("View Payslip | Update Profile");

        } else if ("MANAGER".equals(role)) {
            System.out.println("Manager Dashboard");
            System.out.println("Approve Payslip | View Team Summary");
        }
    }
}

// ================= Main Class ==================
/*
 * Main runner class for Use Case 2.
 *
 * This class coordinates the login flow
 * without implementing the logic itself.
 */
public class UseCase2LoginApp {

    /**
     * Entry point for authentication use case.
     *
     * Execution Flow:
     * - Trigger login
     * - Receive session
     * - Validate session state
     *
     * @author Developer
     * @version 2.0
     */
    public static void main(String[] args) {

        System.out.println("=== USE CASE 2: EMPLOYEE AUTHENTICATION & LOGIN ===");

        AuthenticationService auth = new AuthenticationService();
        Session session = auth.login();

        if (session != null) {

            System.out.println("\n" + session.toString());

            if (session.isExpired()) {
                System.out.println("Session expired. Please login again.");
            } else {
                System.out.println("Session active and valid.");
            }
        }
    }
}
