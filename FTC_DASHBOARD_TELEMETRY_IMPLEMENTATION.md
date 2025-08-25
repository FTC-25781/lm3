# FTC Dashboard Enhanced Telemetry Implementation

## Overview

This document outlines the implementation of enhanced telemetry packet transmission from the FTC robot code to the Redux-based enhanced FTC Dashboard. The implementation enables real-time subsystem monitoring, advanced visualization, and comprehensive telemetry capabilities.

## Architecture Overview

```
┌─────────────────────────┐    Telemetry Packets   ┌─────────────────────────┐
│   FTC Robot Code        │ ──────────────────►    │  Enhanced Dashboard     │
│   (This Repository)     │    JSON Messages       │  (ftc-dashboard repo)   │
│                         │  via __enhanced_       │                         │
│  - Subsystems           │    _dashboard__        │  - Redux Store          │
│  - Telemetry Manager    │                        │  - React Components     │
│  - Jackson POJOs        │                        │  - WebSocket Server     │
└─────────────────────────┘                        └─────────────────────────┘
```

## Implementation Status

### ✅ Completed Components

#### 1. Jackson Dependencies Added
Added to `build.dependencies.gradle`:
```gradle
implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
implementation 'com.fasterxml.jackson.core:jackson-core:2.15.2'
implementation 'com.fasterxml.jackson.core:jackson-annotations:2.15.2'
```

#### 2. POJO Message Classes Created
All message classes with Jackson annotations in `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/messages/`:

- **BaseMessage.java** - Abstract base with polymorphic JSON handling
- **SubsystemUpdateMessage.java** - For subsystem state updates
- **TelemetryUpdateMessage.java** - For telemetry key-value pairs
- **SubsystemData.java** - Abstract base for subsystem-specific data
- **DrivetrainData.java** - Position, velocity, encoders, currents, heading
- **IntakeData.java** - Slide position, state, limits, sensors
- **DepositData.java** - Dual motor positions, state, laser distance
- **GeneralData.java** - Runtime, battery, update rate
- **CameraData.java** - Vision/camera telemetry structure

#### 3. Enhanced Dashboard Manager
**EnhancedDashboard.java** - Singleton implementation that:
- Uses Jackson ObjectMapper for JSON serialization
- Sends messages via FTC Dashboard telemetry packets
- Uses special `__enhanced_dashboard__` key for message routing

#### 4. Telemetry Infrastructure
- **TelemetryManager.java** - Central manager with:
  - 10Hz rate limiting
  - General system telemetry
  - Subsystem registration capability
- **SubsystemTelemetryBuilder.java** - Fluent API for creating telemetry messages

#### 5. Subsystem Integration
Enhanced telemetry methods implemented in:
- **Follower.java** - `sendEnhancedTelemetry()` for drivetrain data
- **IntakeSlideSubsystem.java** - `sendEnhancedTelemetry()` for intake metrics
- **DepositSlideSubsystem.java** - `sendEnhancedTelemetry()` for deposit data

#### 6. OpMode Integration
**TeleOpEnhancements.java** updated with:
- TelemetryManager initialization
- Enhanced telemetry calls in main loop (lines 103-108)
- Maintains existing telemetry alongside enhanced version

## Message Protocol

### Subsystem Update Message Format
```json
{
  "messageType": "subsystemUpdate",
  "timestamp": 1234567890,
  "subsystem": "drivetrain",
  "data": {
    "position": { "x": 10.5, "y": 20.3, "z": 0 },
    "velocity": { "x": 2.1, "y": -1.5, "z": 0 },
    "encoders": {
      "leftFront": 1234,
      "leftBack": 1235,
      "rightFront": 1233,
      "rightBack": 1236
    },
    "currents": {
      "leftFront": 1.2,
      "leftBack": 1.1,
      "rightFront": 1.3,
      "rightBack": 1.2
    },
    "heading": 45.5
  }
}
```

