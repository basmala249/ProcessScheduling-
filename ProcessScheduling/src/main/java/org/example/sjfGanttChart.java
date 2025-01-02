package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class sjfGanttChart extends JPanel {
    private List<FcaiScreenPainter> processes;

    private final int rowHeight = 40; // Height for each row

    public sjfGanttChart() {
        this.processes = new ArrayList<>();
    }

    public void updateProcess(FcaiScreenPainter fcaiScreenPainter) {
        this.processes.add(fcaiScreenPainter);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int yOffset = 0; // Offset to track the Y position for each row
        int xOffset = 0; // Offset to track the X position for each process in the same row

        // Loop through each process and place it in its own row
        for (FcaiScreenPainter fcaiScreenPainter : processes) {
            int width = fcaiScreenPainter.width * 10; // Scale the width by 10

            // Draw the process rectangle
            g2d.setColor(fcaiScreenPainter.color);
            g2d.fillRect(xOffset, yOffset, width, rowHeight - 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(xOffset, yOffset, width, rowHeight - 10);

            // Draw the process name
            g2d.drawString(fcaiScreenPainter.processName, xOffset + 5, yOffset + rowHeight / 2);

            // Update xOffset so the next process starts where the previous one ended
            xOffset += width;

            // Increment yOffset for the next process, ensuring each process is in a new row
            yOffset += rowHeight;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Calculate the total height of the chart based on the number of processes
        int totalHeight = processes.size() * rowHeight;
        return new Dimension(800, totalHeight);
    }
}




