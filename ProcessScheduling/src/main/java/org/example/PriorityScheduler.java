package org.example;
import java.util.*;

public class PriorityScheduler implements SchedulingAlgorithms{
    List<Process> processes;

    public PriorityScheduler(List<Process> processes) {
        this.processes = processes;
    }
    @Override
    public void ScheduleProcess() {
        int currentTime = 0;
        while (!processes.isEmpty()) {
            // list the processes that have arrived already
            List<Process> availableProcesses = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime) {
                    availableProcesses.add(p);
                }
            }

            if (availableProcesses.isEmpty()) {

                currentTime++;
                continue;
            }

            //check for processes that wait for a long time
            for (Process p : processes) {
                if (currentTime - p.arrivalTime >= Main.agingInterval && p.processPriority > 1) {
                    p.processPriority--; // increase the priority
                    System.out.println("Aging applied: " + p.processName + " priority updated to " + p.processPriority);
                }
            }

            // sort according to priority then arrival time
            availableProcesses.sort(Comparator.comparingInt((Process p) -> p.processPriority)
                    .thenComparingInt(p -> p.arrivalTime));

            // pick the process with highest priority
            Process currentProcess = availableProcesses.get(0);

            // calculate waiting time
            currentProcess.waitingTime = currentTime - currentProcess.arrivalTime;

            // process execution
            System.out.println("Process " + currentProcess.processName + " executes from " +
                    currentTime + " to " + (currentTime + currentProcess.InitialbrustTime));
            currentTime += currentProcess.InitialbrustTime;

            // calculate tunaround time
            currentProcess.TurnArroundTime = currentProcess.waitingTime + currentProcess.InitialbrustTime;

            // list of completed processes
            Main.AllProcesses.add(currentProcess);

            // remove the complete process from the waiting processes list
            processes.remove(currentProcess);

            // adding the context switch time
            if (!processes.isEmpty()) {
                currentTime += Main.contextSwitchTime;
            }
        }
    }
    @Override
    
public void GetStatistics() {
    int totalWaitingTime = 0, totalTurnaroundTime = 0;

    // Printing the header of the table
    System.out.println("------------------------------------------------------------------------------------------------");
    System.out.printf("%-10s %-20s %-20s %-20s%n", "Process", "Turnaround Time", "Waiting Time", "Priority");
    System.out.println("------------------------------------------------------------------------------------------------");

    // Printing each process's statistics in a row
    for (Process p : Main.AllProcesses) {
        int turnaroundTime = p.TurnArroundTime;  // Directly use calculated Turnaround Time
        int waitingTime = p.waitingTime;          // Directly use calculated Waiting Time
        System.out.printf("%-10s %-20d %-20d %-20d%n", p.processName, turnaroundTime, waitingTime, p.processPriority);

        // Accumulating the total times
        totalWaitingTime += waitingTime;
        totalTurnaroundTime += turnaroundTime;
    }

    // Calculating and printing the averages
    int numProcesses = Main.AllProcesses.size();
    double avgWaitingTime = (double) totalWaitingTime / numProcesses;
    double avgTurnaroundTime = (double) totalTurnaroundTime / numProcesses;

    System.out.println("------------------------------------------------------------------------------------------------");
    System.out.printf("%-10s %-20.2f %-20.2f %-20s%n", "Average", avgTurnaroundTime, avgWaitingTime, "N/A");
    System.out.println("------------------------------------------------------------------------------------------------");
}
 }