package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FCAIScheduler implements SchedulingAlgorithms {
    private double V1, V2;
    List<Process> processes;
    LinkedList<Process> readyQueue = new LinkedList<>();
    SchedularGanttChart chart;
    int startX = 0;
    double AWT, ATAT;

    public FCAIScheduler(List<Process> processes, SchedularGanttChart chart) {
        this.processes = processes;
        this.chart = chart;
        InitializeFcaiConstants();
        InitializeProcessesFcaiFactors();
    }

    private void InitializeFcaiConstants() {
        int maxArrivalTime = 0, maxBurstTime = 0;
        for (Process p : processes) {
            maxArrivalTime = Math.max(maxArrivalTime, p.arrivalTime);
            maxBurstTime = Math.max(maxBurstTime, p.RemainingBurstTime);
        }
        V1 = maxArrivalTime / 10.0;
        V2 = maxBurstTime / 10.0;
        System.out.println("Initialized FCAI constants: V1 = " + String.format("%.3f", V1) + ", V2 = " + String.format("%.3f", V2));
    }

    private void InitializeProcessesFcaiFactors() {
        for (Process p : processes) {
            p.FCAIFactor = (10 - p.processPriority) + (p.arrivalTime / V1) + (p.RemainingBurstTime / V2);
            System.out.println("Initial FCAI factor for " + p.processName + ": " + String.format("%.3f", p.FCAIFactor));
        }
    }

    void AddProcessThatMustEnterNow(int time) {
        List<Process> processesToRemove = new ArrayList<>();
        for (Process p : processes) {
            if (p.arrivalTime <= time) {
                readyQueue.add(p);
                processesToRemove.add(p);
            }
        }
        processes.removeAll(processesToRemove);
    }

    Process ProcessToPreemete(double currentFcaiFactor) {
        for (Process p : readyQueue) {
            if (p.FCAIFactor < currentFcaiFactor) {
                return p;
            }
        }
        return null;
    }

    void UpdateFcaiFactor(Process p) {
        p.FCAIFactor = (10 - p.processPriority) + (p.arrivalTime / V1) + (p.RemainingBurstTime / V2);
    }

    void PrintScheduleHeader() {
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-10s %-15s %-15s %-15s %-20s %-10s%n",
                "Time", "Process", "Executed Time", "Remaining", "Burst Time", "FCAI Factor", "Priority FCAI Factor", "Action Details");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

    }

    private void Run() {
        int time = 0;
        Process processInExecution = null;
        PrintScheduleHeader();
        while (true) {
            if (processInExecution != null) {
                int timeExecuted = 0;
                int timeWithoutPreemption = (int) Math.ceil(processInExecution.timeQuantum * 0.4);
                timeWithoutPreemption = Math.min(timeWithoutPreemption, processInExecution.RemainingBurstTime);

                processInExecution.RemainingBurstTime -= timeWithoutPreemption;
                time += timeWithoutPreemption;

                AddProcessThatMustEnterNow(time);

                if (processInExecution.RemainingBurstTime == 0) {
                    // Update startX for the task bar position and use startY to avoid overlapping
                    chart.updateProcess(new FcaiScreenPainter(startX, processInExecution.processID * 100, timeWithoutPreemption, processInExecution.processName, processInExecution.color));
                    System.out.printf("%-10d %-10s %-15d %-15s %-15d %-20s %-10s%n",
                            time, processInExecution.processName, timeWithoutPreemption, "0", processInExecution.timeQuantum, "N/A", "Completed");

                    Main.AllProcesses.add(processInExecution);
                    processInExecution.leaveTime = time;

                    // Move startX to the right for the next process
                    startX += timeWithoutPreemption;

                    processInExecution = readyQueue.isEmpty() ? null : readyQueue.removeFirst();
                } else {
                    int remainingQuantum = Math.min(processInExecution.RemainingBurstTime,
                            processInExecution.timeQuantum - timeWithoutPreemption);
                    Process otherProcess = null;

                    while (remainingQuantum > 0) {
                        otherProcess = ProcessToPreemete(processInExecution.FCAIFactor);

                        if (otherProcess == null) {
                            processInExecution.RemainingBurstTime--;
                            timeWithoutPreemption++;
                            time++;
                        } else {
                            break;
                        }

                        AddProcessThatMustEnterNow(time);
                        UpdateFcaiFactor(processInExecution);
                        remainingQuantum--;
                    }

                    if (processInExecution.RemainingBurstTime == 0) {
                        chart.updateProcess(new FcaiScreenPainter(startX, processInExecution.processID * 100, timeWithoutPreemption, processInExecution.processName, processInExecution.color));
                        System.out.printf("%-10d %-10s %-15d %-15s %-15d %-20s %-10s%n",
                                time, processInExecution.processName, timeWithoutPreemption, "0", processInExecution.timeQuantum, "N/A", "Completed");

                        Main.AllProcesses.add(processInExecution);
                        processInExecution.leaveTime = time;

                        // Move startX to the right for the next process
                        startX += timeWithoutPreemption;

                        processInExecution = readyQueue.isEmpty() ? null : readyQueue.removeFirst();
                    } else if (remainingQuantum > 0) {
                        // Preempted by another process
                        processInExecution.timeQuantum += remainingQuantum;
                        processInExecution.quantumHistory.add(processInExecution.timeQuantum);
                        UpdateFcaiFactor(processInExecution);

                        chart.updateProcess(new FcaiScreenPainter(startX, processInExecution.processID * 100, timeWithoutPreemption, processInExecution.processName, processInExecution.color));
                        // Move startX to the right after preemption
                        startX += timeWithoutPreemption;

                        System.out.printf("%-10d %-10s %-15d %-15d %-15d %-20.3f %-10s%n",
                                time, processInExecution.processName, timeWithoutPreemption,
                                processInExecution.RemainingBurstTime, processInExecution.timeQuantum,
                                processInExecution.FCAIFactor, "Preempted by other process");
                        // chart.updateProcess(new Painter(startX, startY, timeWithoutPreemption, processInExecution.processName, processInExecution.color));
                        readyQueue.add(processInExecution);
                        readyQueue.remove(otherProcess);
                        processInExecution = otherProcess;
                    } else {

                        processInExecution.timeQuantum += 2;
                        processInExecution.quantumHistory.add(processInExecution.timeQuantum);
                        UpdateFcaiFactor(processInExecution);

                        System.out.printf("%-10d %-10s %-15d %-15d %-15d %-20.3f %-10s%n",
                                time, processInExecution.processName, timeWithoutPreemption,
                                processInExecution.RemainingBurstTime, processInExecution.timeQuantum,
                                processInExecution.FCAIFactor, "Quantum updated");
                        chart.updateProcess(new FcaiScreenPainter(startX, processInExecution.processID * 100, timeWithoutPreemption, processInExecution.processName, processInExecution.color));
                        startX += timeWithoutPreemption;
                        readyQueue.add(processInExecution);
                        readyQueue.remove(otherProcess);
                        processInExecution = otherProcess;
                    }
                }
            }
            AddProcessThatMustEnterNow(time);
            // if both queues are empty and the processInExceution is null
            // that means we finished processing
            if (readyQueue.isEmpty() && processes.isEmpty() && processInExecution == null) {
                break;
            }

            if (processInExecution == null && !readyQueue.isEmpty()) {
                processInExecution = readyQueue.removeFirst();
                continue;
            }
            if (readyQueue.isEmpty() && processInExecution == null) {
                time++;
            }
            startX++;
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
    }

    @Override
    public void ScheduleProcess() {

        Run();
    }

    @Override
    public void GetStatistics(){
        double sumT = 0.0, sumW = 0.0;

        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s%n", "Process", "Turnaround Time", "Waiting Time");
        System.out.println("----------------------------------------------------------------------------");
        List<String> historyOfQuantums = new ArrayList<>();
        String s = "";
        for (Process p : Main.AllProcesses) {
            int turnaroundTime = p.leaveTime - p.arrivalTime;
            int waitingTime = turnaroundTime - p.InitialbrustTime;
            p.TurnArroundTime = turnaroundTime;
            p.waitingTime = waitingTime;
            System.out.printf("%-15s %-20d %-20d%n", p.processName, turnaroundTime, waitingTime);

            sumT += turnaroundTime;
            sumW += waitingTime;
            s += p.processName + " ";
            for(Integer quantum: p.quantumHistory) {
                s += quantum + " -> ";
            }
            s += "completed \n";
            historyOfQuantums.add(s);
            s = "";
        }

        sumT = sumT / (double) Main.AllProcesses.size();
        sumW = sumW / (double) Main.AllProcesses.size();
        AWT = sumW; ATAT = sumT;

        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-15s %-20.2f %-20.2f%n", "Average", sumT, sumW);
        System.out.println("----------------------------------------------------------------------------");

        for(String quantum: historyOfQuantums) {
            System.out.print(quantum);
        }

    }
}
