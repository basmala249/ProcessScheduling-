package org.example;


import java.util.*;

public class ShortestJobFirst implements SchedulingAlgorithms {
    List<Process> processes;
    sjfGanttChart chart; // Gantt chart for GUI
    double AWT, ATAT;
    public ShortestJobFirst(List<Process> processes, sjfGanttChart chart) {
        this.processes = processes;
        this.chart = chart; // Pass Gantt chart to the scheduler
    }

    @Override
    public void ScheduleProcess() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int currentTime = 0;
        int xCoord = 0; // X-coordinate for the Gantt chart

        while (!processes.isEmpty()) {
            System.out.println(xCoord);
            List<Process> readyProcesses = new ArrayList<>();

            // Add processes that have arrived to the ready list
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime) {
                    readyProcesses.add(p);
                }
            }

            if (readyProcesses.isEmpty()) {
                xCoord++;
                currentTime++;
                continue;
            }

            // Sort ready processes by burst time, then priority
            readyProcesses.sort((p1, p2) -> {
                if (p1.InitialbrustTime != p2.InitialbrustTime) {
                    return Integer.compare(p1.InitialbrustTime, p2.InitialbrustTime);
                }
                return Integer.compare(p1.processPriority, p2.processPriority);
            });

            // Execute the process with the shortest burst time
            Process currentProcess = readyProcesses.get(0);
            readyProcesses.remove(0); // Remove from ready list

            // Add process to Gantt chart
            int width = currentProcess.InitialbrustTime;
            chart.updateProcess(new FcaiScreenPainter(xCoord, 20, width, currentProcess.processName, currentProcess.color));
            xCoord += width;

            // Update the current time and calculate process statistics
            currentTime += currentProcess.InitialbrustTime;
            currentProcess.waitingTime = currentTime - currentProcess.arrivalTime - currentProcess.InitialbrustTime;
            currentProcess.TurnArroundTime = currentTime - currentProcess.arrivalTime;

            Main.AllProcesses.add(currentProcess); // Add completed process to AllProcesses
            processes.remove(currentProcess); // Remove from main list
        }
    }

    @Override
    public void GetStatistics() {
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        System.out.print("Process execution order is: ");
        for (int i = 0; i < Main.AllProcesses.size(); i++) {
            System.out.print(Main.AllProcesses.get(i).processName);
            if (i < Main.AllProcesses.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println("\nScheduling Results:");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-15s %-15s\n", "Process", "Waiting Time", "Turnaround Time", "Arrival Time", "Burst Time");
        System.out.println("------------------------------------------------------------------------------------");

        for (Process p : Main.AllProcesses) {
            System.out.printf("%-10s %-15d %-15d %-15d %-15d\n",
                    p.processName, p.waitingTime, p.TurnArroundTime, p.arrivalTime, p.InitialbrustTime);
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.TurnArroundTime;
        }
AWT=(double) totalWaitingTime / Main.AllProcesses.size();
        ATAT=(double) totalTurnaroundTime / Main.AllProcesses.size();
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("\nAverage Waiting Time: " + (double) totalWaitingTime / Main.AllProcesses.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / Main.AllProcesses.size());
    }
}

