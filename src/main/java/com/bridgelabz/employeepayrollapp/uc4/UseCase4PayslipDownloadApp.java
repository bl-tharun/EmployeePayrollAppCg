package com.bridgelabz.employeepayrollapp.uc4;

import java.io.FileWriter;
import java.io.IOException;

/*
 * ======================================================
 * USE CASE 4: PAYSLIP PRINT / DOWNLOAD
 * ======================================================
 *
 * Goal of this Use Case:
 * - Protect existing data from accidental modification
 * - Learn how objects can be safely copied
 * - Understand how object equality works
 *
 * New ideas introduced in UC4:
 * - Immutability
 * - Cloning objects
 * - equals() and hashCode()
 * - Simple file persistence
 *
 * This use case builds on:
 * - UC3: Payslip generation
 */

// =================== Immutable Payslip ======================
/*
 * Payslip represents a finalized salary record.
 *
 * Key idea introduced here:
 * - Immutability
 *
 * Once a payslip is generated:
 * - Its data should never change
 * - Any operation (print / download) must use a copy
 *
 * Making the class final prevents inheritance-based modification.
 */
final class Payslip implements Cloneable {

    private final String empId;
    private final String empName;
    private final String month;
    private final double netPay;

    /*
     * Constructor initializes all fields once.
     *
     * Fields are final, so values cannot be changed later.
     */
    public Payslip(String empId, String empName, String month, double netPay) {
        this.empId = empId;
        this.empName = empName;
        this.month = month;
        this.netPay = netPay;
    }

    /*
     * Only getters are provided.
     *
     * No setters ensure immutability.
     */
    public String getEmpId() { return empId; }
    public String getEmpName() { return empName; }
    public String getMonth() { return month; }
    public double getNetPay() { return netPay; }

    /*
     * Creates a safe copy of the payslip.
     *
     * Why cloning is needed:
     * - Original object should remain untouched
     * - Downloaded or printed version must be independent
     */
    public Object clone() {
        return new Payslip(empId, empName, month, netPay);
    }

    /*
     * Defines logical equality between two payslips.
     *
     * Two payslips are considered equal if:
     * - They belong to the same employee
     * - They are for the same month
     */
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Payslip)) {
            return false;
        }

        Payslip p = (Payslip) o;

        return this.empId.equals(p.empId)
                && this.month.equals(p.month);
    }

    /*
     * hashCode() is implemented to be consistent with equals().
     *
     * This is important when objects are used in collections.
     */
    public int hashCode() {
        int result = 17;
        result = 31 * result + empId.hashCode();
        result = 31 * result + month.hashCode();
        return result;
    }

    /*
     * Converts payslip data into readable text.
     *
     * Used for:
     * - Console printing
     * - File download
     */
    public String toString() {
        return "PAYSLIP\n"
                + "Employee ID   : " + empId + "\n"
                + "Employee Name : " + empName + "\n"
                + "Month         : " + month + "\n"
                + "Net Pay       : " + netPay + "\n";
    }
}

// =================== Download Token ======================
/*
 * DownloadToken represents time-bound access to a download.
 *
 * Purpose:
 * - Prevent unlimited access to generated files
 * - Simulate real-world expiry logic
 *
 * This introduces the idea of time-based validation.
 */
class DownloadToken {

    private long createdTime;
    private long expiryMillis;

    public DownloadToken() {
        createdTime = System.currentTimeMillis();
        expiryMillis = 60 * 1000; // 1 minute validity
    }

    /*
     * Checks whether the token is still valid.
     */
    public boolean isExpired() {
        long now = System.currentTimeMillis();
        return (now - createdTime) > expiryMillis;
    }
}

// =================== File Service ======================
/*
 * FileService handles saving payslip data to files.
 *
 * Why this class exists:
 * - File operations should not be inside Payslip
 * - Separates persistence from data representation
 */
class FileService {

    /*
     * Saves payslip as a text file.
     *
     * A unique filename is generated using timestamp
     * to avoid overwriting existing files.
     */
    public String savePayslipAsText(Payslip payslip) throws IOException {

        String fileName = "Payslip_" + payslip.getEmpId() + "_"
                + System.currentTimeMillis() + ".txt";

        FileWriter fw = new FileWriter(fileName);
        fw.write(payslip.toString());
        fw.close();

        return fileName;
    }

    /*
     * Saves payslip as a PDF file.
     *
     * Note:
     * - This is a simplified demo
     * - Content is plain text with .pdf extension
     */
    public String savePayslipAsPdf(Payslip payslip) throws IOException {

        String fileName = "Payslip_" + payslip.getEmpId() + "_"
                + System.currentTimeMillis() + ".pdf";

        FileWriter fw = new FileWriter(fileName);
        fw.write(payslip.toString());
        fw.close();

        return fileName;
    }
}

// =================== Main App ======================
/*
 * Main runner class for Use Case 4.
 *
 * Role of main():
 * - Demonstrate safe usage of a finalized object
 * - Coordinate cloning, validation, and persistence
 *
 * main() does NOT modify the original payslip.
 */
public class UseCase4PayslipDownloadApp {

    /**
     * Entry point for payslip print / download use case.
     *
     * Execution Flow:
     * 1. Create original payslip
     * 2. Clone payslip for download
     * 3. Verify equality and identity
     * 4. Check download expiry
     * 5. Save payslip to files
     * 6. Print cloned payslip
     *
     * @author Developer
     * @version 4.0
     */
    public static void main(String[] args) {

        System.out.println("=== USE CASE 4: PAYSLIP PRINT / DOWNLOAD ===");

        // Existing generated payslip (read-only)
        Payslip original = new Payslip(
                "EMP-1010",
                "John David",
                "January 2026",
                48500.00
        );

        System.out.println("\nOriginal Payslip:");
        System.out.println(original);

        try {
            // Step 1: Create a safe copy
            Payslip downloadCopy = (Payslip) original.clone();

            // Step 2: Verify logical equality
            if (original.equals(downloadCopy)) {
                System.out.println("Verified: Download copy is equal to original.");
            }

            // Step 3: Compare hash codes
            System.out.println("Original hashcode : " + original.hashCode());
            System.out.println("Cloned   hashcode : " + downloadCopy.hashCode());

            // Step 4: Validate download token
            DownloadToken token = new DownloadToken();

            if (token.isExpired()) {
                System.out.println("Download link expired.");
                return;
            }

            // Step 5: Persist cloned payslip
            FileService fs = new FileService();

            String textFile = fs.savePayslipAsText(downloadCopy);
            String pdfFile = fs.savePayslipAsPdf(downloadCopy);

            // Step 6: Confirmation
            System.out.println("\nPayslip Download Successful.");
            System.out.println("Saved as text file: " + textFile);
            System.out.println("Saved as PDF file : " + pdfFile);

            // Step 7: Print cloned payslip
            System.out.println("\n--- Printed Payslip ---");
            System.out.println(downloadCopy);

        } catch (Exception e) {
            System.out.println("Error during payslip download.");
        }
    }
}
