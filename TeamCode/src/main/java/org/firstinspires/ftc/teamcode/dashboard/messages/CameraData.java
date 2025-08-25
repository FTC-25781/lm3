package org.firstinspires.ftc.teamcode.dashboard.messages;

import java.util.List;

public class CameraData extends SubsystemData {
    private List<DetectedObject> detectedObjects;
    private double frameRate;
    private double processingTime;
    
    public CameraData() {}
    
    public static class DetectedObject {
        private double x;
        private double y;
        private double width;
        private double height;
        private double confidence;
        private String label;
        private String color;
        
        public DetectedObject() {}
        
        // Getters and setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        
        public double getWidth() { return width; }
        public void setWidth(double width) { this.width = width; }
        
        public double getHeight() { return height; }
        public void setHeight(double height) { this.height = height; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    // Getters and setters
    public List<DetectedObject> getDetectedObjects() { 
        return detectedObjects; 
    }
    
    public void setDetectedObjects(List<DetectedObject> detectedObjects) { 
        this.detectedObjects = detectedObjects; 
    }
    
    public double getFrameRate() { 
        return frameRate; 
    }
    
    public void setFrameRate(double frameRate) { 
        this.frameRate = frameRate; 
    }
    
    public double getProcessingTime() { 
        return processingTime; 
    }
    
    public void setProcessingTime(double processingTime) { 
        this.processingTime = processingTime; 
    }
}