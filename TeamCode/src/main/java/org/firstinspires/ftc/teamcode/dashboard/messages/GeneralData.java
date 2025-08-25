package org.firstinspires.ftc.teamcode.dashboard.messages;

public class GeneralData extends SubsystemData {
    private boolean opModeActive;
    private double runtime;
    private double updateRate;
    private double batteryVoltage;
    
    // Constructors
    public GeneralData() {}
    
    // Getters and setters
    public boolean isOpModeActive() { 
        return opModeActive; 
    }
    
    public void setOpModeActive(boolean opModeActive) { 
        this.opModeActive = opModeActive; 
    }
    
    public double getRuntime() { 
        return runtime; 
    }
    
    public void setRuntime(double runtime) { 
        this.runtime = runtime; 
    }
    
    public double getUpdateRate() { 
        return updateRate; 
    }
    
    public void setUpdateRate(double updateRate) { 
        this.updateRate = updateRate; 
    }
    
    public double getBatteryVoltage() { 
        return batteryVoltage; 
    }
    
    public void setBatteryVoltage(double batteryVoltage) { 
        this.batteryVoltage = batteryVoltage; 
    }
}