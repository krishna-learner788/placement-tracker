import java.util.*;

/**
 * PlacementTracker - Student Placement Management System
 * -------------------------------------------------------
 * Author  : Krishna Gupta
 * College : Manipal Institute of Technology (MIT Manipal)
 * Branch  : B.Tech CS + Fintech
 *
 * Data Structures Used:
 *   1. HashMap<String, Student>  — O(1) student lookup by roll number
 *   2. ArrayList<String>         — ordered list of companies applied to per student
 *   3. Stack<String>             — LIFO-based undo history for last actions
 *
 * Context: Built to support placement coordination work as Junior Coordinator
 *          at MIT-Manipal (assisting seniors in placement sessions).
 */
public class PlacementTracker {

    // ─────────────────────────────────────────────────────────────
    // Inner class — represents one student in the placement system
    // ─────────────────────────────────────────────────────────────
    static class Student {
        String rollNo;
        String name;
        String branch;
        double cgpa;
        boolean placed;
        String placedAt;                      // company where placed, null if not
        ArrayList<String> appliedCompanies;   // DS #2 — ArrayList of applications

        Student(String rollNo, String name, String branch, double cgpa) {
            this.rollNo = rollNo;
            this.name = name;
            this.branch = branch;
            this.cgpa = cgpa;
            this.placed = false;
            this.placedAt = null;
            this.appliedCompanies = new ArrayList<>();
        }