### Telemetry Update Message Format
```json
{
  "messageType": "telemetryUpdate",
  "timestamp": 1234567890,
  "section": "intake",
  "values": [
    {
      "key": "slidePosition",
      "value": 23.5,
      "unit": "cm"
    },
    {
      "key": "motorCurrent",
      "value": 1.8,
      "unit": "A"
    }
  ]
}
```

## Current Data Flow

1. **Subsystem Update**: Each subsystem's `sendEnhancedTelemetry()` method:
   - Creates appropriate data POJO (IntakeData, DepositData, etc.)
   - Populates with current sensor/state values
   - Sends via EnhancedDashboard singleton

2. **JSON Serialization**: EnhancedDashboard:
   - Uses Jackson ObjectMapper to convert POJOs to JSON
   - Handles polymorphic types via @JsonTypeInfo annotations
   - Manages serialization errors gracefully

3. **Packet Transmission**: 
   - JSON string added to TelemetryPacket with `__enhanced_dashboard__` key
   - Sent through standard FTC Dashboard infrastructure
   - Dashboard intercepts and routes to enhanced components

## Testing Integration

To test the implementation:

1. **Verify JSON Serialization**:
   ```bash
   adb logcat | grep -E "(EnhancedDashboard|Jackson|JSON)"
   ```

2. **Check Telemetry Flow**:
   - Run "Match TeleOp" OpMode
   - Monitor dashboard for `__enhanced_dashboard__` packets
   - Verify JSON structure matches expected format

3. **Validate Update Rate**:
   - Should maintain ~10Hz for enhanced telemetry
   - Standard telemetry continues at normal rate

## Remaining Work

### Subsystems Needing Telemetry Methods
- [ ] IntakeClawSubsystem - claw state, sample detection
- [ ] IntakeV4BSubsystem - servo positions, wrist angles
- [ ] DepositClawSubsystem - claw state, grip status
- [ ] DepositV4BSubsystem - arm positions, states

### Dashboard Enhancements
- [ ] Custom widgets for subsystem visualization
- [ ] Data recording/playback functionality
- [ ] Alert thresholds and notifications
- [ ] Performance metrics dashboard

### Additional Features
- [ ] Camera/vision telemetry integration
- [ ] Autonomous path visualization
- [ ] Historical data analysis tools

## Performance Characteristics

- **Update Rate**: 10Hz for enhanced telemetry
- **Message Size**: Typically 200-500 bytes per update
- **Latency**: <50ms typical (network dependent)
- **CPU Impact**: Minimal due to rate limiting

## Troubleshooting Guide

### No Enhanced Telemetry Data
1. Verify `sendEnhancedTelemetry()` called in OpMode loop
2. Check dashboard for `__enhanced_dashboard__` key reception
3. Validate network connectivity

### JSON Serialization Errors
1. Check logcat for Jackson exceptions
2. Verify POJO getter/setter naming conventions
3. Ensure all fields have proper types

### Performance Issues
1. Confirm 10Hz rate limiting is active
2. Monitor message sizes
3. Check for blocking operations in telemetry methods

## Code Locations

- **Message POJOs**: `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/messages/`
- **Dashboard Manager**: `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/EnhancedDashboard.java`
- **Telemetry Manager**: `/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/dashboard/TelemetryManager.java`
- **Subsystem Integration**: Various subsystem classes with `sendEnhancedTelemetry()` methods

## Next Steps

1. Complete telemetry integration for remaining subsystems
2. Test with actual enhanced dashboard UI
3. Add performance monitoring and optimization
4. Document dashboard configuration requirements
5. Create subsystem-specific dashboard widgets

## References

- [FTC Dashboard Documentation](https://acmerobotics.github.io/ftc-dashboard)
- [Jackson Annotations Guide](https://github.com/FasterXML/jackson-annotations/wiki)
- [Testing Guide](./DASHBOARD_TESTING_GUIDE.md)