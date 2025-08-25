package org.firstinspires.ftc.teamcode.dashboard.messages;

public class SubsystemUpdateMessage extends BaseMessage {
    private String subsystem;
    private SubsystemData data;
    
    public SubsystemUpdateMessage() {}
    
    public SubsystemUpdateMessage(String subsystem, SubsystemData data) {
        super();
        this.subsystem = subsystem;
        this.data = data;
    }
    
    public String getSubsystem() { 
        return subsystem; 
    }
    
    public void setSubsystem(String subsystem) { 
        this.subsystem = subsystem; 
    }
    
    public SubsystemData getData() { 
        return data; 
    }
    
    public void setData(SubsystemData data) { 
        this.data = data; 
    }
}