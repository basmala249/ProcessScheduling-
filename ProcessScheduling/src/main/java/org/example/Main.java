package org.example;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int contextSwitchTime = 0;
    public static int agingInterval = 10;
    public static List<Process> AllProcesses = new ArrayList<>();
    public static SchedulingAlgorithms algorithm = null;
    public static ArrayList<Process> processes = new ArrayList<>();


    public static void main(String[] args) {

        Run();

    }
    public static void RunFACISchedular() {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Scheduling");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SchedularGanttChart chart = new SchedularGanttChart();

            JEditorPane processDataSection = new JEditorPane();
            processDataSection.setEditable(false);
            processDataSection.setContentType("text/html");

            StringBuilder processData = new StringBuilder();
            processData.append("<table cellpadding='5' cellspacing='0' style='border-collapse: collapse; border: none;'>")
                    .append("<tr>")
                    .append("<th>Name</th>")
                    .append("<th>ID</th>")
                    .append("<th>Priority</th>")
                    .append("<th>Color</th>")
                    .append("<th>Color Box</th>")
                    .append("</tr>");

            for (Process process : processes) {
                processData.append("<tr>")
                        .append("<td><b>").append(process.processName).append("</b></td>")
                        .append("<td>").append(process.processID).append("</td>")
                        .append("<td>").append(process.processPriority).append("</td>")
                        .append("<td>").append(process.processColor).append("</td>")
                        .append("<td style='background-color:").append(toHex(process.color))
                        .append("; padding: 5px; border-radius: 3px; color: white;'>")
                        .append("</td>").append("</tr>");
            }

            processData.append("</table>");
            processDataSection.setText("<html>" + processData.toString() + "</html>");


            JPanel horizontalPanel = new JPanel(new BorderLayout());
            horizontalPanel.add(new JScrollPane(processDataSection), BorderLayout.CENTER);

            JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(chart), horizontalPanel);
            horizontalSplit.setDividerLocation(700);

            FCAIScheduler fcai = new FCAIScheduler(processes, chart);
            fcai.ScheduleProcess();
            fcai.GetStatistics();

            JTextArea statistics = new JTextArea(10, 30);
            statistics.setEditable(false);
            JPanel verticalPanel = new JPanel(new BorderLayout());
            verticalPanel.add(new JScrollPane(statistics), BorderLayout.CENTER);

            JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, verticalPanel);
            verticalSplit.setDividerLocation(500);

            statistics.setText("Statistics \n");
            statistics.append("Scheduling Algorithm: FCAI \n");
            statistics.append("AWT:  " + fcai.AWT + "\n");
            statistics.append("ATAT: " +  fcai.ATAT + "\n");


            frame.add(verticalSplit);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    public static void RunSJF() {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Shortest Job First Scheduling");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            sjfGanttChart chart = new sjfGanttChart();


            JEditorPane processInformation = new JEditorPane();
            processInformation.setEditable(false);
            processInformation.setContentType("text/html");

            StringBuilder sb = new StringBuilder();
            sb.append("<table cellpadding='5' cellspacing='0' style='border-collapse: collapse; border: none;'>")
                    .append("<tr>")
                    .append("<th>Name</th>")
                    .append("<th>ID</th>")
                    .append("<th>Priority</th>")
                    .append("<th>Color</th>")
                    .append("<th>Color Box</th>")
                    .append("</tr>");

            for (Process process : processes) {
                sb.append("<tr>")
                        .append("<td><b>").append(process.processName).append("</b></td>")
                        .append("<td>").append(process.processID).append("</td>")
                        .append("<td>").append(process.processPriority).append("</td>")
                        .append("<td>").append(process.processColor).append("</td>")
                        .append("<td style='background-color:").append(toHex(process.color))
                        .append("; padding: 5px; border-radius: 3px; color: white;'>")
                        .append("</td>").append("</tr>");
            }

            sb.append("</table>");
            processInformation.setText("<html>" + sb.toString() + "</html>");


            JPanel horizontalPanel = new JPanel(new BorderLayout());
            horizontalPanel.add(new JScrollPane(processInformation), BorderLayout.CENTER);

            JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(chart), horizontalPanel);
            horizontalSplit.setDividerLocation(700);

            ShortestJobFirst sjf = new ShortestJobFirst(processes, chart);
            sjf.ScheduleProcess();
            sjf.GetStatistics();

            JTextArea statisticsArea = new JTextArea(10, 30);
            statisticsArea.setEditable(false);
            JPanel verticalPanel = new JPanel(new BorderLayout());
            verticalPanel.add(new JScrollPane(statisticsArea), BorderLayout.CENTER);

            JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, verticalPanel);
            verticalSplit.setDividerLocation(500);


            statisticsArea.setText("Statistics \n");
            statisticsArea.append("Scheduling Algorithm: Shortest Job First (SJF)\n");
            statisticsArea.append("AWT:  " + sjf.AWT + "\n");
            statisticsArea.append("ATAT: " + sjf.ATAT + "\n");

            frame.add(verticalSplit);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    public static void Run(){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            AllProcesses.clear();
            ShowMenu();
           // System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    algorithm = new PriorityScheduler(processes);
                    System.out.println("\nRunning Scheduling Algorithm...");
                    algorithm.ScheduleProcess();
                    algorithm.GetStatistics();
                    break;

                case 2:
                    RunSJF();
                    break;

                case 3:
                    algorithm = new SRTFScheduler(processes,contextSwitchTime);
                    System.out.println("\nRunning Scheduling Algorithm...");
                    algorithm.ScheduleProcess();
                    algorithm.GetStatistics();
                    break;

                case 4:
                    RunFACISchedular();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    public static void ShowMenu(){
        Scanner scanner = new Scanner(System.in);
        // Input number For All Processes and their details before the menu
        System.out.print("Enter the number of processes: ");
        int numberOfProcesses = scanner.nextInt();
        System.out.print("Enter Context Switch Time or 0: ");
        contextSwitchTime = scanner.nextInt();
        for (int i = 0; i < numberOfProcesses; i++) {
            System.out.println("\nEnter details for Process " + (i + 1) + ":");

            System.out.print("Enter Process Name: ");
            String name = scanner.next();

            System.out.print("Enter Process Color: ");
            String color = scanner.next();

            System.out.print("Enter Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Enter Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Enter Process Priority or 0: ");
            int priority = scanner.nextInt();
            System.out.print("Enter Process Quantam Or 0: ");
            int timeQuantum = scanner.nextInt();
            Color c = GetColor(color);
            Process process = new Process(name, color, arrivalTime, burstTime , i + 1, timeQuantum , priority, c);
            processes.add(process);
        }
        System.out.println("\n--- Scheduling Algorithms Menu ---");
        System.out.println("1. Select Non-Preemptive Priority Scheduling");
        System.out.println("2. Select Non-Preemptive Shortest Job First (SJF)");
        System.out.println("3. Select Shortest Remaining Time First (SRTF)");
        System.out.println("4. Select FCAI Scheduling");
        System.out.println("5. Exit");

        System.out.print("Choose an option: ");
    }

    public static Color GetColor(String color) {
        if (color.equalsIgnoreCase("red")) {
            return Color.RED;
        }
        if (color.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        }
        if (color.equalsIgnoreCase("green")) {
            return Color.GREEN;
        }
        if (color.equalsIgnoreCase("yellow")) {
            return Color.YELLOW;
        }
        if (color.equalsIgnoreCase("orange")) {
            return Color.ORANGE;
        }
        if (color.equalsIgnoreCase("pink")) {
            return Color.PINK;
        }
        if (color.equalsIgnoreCase("gray")) {
            return Color.GRAY;
        }
        if (color.equalsIgnoreCase("black")) {
            return Color.BLACK;
        }
        if (color.equalsIgnoreCase("cyan")) {
            return Color.CYAN;
        }
        return Color.MAGENTA;
    }



}
