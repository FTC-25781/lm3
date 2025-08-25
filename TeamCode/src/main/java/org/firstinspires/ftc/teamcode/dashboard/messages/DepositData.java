package org.firstinspires.ftc.teamcode.dashboard.messages;

public class DepositData extends SubsystemData {
    private double slidePosition;
    private double slidePosition2;
    private double targetPosition;
    private String state;
    private double leftMotorPower;
    private double rightMotorPower;
    private double laserDistance;
    private boolean depositComplete;
    
    // Constructors
    public DepositData() {}
    
    // Getters and setters
    public double getSlidePosition() { 
        return slidePosition; 
    }
    
    public void setSlidePosition(double slidePosition) { 
        this.slidePosition = slidePosition; 
    }
    
    public double getSlidePosition2() { 
        return slidePosition2; 
    }
    
    public void setSlidePosition2(double slidePosition2) { 
        this.slidePosition2 = slidePosition2; 
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
    
    public double getLeftMotorPower() { 
        return leftMotorPower; 
    }
    
    public void setLeftMotorPower(double leftMotorPower) { 
        this.leftMotorPower = leftMotorPower; 
    }
    
    public double getRightMotorPower() { 
        return rightMotorPower; 
    }
    
    public void setRightMotorPower(double rightMotorPower) { 
        this.rightMotorPower = rightMotorPower; 
    }
    
    public double getLaserDistance() { 
        return laserDistance; 
    }
    
    public void setLaserDistance(double laserDistance) { 
        this.laserDistance = laserDistance; 
    }
    
    public boolean isDepositComplete() { 
        return depositComplete; 
    }
    
    public void setDepositComplete(boolean depositComplete) { 
        this.depositComplete = depositComplete; 
    }
}