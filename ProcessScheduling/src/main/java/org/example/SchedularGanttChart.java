package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class SchedularGanttChart extends JPanel {
    private List<FcaiScreenPainter> processes;

    public SchedularGanttChart() {
        this.processes = new ArrayList<>();
    }
    public void updateProcess(FcaiScreenPainter fcaiScreenPainter) {
        this.processes.add(fcaiScreenPainter);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        int rowHeight = 40;

        for (FcaiScreenPainter fcaiScreenPainter : processes) {
            int y = fcaiScreenPainter.yCoord;
            int x = fcaiScreenPainter.xCoord * 10;
            int width = (fcaiScreenPainter.width) * 10;

            graphics.setColor(fcaiScreenPainter.color);
            graphics.fillRect(x, y, width, rowHeight - 10);
            graphics.setColor(Color.BLACK);
            graphics.drawString(fcaiScreenPainter.processName, x + 5, y + rowHeight / 2);
        }
    }

    @Override
    public Dimension getPreferredSize() {

        return new Dimension(800, 400);
    }
}
