package com.syfe.finance;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Application starting...");
        
        try {
            PersonalFinanceManagerApplication.main(args);
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}