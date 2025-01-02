package org.example;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SRTFScheduler implements SchedulingAlgorithms {
    List<Process> processes ;
    List<String> executionOrder = new ArrayList<>();
    Semaphore cpu = new Semaphore(1);
    int contextSwitchTime ;
    public SRTFScheduler(List<Process> processes ,int contextSwitchTime){
        this.processes = processes;
        this.contextSwitchTime = contextSwitchTime;
    }
    @Override
    public void ScheduleProcess() {
        int currentTime = 0;
        int completed = 0;
        Process currentProcess = null;
        int starvationThreshold = 5;

        while (completed < processes.size()) {
            Process shortest = null;

            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && process.RemainingBurstTime > 0 && process != currentProcess) {
                    int watingTime = currentTime- process.arrivalTime  - (process.InitialbrustTime - process.RemainingBurstTime);
                    if(watingTime > starvationThreshold)
                        process.processPriority++;
                }
            }

            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && process.RemainingBurstTime > 0) {
                    if(shortest == null || shortest.processPriority < process.processPriority){
                        shortest = process;
                        process.processPriority = 0;
                    }
                    else if (process.RemainingBurstTime < shortest.RemainingBurstTime ){
                        shortest = process;
                        process.processPriority = 0;
                    }
                }
            }

            if (shortest == null) {
                currentTime++;
                continue;
            }

            if (currentProcess != shortest) {
                try {
                    currentTime += contextSwitchTime;
                    Thread.sleep(contextSwitchTime*100);
                } catch (InterruptedException e) {
                    System.out.println("Context switching interrupted.");
                }
                currentProcess = shortest;
            }


            executionOrder.add(shortest.processName);
            try {
                cpu.acquire();
                Thread processThread = new Thread(shortest);
                processThread.start();
                Thread.sleep(100);
                shortest.RemainingBurstTime--;
                currentTime++;
                if (shortest.RemainingBurstTime == 0) {
                    shortest.TurnArroundTime = currentTime - shortest.arrivalTime;
                    shortest.waitingTime = shortest.TurnArroundTime - shortest.InitialbrustTime;
                    completed++;
                }
                cpu.release();
            } catch (InterruptedException e) {
                System.out.println("Execution interrupted.");
            }
        }
    }

    @Override
    public void GetStatistics() {
        System.out.println("\nExecution Order: " + String.join(" -> ", executionOrder));

        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-20s %-20s %n", "Process", "Turnaround Time", "Waiting Time");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Process p : processes) {
            System.out.printf("%-10s %-20d %-20d %n", p.processName, p.TurnArroundTime, p.waitingTime);

            // Accumulating the total times
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.TurnArroundTime;
        }

        // Calculating and printing the averages
        int numProcesses = processes.size();
        double avgWaitingTime = (double) totalWaitingTime / numProcesses;
        double avgTurnaroundTime = (double) totalTurnaroundTime / numProcesses;

        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-20.2f %-20.2f %n", "Average", avgTurnaroundTime, avgWaitingTime);
        System.out.println("------------------------------------------------------------------------------------------------");
    }

}
