package com.kg.sqlengine;
import com.kg.sqlengine.Storage.Database;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Database db = new Database();
        SimpleSQLExecutor executor = new SimpleSQLExecutor(db);

        Scanner scanner = new Scanner(System.in);

        System.out.println("MiniSQL Engine Started (type EXIT to quit)");

        while (true) {
            System.out.print("MiniSQL > ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("EXIT")) {
                break;
            }

            try {
                executor.execute(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
