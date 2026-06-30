package com.mastery.jdbc;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;

import java.sql.*;

@JavaConcept(
    name = "JDBC Basics and PreparedStatement",
    difficulty = Difficulty.INTERMEDIATE,
    what = "JDBC connects Java applications to databases. Statement compiles queries dynamically on each call (vulnerable to SQL injection). PreparedStatement pre-compiles SQL structures, enforcing parameter type bounds and blocking SQL injection.",
    whyItMatters = "Concatenating user inputs into raw Statements allows attackers to bypass security checks (SQL injection). PreparedStatement parses parameters as literals, neutralizing injection scripts.",
    keyPoints = {
        "H2 is an in-memory database, initialized with URL 'jdbc:h2:mem:testdb'.",
        "Connection, Statement, and ResultSet represent OS resources and must be closed using try-with-resources.",
        "PreparedStatement improves execution speed by caching compiled query execution plans in the database engine."
    },
    interviewQuestions = {
        @Question(
            question = "How does PreparedStatement prevent SQL injection?",
            answer = "PreparedStatement uses pre-compiled SQL queries with placeholders (?). The database compiles the query structure first. When parameters are supplied later via setString(), they are bound strictly as data values, preventing the database from executing them as SQL commands."
        ),
        @Question(
            question = "Why should we use try-with-resources for JDBC operations?",
            answer = "JDBC objects like Connection, Statement, and ResultSet hold open database sockets and cursors. If not closed explicitly, these database descriptors leak, eventually blocking the application from opening new connections."
        )
    },
    pitfalls = {
        "Concatenating inputs directly inside Statement SQL queries: SELECT * FROM users WHERE name = 'username'.",
        "Forgetting to register database drivers in legacy configurations (modern JDBC uses ServiceLoader to auto-load drivers)."
    }
)
public class JdbcBasicsAndPrepared {
    
    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        ConsoleFormatter.printHeader("JDBC Basics & SQL Injection Defense");

        // 1. Create table & Insert seed data
        ConsoleFormatter.printStep("Database Bootstrapping", "Initializing H2 database table");
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), secret VARCHAR(255))");
            stmt.execute("INSERT INTO users (name, secret) VALUES ('Alice', 'ALICE-SECRET-KEY')");
            stmt.execute("INSERT INTO users (name, secret) VALUES ('Bob', 'BOB-SECRET-KEY')");
            
            ConsoleFormatter.printSuccess("H2 database users table created and seed data inserted.");

        } catch (SQLException e) {
            ConsoleFormatter.printError("Bootstrapping failed", e);
        }

        // 2. Demonstrating SQL Injection Vulnerability via Statement
        ConsoleFormatter.printStep("SQL Injection Attack", "Executing vulnerable search using Statement");
        
        // Attacking string parameter
        String maliciousInput = "' OR '1'='1"; // Always true query bypass
        System.out.println("  Malicious user input: " + maliciousInput);

        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Vulnerable SQL concatenation
            String sql = "SELECT * FROM users WHERE name = '" + maliciousInput + "'";
            System.out.println("  Vulnerable query compiled: " + sql);
            
            try (ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("  [VULNERABLE RESULT] Records returned:");
                while (rs.next()) {
                    System.out.println("    --> User: " + rs.getString("name") + " | Secret: " + rs.getString("secret"));
                }
            }
            ConsoleFormatter.printWarning("Statement bypass leaked ALL secrets because inputs were treated as executable query commands!");

        } catch (SQLException e) {
            ConsoleFormatter.printError("SQL execution failure", e);
        }

        // 3. Solving via PreparedStatement
        ConsoleFormatter.printStep("SQL Injection Neutralization", "Executing same query using PreparedStatement parameter bindings");
        try (Connection conn = DriverManager.getConnection(H2_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?")) {
            
            pstmt.setString(1, maliciousInput); // Binds malicious input as a literal value
            System.out.println("  Pre-compiled query: SELECT * FROM users WHERE name = ?");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("  [SECURE RESULT] Records returned:");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("    --> User: " + rs.getString("name"));
                }
                if (!found) {
                    ConsoleFormatter.printSuccess("Security checks held! PreparedStatement safely treated malicious code as a literal name value, returning zero records.");
                }
            }
        } catch (SQLException e) {
            ConsoleFormatter.printError("SQL execution failure", e);
        }
    }
}
