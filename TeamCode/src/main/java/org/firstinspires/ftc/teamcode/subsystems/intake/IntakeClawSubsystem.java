package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeClawSubsystem {

    public final Servo clawServo;
    public final Servo orientationServo;

    private static final double CLAW_OPEN_POS = 0.77;
    private static final double CLAW_CLOSED_POS = 1.0;
    private double currentOrientation = 0.0;

    public IntakeClawSubsystem(HardwareMap hardwareMap) {
        clawServo = hardwareMap.get(Servo.class, "clsrv");
        orientationServo = hardwareMap.get(Servo.class, "orsrv");

        if (clawServo == null || orientationServo == null) {
            throw new IllegalArgumentException("Failed to initialize one or more servos.");
        }
    }

    // Moves orientation servo to preset position
    public void runToPreset() {
        orientationServo.setPosition(1.0);
    }

    // Increases orientation by 10 degrees, capping at 90 degrees
    public void setOrientationIncrease() {
        if (currentOrientation < 90.0) {
            currentOrientation += 10.0;
            double servoPosition = currentOrientation / 180.0;
            orientationServo.setPosition(orientationClamp(servoPosition));
        }
    }

    public void setOrientationDecrease() {
        if (currentOrientation > 0) {
            currentOrientation -= 10.0;
            double servoPosition = currentOrientation / 180.0;
            orientationServo.setPosition(orientationClamp(servoPosition));
        }
    }

    // Opens the claw to a pre-defined position
    public void openClaw() {
        clawServo.setPosition(CLAW_OPEN_POS);
    }

    // Closes the claw to a pre-defined position
    public void closeClaw() {
        clawServo.setPosition(CLAW_CLOSED_POS);
    }

    private double orientationClamp(double value) {
        return Math.max(0.0, Math.min(0.55, value));
    }

    public void update() {

    }
}
