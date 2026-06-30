package com.mastery.utils;

public class ConsoleFormatter {
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "================================================================================" + RESET);
        System.out.println(CYAN + BOLD + " CONCEPT: " + title.toUpperCase() + RESET);
        System.out.println(CYAN + BOLD + "================================================================================" + RESET);
    }

    public static void printStep(String stepName, String details) {
        System.out.println(YELLOW + BOLD + "⚡ " + stepName + ": " + RESET + WHITE + details + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + BOLD + "✓ SUCCESS: " + RESET + GREEN + message + RESET);
    }

    public static void printWarning(String warning) {
        System.out.println(YELLOW + BOLD + "⚠ WARNING: " + RESET + YELLOW + warning + RESET);
    }

    public static void printError(String error, Throwable t) {
        System.out.println(RED + BOLD + "✗ ERROR: " + RESET + RED + error + RESET);
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    public static void printDivider() {
        System.out.println(WHITE + "--------------------------------------------------------------------------------" + RESET);
    }
}
