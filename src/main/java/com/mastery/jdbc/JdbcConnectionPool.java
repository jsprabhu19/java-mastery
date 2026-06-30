package com.mastery.jdbc;

import com.mastery.annotations.Difficulty;
import com.mastery.annotations.JavaConcept;
import com.mastery.annotations.JavaConcept.Question;
import com.mastery.utils.ConsoleFormatter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@JavaConcept(
    name = "Database Connection Pooling (HikariCP)",
    difficulty = Difficulty.ADVANCED,
    what = "Database Connection Pooling maintains a cache of active database connections. When the application requests a connection, it receives an active pooled connection instantly. Closing the connection returns it to the pool instead of terminating the socket.",
    whyItMatters = "Opening raw database connections via DriverManager requires TCP handshakes, memory allocations, and credential checks, bottlenecking high-performance databases. Connection pools reuse connections, achieving sub-millisecond response speeds.",
    keyPoints = {
        "HikariCP is the industry-standard, high-performance database connection pool (default in Spring Boot).",
        "Configuring maximumPoolSize sets the upper limit of simultaneous physical database connections allowed.",
        "Calling connection.close() on a pooled connection overrides close() to recycle the connection back to the pool."
    },
    interviewQuestions = {
        @Question(
            question = "What happens to the physical database socket when you call connection.close() in a pooled environment?",
            answer = "The physical socket remains open. The connection pool wraps the raw connection in a proxy. When connection.close() is invoked, the proxy intercepts the call, resets connection attributes (autocommit, transaction states), and returns the active connection back to the pool cache."
        ),
        @Question(
            question = "Why is it important to configure connectionTimeout on a connection pool?",
            answer = "If all connections in the pool are busy, subsequent requests wait. connectionTimeout determines how long a thread blocks waiting for an available connection before throwing a SQLException (preventing infinite thread hangs)."
        )
    },
    pitfalls = {
        "Setting maximumPoolSize too high, exhausting database socket limits and memory resources.",
        "Leaking connections by not closing them inside try-with-resources, leaving them checked out indefinitely and locking the pool (pool starvation)."
    }
)
@SuppressWarnings("all")
public class JdbcConnectionPool {

    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        ConsoleFormatter.printHeader("Database Connection Pooling (HikariCP)");

        // 1. Configure HikariCP
        ConsoleFormatter.printStep("Configuring HikariCP Pool", "Setting up HikariConfig properties");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(H2_URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        
        // Pool configurations
        config.setMaximumPoolSize(5); // Upper limit
        config.setConnectionTimeout(1000); // 1 second timeout
        config.setIdleTimeout(60000); // 60 seconds idle limit
        config.setPoolName("Mastery-HikariPool");

        // 2. Initialize DataSource
        ConsoleFormatter.printStep("Initializing DataSource", "Creating HikariDataSource instance");
        try (HikariDataSource ds = new HikariDataSource(config)) {
            
            // 3. Requesting and recycling connections
            ConsoleFormatter.printStep("Acquiring Connection", "Borrowing connection from Hikari pool and running query");
            try (Connection conn = ds.getConnection(); // Borrows connection from pool
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                
                if (rs.next()) {
                    System.out.println("  [QUERY SUCCESS] Raw value returned from H2: " + rs.getInt(1));
                }
                
                System.out.println("  [INFO] Borrowed connection class: " + conn.getClass().getName()); // Proxy class!
                
            } catch (SQLException e) {
                ConsoleFormatter.printError("Database query failed", e);
            } // conn.close() runs here, returning connection to Hikari pool!

            // 4. Starvation Simulation
            ConsoleFormatter.printStep("Starvation Protection Check", "Exhausting all pool connections to verify connection timeouts");
            Connection[] connections = new Connection[5];
            try {
                for (int i = 0; i < 5; i++) {
                    connections[i] = ds.getConnection(); // Borrow all 5 connections
                    System.out.println("  Borrowed connection #" + (i + 1));
                }

                // Attempt to borrow a 6th connection (should fail since max is 5 and timeout is 1000ms)
                System.out.println("  Attempting to borrow 6th connection (expecting timeout exception)...");
                ds.getConnection(); // Will block for 1 second and throw exception
                
            } catch (SQLException e) {
                ConsoleFormatter.printWarning("Starvation check succeeded! Exception caught: " + e.getMessage());
            } finally {
                // Return borrowed connections to pool
                for (Connection c : connections) {
                    if (c != null) {
                        try {
                            c.close(); // Return to pool
                        } catch (SQLException ignored) {}
                    }
                }
                System.out.println("  Returned all borrowed connections back to the pool.");
            }

            ConsoleFormatter.printSuccess("HikariCP connection pool initialized and tested successfully.");
        }
    }
}
