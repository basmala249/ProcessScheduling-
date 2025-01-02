package org.example;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Process implements Runnable {
    List<Integer> quantumHistory = new ArrayList<Integer>();
    String processName, processColor;
    int arrivalTime, RemainingBurstTime, processPriority , TurnArroundTime , waitingTime , timeQuantum ;
    double FCAIFactor;
    int leaveTime, InitialbrustTime;
    int processID;
    Color color;


    public Process(String processName, String processColor,
                   int arrivalTime, int burstTime, int processID,int timeQuantum, int processPriority, Color color) {
        this.processName = processName;
        this.processColor = processColor;
        this.arrivalTime = arrivalTime;
        this.RemainingBurstTime = burstTime;
        this.processPriority = processPriority;
        this.InitialbrustTime = burstTime;
        this.timeQuantum = timeQuantum;
        quantumHistory.add(timeQuantum);
        this.color = color;
        this.processID = processID;
    }

    @Override
    public String toString() {
        return "Process{" +
                "processName='" + processName + '\'' +
                ", processColor='" + processColor + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", RemainingBurstTime=" + RemainingBurstTime +
                ", processPriority=" + processPriority +
                ", fcaiFactor=" + FCAIFactor +
                '}';
    }

    @Override
    public void run() {
        try {
            System.out.println("Process " + processName + " is running.");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Process " + processName + " interrupted.");
        }

    }
}