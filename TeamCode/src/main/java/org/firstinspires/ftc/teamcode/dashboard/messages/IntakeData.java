package org.firstinspires.ftc.teamcode.dashboard.messages;

public class IntakeData extends SubsystemData {
    private double slidePosition;
    private double targetPosition;
    private String state;
    private double motorPower;
    private boolean limitSwitch;
    private boolean sampleDetected;
    
    // Constructors
    public IntakeData() {}
    
    // Getters and setters
    public double getSlidePosition() { 
        return slidePosition; 
    }
    
    public void setSlidePosition(double slidePosition) { 
        this.slidePosition = slidePosition; 
    }
    
    public double getTargetPosition() { 
        return targetPosition; 
    }
    
    public void setTargetPosition(double targetPosition) { 
        this.targetPosition = targetPosition; 
    }
    
    public String getState() { 
        return state; 
    }
    
    public void setState(String state) { 
        this.state = state; 
    }
    
    public double getMotorPower() { 
        return motorPower; 
    }
    
    public void setMotorPower(double motorPower) { 
        this.motorPower = motorPower; 
    }
    
    public boolean isLimitSwitch() { 
        return limitSwitch; 
    }
    
    public void setLimitSwitch(boolean limitSwitch) { 
        this.limitSwitch = limitSwitch; 
    }
    
    public boolean isSampleDetected() { 
        return sampleDetected; 
    }
    
    public void setSampleDetected(boolean sampleDetected) { 
        this.sampleDetected = sampleDetected; 
    }
}