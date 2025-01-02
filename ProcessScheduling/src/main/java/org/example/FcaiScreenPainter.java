package org.example;

import java.awt.*;

public class FcaiScreenPainter {
    int xCoord, yCoord, width;
    Color color;
    String processName;
    public FcaiScreenPainter(int xCoord, int yCoord, int width, String processName, Color color) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.color = color;
        this.processName = processName;
    }
}