        @Override
        public String toString() {
            String status = placed ? "✔ Placed @ " + placedAt : "✘ Not Placed";
            return String.format("| %-8s | %-18s | %-18s | CGPA: %.2f | %s",
                    rollNo, name, branch, cgpa, status);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Global data structures
    // ─────────────────────────────────────────────────────────────

    // DS #1 — HashMap: roll number → Student object
    //          Chosen because we need O(1) average lookup during placement drives
    static HashMap<String, Student> studentDB = new HashMap<>();

    // DS #3 — Stack: tracks last N operations for undo functionality
    //          Stack is LIFO — most recent action is popped first, just like
    //          Ctrl+Z in any editor
    static Stack<String> actionHistory = new Stack<>();

    static Scanner sc = new Scanner(System.in);

    // ─────────────────────────────────────────────────────────────
    // Main — menu-driven interface
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     MIT Manipal — Student Placement Tracker          ║");
        System.out.println("║     Krishna Gupta | CS + Fintech | DSA Project       ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        loadSampleMITData();  // pre-load realistic MIT Manipal sample data

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudent();
                case 4 -> markPlaced();
                case 5 -> applyToCompany();
                case 6 -> viewStudentApplications();
                case 7 -> filterByCGPA();
                case 8 -> showStats();
                case 9 -> undoLastAction();
                case 10 -> { running = false; System.out.println("\nGoodbye! Best of luck with placements 🚀 — Krishna Gupta"); }
                default -> System.out.println("[!] Invalid choice. Please try again.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 1. Add a new student record
    // ─────────────────────────────────────────────────────────────
    static void addStudent() {
        System.out.println("── Add New Student ────────────────────────");
        System.out.print("Roll No (e.g. 230911001): ");
        String roll = sc.nextLine().trim().toUpperCase();

        // HashMap.containsKey() — O(1) duplicate check
        if (studentDB.containsKey(roll)) {
            System.out.println("[!] Roll number already exists in system.");
            return;
        }

        System.out.print("Full Name   : ");
        String name = sc.nextLine().trim();

        System.out.print("Branch      : ");
        String branch = sc.nextLine().trim();

        double cgpa = readDouble("CGPA (0-10): ");
        if (cgpa < 0 || cgpa > 10) { System.out.println("[!] Invalid CGPA."); return; }

        Student s = new Student(roll, name, branch, cgpa);
        studentDB.put(roll, s);           // HashMap.put() — O(1) insertion

        actionHistory.push("ADD:" + roll); // push to Stack for undo
        System.out.println("[✔] Student '" + name + "' added successfully.");
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Display all students (iterate HashMap values — O(n))
    // ─────────────────────────────────────────────────────────────
    static void viewAllStudents() {
        if (studentDB.isEmpty()) {
            System.out.println("[!] No students in system yet.");
            return;
        }
        System.out.println("── All Students (" + studentDB.size() + " records) ─────────────────────────────────────────────");
        for (Student s : studentDB.values()) {   // HashMap.values() — O(n) traversal
            System.out.println(s);
        }
        System.out.println("────────────────────────────────────────────────────────────────────────────────────────");
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Search by roll number — O(1) using HashMap.get()
    // ─────────────────────────────────────────────────────────────
    static void searchStudent() {
        System.out.print("Enter Roll No: ");
        String roll = sc.nextLine().trim().toUpperCase();

        Student s = studentDB.get(roll);   // HashMap.get() — O(1)
        if (s == null) {
            System.out.println("[!] Student not found.");
        } else {
            System.out.println(s);
            // ArrayList.toString() gives [company1, company2, ...]
            System.out.println("    Applied to: " +
                    (s.appliedCompanies.isEmpty() ? "None" : s.appliedCompanies));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Mark student as placed at a company
    // ─────────────────────────────────────────────────────────────
    static void markPlaced() {
        System.out.print("Roll No : ");
        String roll = sc.nextLine().trim().toUpperCase();
        Student s = studentDB.get(roll);
        if (s == null) { System.out.println("[!] Student not found."); return; }
        if (s.placed)  { System.out.println("[!] Already placed at " + s.placedAt + ". Use undo to revert."); return; }

        System.out.print("Company : ");
        String comp = sc.nextLine().trim();

        s.placed = true;
        s.placedAt = comp;
        if (!s.appliedCompanies.contains(comp)) s.appliedCompanies.add(comp);

        actionHistory.push("PLACE:" + roll + ":" + comp);
        System.out.println("[✔] " + s.name + " marked as placed at " + comp + "!");
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Record a company application (ArrayList.add — O(1) amortized)
    // ─────────────────────────────────────────────────────────────
    static void applyToCompany() {
        System.out.print("Roll No : ");
        String roll = sc.nextLine().trim().toUpperCase();
        Student s = studentDB.get(roll);
        if (s == null) { System.out.println("[!] Student not found."); return; }

        System.out.print("Company to apply for: ");
        String comp = sc.nextLine().trim();

        // ArrayList.contains() — O(n) scan to avoid duplicates
        if (s.appliedCompanies.contains(comp)) {
            System.out.println("[!] Already applied to " + comp);
        } else {
            s.appliedCompanies.add(comp);      // ArrayList.add() — O(1) amortized
            actionHistory.push("APPLY:" + roll + ":" + comp);
            System.out.println("[✔] Application to " + comp + " recorded for " + s.name);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. View all companies a specific student applied to
    // ─────────────────────────────────────────────────────────────
    static void viewStudentApplications() {
        System.out.print("Roll No : ");
        String roll = sc.nextLine().trim().toUpperCase();
        Student s = studentDB.get(roll);
        if (s == null) { System.out.println("[!] Student not found."); return; }

        System.out.println("── " + s.name + " — Company Applications ──────────");
        ArrayList<String> list = s.appliedCompanies;
        if (list.isEmpty()) {
            System.out.println("   No applications recorded yet.");
        } else {
            for (int i = 0; i < list.size(); i++) {   // ArrayList index access — O(1)
                String marker = (list.get(i).equals(s.placedAt)) ? " ← PLACED HERE" : "";
                System.out.printf("   %d. %s%s%n", i + 1, list.get(i), marker);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 7. Filter students with CGPA above a threshold — O(n) scan
    //    Useful for shortlisting students for companies like Cisco
    // ─────────────────────────────────────────────────────────────
    static void filterByCGPA() {
        double threshold = readDouble("Enter minimum CGPA cutoff (e.g. 7.5): ");
        System.out.println("── Students with CGPA ≥ " + threshold + " ─────────────────");

        int count = 0;
        for (Student s : studentDB.values()) {    // O(n) linear scan
            if (s.cgpa >= threshold) {
                System.out.println(s);
                count++;
            }
        }
        if (count == 0) System.out.println("   No students match this cutoff.");
        else System.out.println("   Total eligible: " + count + " student(s)");
    }

    // ─────────────────────────────────────────────────────────────
    // 8. Overall placement statistics
    // ─────────────────────────────────────────────────────────────
    static void showStats() {
        int total = studentDB.size(), placed = 0;
        double cgpaSum = 0, maxCGPA = 0;
        String topper = "N/A";

        for (Student s : studentDB.values()) {    // O(n) single pass
            if (s.placed) placed++;
            cgpaSum += s.cgpa;
            if (s.cgpa > maxCGPA) { maxCGPA = s.cgpa; topper = s.name; }
        }

        double avg = total > 0 ? cgpaSum / total : 0;
        double placedPct = total > 0 ? (placed * 100.0 / total) : 0;

        System.out.println("── MIT Manipal Placement Statistics ──────────────────");
        System.out.printf("  Total Students    : %d%n", total);
        System.out.printf("  Placed            : %d  (%.1f%%)%n", placed, placedPct);
        System.out.printf("  Unplaced          : %d%n", total - placed);
        System.out.printf("  Average CGPA      : %.2f%n", avg);
        System.out.printf("  Highest CGPA      : %.2f  (%s)%n", maxCGPA, topper);
        System.out.println("────────────────────────────────────────────────────────");
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Undo last action using Stack.pop() — O(1) LIFO reversal
    // ─────────────────────────────────────────────────────────────
    static void undoLastAction() {
        // Stack.isEmpty() — O(1)
        if (actionHistory.isEmpty()) {
            System.out.println("[!] No actions to undo.");
            return;
        }

        String last = actionHistory.pop();    // Stack.pop() — O(1), removes top element
        String[] parts = last.split(":");

        switch (parts[0]) {
            case "ADD" -> {
                Student removed = studentDB.remove(parts[1]);  // HashMap.remove() — O(1)
                System.out.println("[Undo] Removed student: " + (removed != null ? removed.name : parts[1]));
            }
            case "PLACE" -> {
                Student s = studentDB.get(parts[1]);
                if (s != null) {
                    s.placed = false;
                    s.placedAt = null;
                    System.out.println("[Undo] Unplaced: " + s.name);
                }
            }
            case "APPLY" -> {
                Student s = studentDB.get(parts[1]);
                if (s != null) {
                    s.appliedCompanies.remove(parts[2]);   // ArrayList.remove(Object) — O(n)
                    System.out.println("[Undo] Removed application to " + parts[2] + " for " + s.name);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helper — print the main menu
    // ─────────────────────────────────────────────────────────────
    static void printMenu() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│         PLACEMENT TRACKER MENU           │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1.  Add Student                         │");
        System.out.println("│  2.  View All Students                   │");
        System.out.println("│  3.  Search Student (Roll No)            │");
        System.out.println("│  4.  Mark as Placed                      │");
        System.out.println("│  5.  Record Company Application          │");
        System.out.println("│  6.  View Student's Applications         │");
        System.out.println("│  7.  Filter by CGPA Cutoff               │");
        System.out.println("│  8.  Show Placement Statistics           │");
        System.out.println("│  9.  Undo Last Action                    │");
        System.out.println("│  10. Exit                                │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    // ─────────────────────────────────────────────────────────────
    // Helper — safe integer input
    // ─────────────────────────────────────────────────────────────
    static int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    // Helper — safe double input
    static double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // ─────────────────────────────────────────────────────────────
    // Sample data — realistic MIT Manipal CS + Fintech batch
    // (includes Krishna Gupta's real CGPA trajectory as reference)
    // ─────────────────────────────────────────────────────────────
    static void loadSampleMITData() {
        Object[][] students = {
            // {rollNo, name, branch, cgpa}
            {"240958222", "Krishna Gupta",    "CS + Fintech",    7.93},  // ← YOU (Sem 4 CGPA)
            {"230911002", "Arjun Sharma",     "CS + Fintech",    8.45},
            {"230911003", "Priya Nair",       "CS (Core)",       9.10},
            {"230911004", "Rahul Verma",      "CS + Fintech",    7.20},
            {"230911005", "Sneha Reddy",      "CS (Core)",       8.80},
            {"230911006", "Aditya Bose",      "CS + IoT",        7.67},
            {"230911007", "Meera Pillai",     "CS + Fintech",    8.15},
            {"230911008", "Karan Singh",      "CS (Core)",       6.95},
        };

        for (Object[] d : students) {
            Student s = new Student((String) d[0], (String) d[1], (String) d[2], (double) d[3]);
            studentDB.put((String) d[0], s);
        }

        // Mark a few as placed at realistic companies for demo
        Student priya = studentDB.get("230911003");
        priya.placed = true; priya.placedAt = "Cisco"; priya.appliedCompanies.addAll(List.of("Cisco", "Google", "Microsoft"));

        Student sneha = studentDB.get("230911005");
        sneha.placed = true; sneha.placedAt = "JP Morgan"; sneha.appliedCompanies.addAll(List.of("JP Morgan", "Goldman Sachs"));

        // Krishna's applications
        Student krishna = studentDB.get("240958222");
        krishna.appliedCompanies.addAll(List.of("Cisco", "Finova Capital", "Zerodha"));

        System.out.println("[INFO] 8 MIT Manipal sample students loaded.");
        System.out.println("[INFO] Your record: Roll 240958222 | Krishna Gupta | CGPA 7.93\n");
    }
}
