package org.firstinspires.ftc.teamcode.dashboard.messages;

import java.util.List;

public class TelemetryUpdateMessage extends BaseMessage {
    private String section;
    private List<TelemetryValue> values;
    
    public TelemetryUpdateMessage() {}
    
    public TelemetryUpdateMessage(String section, List<TelemetryValue> values) {
        super();
        this.section = section;
        this.values = values;
    }
    
    public String getSection() { 
        return section; 
    }
    
    public void setSection(String section) { 
        this.section = section; 
    }
    
    public List<TelemetryValue> getValues() { 
        return values; 
    }
    
    public void setValues(List<TelemetryValue> values) { 
        this.values = values; 
    }
    
    public static class TelemetryValue {
        private String key;
        private Object value;
        private String unit;
        
        public TelemetryValue() {}
        
        public TelemetryValue(String key, Object value, String unit) {
            this.key = key;
            this.value = value;
            this.unit = unit;
        }
        
        public TelemetryValue(String key, Object value) {
            this(key, value, null);
        }
        
        // Getters and setters
        public String getKey() { 
            return key; 
        }
        
        public void setKey(String key) { 
            this.key = key; 
        }
        
        public Object getValue() { 
            return value; 
        }
        
        public void setValue(Object value) { 
            this.value = value; 
        }
        
        public String getUnit() { 
            return unit; 
        }
        
        public void setUnit(String unit) { 
            this.unit = unit; 
        }
    }
}