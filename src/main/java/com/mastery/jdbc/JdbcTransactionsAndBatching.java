package com.mastery.jdbc;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.sql.*;

@JavaConcept(
    name = "JDBC Transactions and Batch Processing",
    difficulty = Difficulty.ADVANCED,
    what = "JDBC transactions ensure ACID properties. Disabling auto-commit enables grouping multiple statements into atomic units committed or rolled back together. Batch processing bundles multiple executions into a single network transmission.",
    whyItMatters = "Executing multiple single insert queries causes heavy network overhead. Batching bundles queries, speeding up performance by orders of magnitude. Manual transaction management prevents database corruption during failures.",
    keyPoints = {
        "Transactions are initialized by calling connection.setAutoCommit(false).",
        "Savepoints allow rollback of specific nested database alterations inside a transaction.",
        "executeBatch() returns an array of integers representing the update counts of each command."
    },
    interviewQuestions = {
        @Question(
            question = "Explain how Savepoints work in JDBC transactions.",
            answer = "Savepoints provide fine-grained transaction control. By calling Savepoint sp = conn.setSavepoint('sp1'), you mark a point in the execution timeline. If later steps fail, you can call conn.rollback(sp) to undo alterations back to that point, and then commit preceding operations."
        ),
        @Question(
            question = "Why does Batch processing improve database write speeds?",
            answer = "Traditional single inserts require a network roundtrip for each query (parse, compile, run, acknowledge). Batch processing aggregates multiple query parameters, sending them to the database engine in a single network packet."
        )
    },
    pitfalls = {
        "Forgetting to call commit() after disabling auto-commit, leaving lock blocks open on tables and discarding modifications.",
        "Omitting clean rollbacks in catch blocks, leaving database connections in unstable, locked transaction states."
    }
)
public class JdbcTransactionsAndBatching {

    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        ConsoleFormatter.printHeader("JDBC Transactions & Batching");

        // Set up database table
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (id VARCHAR(50) PRIMARY KEY, balance DOUBLE)");
            stmt.execute("DELETE FROM accounts"); // Clear old records
            stmt.execute("INSERT INTO accounts VALUES ('ACC-A', 1000.00)");
            stmt.execute("INSERT INTO accounts VALUES ('ACC-B', 1000.00)");
        } catch (SQLException e) {
            ConsoleFormatter.printError("H2 setup failed", e);
        }

        // 1. Transaction Commit & Rollback Demo
        ConsoleFormatter.printStep("Transaction Control", "Executing fund transfer with commit and simulated rollback");
        transferFunds("ACC-A", "ACC-B", 200.00, false); // Should succeed
        transferFunds("ACC-A", "ACC-B", 500.00, true);  // Should fail and rollback

        printAccountBalances();

        // 2. Savepoints Demo
        ConsoleFormatter.printStep("Savepoint rollback", "Modifying data and rolling back to a Savepoint");
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Enable manual transactions

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 'ACC-A'");
                
                // Define Savepoint
                Savepoint sp = conn.setSavepoint("BalanceCheckpoint");
                System.out.println("  [TX] Created Savepoint 'BalanceCheckpoint'.");

                stmt.executeUpdate("UPDATE accounts SET balance = balance - 500 WHERE id = 'ACC-A'"); // Dangerous decrease
                
                // Oops! We want to revert the second update but keep the first
                conn.rollback(sp);
                System.out.println("  [TX] Rolled back to Savepoint 'BalanceCheckpoint'. First update preserved.");
                
                conn.commit(); // Commit first change only
                ConsoleFormatter.printSuccess("Transaction committed after savepoint rollback.");
            } catch (SQLException e) {
                conn.rollback();
                ConsoleFormatter.printError("Transaction rolled back fully due to sql error", e);
            }
        } catch (SQLException e) {
            ConsoleFormatter.printError("Connection failed", e);
        }

        printAccountBalances();

        // 3. Batch Processing Demo
        ConsoleFormatter.printStep("Batch Insertions", "Inserting 1000 records using PreparedStatement batching");
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             Statement initStmt = conn.createStatement()) {
            
            initStmt.execute("CREATE TABLE IF NOT EXISTS batch_users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
            initStmt.execute("DELETE FROM batch_users");

            conn.setAutoCommit(false); // Crucial: batch operations run faster with auto-commit disabled
            
            long start = System.currentTimeMillis();
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO batch_users (name) VALUES (?)")) {
                for (int i = 1; i <= 1000; i++) {
                    pstmt.setString(1, "User-" + i);
                    pstmt.addBatch(); // Add to batch queue
                    
                    if (i % 200 == 0) {
                        pstmt.executeBatch(); // Execute batch every 200 records
                    }
                }
                pstmt.executeBatch(); // Flush remaining
                conn.commit();
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("Finished batch inserting 1000 records in: " + duration + " ms");
            
            // Check count
            try (ResultSet rs = initStmt.executeQuery("SELECT COUNT(*) FROM batch_users")) {
                if (rs.next()) {
                    System.out.println("Total users count: " + rs.getInt(1));
                }
            }
            
            ConsoleFormatter.printSuccess("Batch insertions completed successfully.");
            
        } catch (SQLException e) {
            ConsoleFormatter.printError("Batch execution failed", e);
        }
    }

    private static void transferFunds(String fromAcc, String toAcc, double amount, boolean simulateCrash) {
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Disables auto-commit! Start of transaction block

            try (PreparedStatement withdrawStmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
                 PreparedStatement depositStmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
                
                // Step 1: Withdraw
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, fromAcc);
                withdrawStmt.executeUpdate();

                // Simulate systemic failure or exception
                if (simulateCrash) {
                    throw new SQLException("System crash simulation inside transaction pipeline!");
                }

                // Step 2: Deposit
                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, toAcc);
                depositStmt.executeUpdate();

                conn.commit(); // Commit transaction
                System.out.println("  [TX] Transfer of $" + amount + " committed successfully.");

            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on failure!
                ConsoleFormatter.printWarning("Transfer failed! Reverting modifications. Error: " + e.getMessage());
            }

        } catch (SQLException e) {
            ConsoleFormatter.printError("Connection error", e);
        }
    }

    private static void printAccountBalances() {
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM accounts")) {
            System.out.println("  [BALANCES] Current Accounts State:");
            while (rs.next()) {
                System.out.println("    --> Account: " + rs.getString("id") + " | Balance: $" + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            ConsoleFormatter.printError("Failed fetching balances", e);
        }
    }
}
