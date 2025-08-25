package org.firstinspires.ftc.teamcode.dashboard.messages;

import java.util.Map;

public class DrivetrainData extends SubsystemData {
    private Position position;
    private Velocity velocity;
    private Map<String, Integer> encoders;
    private Map<String, Double> currents;
    private double heading;
    
    public static class Position {
        private double x;
        private double y;
        private double z;
        
        public Position() {}
        
        public Position(double x, double y, double z) {
            this.x = x; 
            this.y = y; 
            this.z = z;
        }
        
        // Getters and setters
        public double getX() { 
            return x; 
        }
        
        public void setX(double x) { 
            this.x = x; 
        }
        
        public double getY() { 
            return y; 
        }
        
        public void setY(double y) { 
            this.y = y; 
        }
        
        public double getZ() { 
            return z; 
        }
        
        public void setZ(double z) { 
            this.z = z; 
        }
    }
    
    public static class Velocity {
        private double x;
        private double y;
        private double z;
        
        public Velocity() {}
        
        public Velocity(double x, double y, double z) {
            this.x = x; 
            this.y = y; 
            this.z = z;
        }
        
        // Getters and setters
        public double getX() { 
            return x; 
        }
        
        public void setX(double x) { 
            this.x = x; 
        }
        
        public double getY() { 
            return y; 
        }
        
        public void setY(double y) { 
            this.y = y; 
        }
        
        public double getZ() { 
            return z; 
        }
        
        public void setZ(double z) { 
            this.z = z; 
        }
    }
    
    // Getters and setters
    public Position getPosition() { 
        return position; 
    }
    
    public void setPosition(Position position) { 
        this.position = position; 
    }
    
    public Velocity getVelocity() { 
        return velocity; 
    }
    
    public void setVelocity(Velocity velocity) { 
        this.velocity = velocity; 
    }
    
    public Map<String, Integer> getEncoders() { 
        return encoders; 
    }
    
    public void setEncoders(Map<String, Integer> encoders) { 
        this.encoders = encoders; 
    }
    
    public Map<String, Double> getCurrents() { 
        return currents; 
    }
    
    public void setCurrents(Map<String, Double> currents) { 
        this.currents = currents; 
    }
    
    public double getHeading() { 
        return heading; 
    }
    
    public void setHeading(double heading) { 
        this.heading = heading; 
    }
}