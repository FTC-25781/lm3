package org.firstinspires.ftc.teamcode.sensors;

import java.util.LinkedList;

public class MovingAverageWithOutlier {
    private final LinkedList<Double> window = new LinkedList<>();
    private final int windowSize;
    private final double outlierThreshold;

    public MovingAverageWithOutlier(int windowSize, double outlierThreshold) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be greater than 0");
        }
        this.windowSize = windowSize;
        this.outlierThreshold = outlierThreshold;
    }

    public double add(double value) {
        // check if the window is empty for the first entry
        if(window.isEmpty()) {
            window.add(value);
            return getAverage();
        }
        // Check for outlier
        // Ignore outlier, keep previous average
        // if (isOutlier(value)) { return getAverage(); }
        // Add new value to the window
        window.add(value);
        // Remove oldest value if window exceeds size
        if (window.size() > windowSize) { window.poll(); }
        return getAverage();
    }

    private boolean isOutlier(double value) {
        double avg = getAverage();
        double deviation = Math.abs(value - avg);
        return deviation > outlierThreshold;
    }

    private double getAverage() {
        if (window.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double num : window)
        {
            sum += num;
        }
        return sum / window.size();
    }
}
