package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeV4BSubsystem {

    public final Servo wristServo1;
    public final Servo wristServo2;

    private static final double POSITION_INCREMENT = 0.01;
    private static final int DELAY_MS = 20; // Delay between increments

    private static final double DEFAULT = 0.15;
    private static final double DROP = 0.38;
    private static final double PICKUP = 0.05;
    private static final double AUTO_PICKUP = 0.07;

    public IntakeV4BSubsystem(HardwareMap hardwareMap) {
        wristServo1 = hardwareMap.get(Servo.class, "wsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "wsrv2");

        wristServo1.setDirection(Servo.Direction.REVERSE);
    }

    public void setWristDropPosition() {
        smoothSetWristPosition(DROP, DROP);
    }

    public void setWristDefaultPosition() {
        smoothSetWristPosition(DEFAULT, DEFAULT);
    }

    public void setWristPickPosition() {
        smoothSetWristPosition(PICKUP, PICKUP);
    }
    public void setWristPickAutoPosition() {
        smoothSetWristPosition(AUTO_PICKUP, AUTO_PICKUP);
    }

    private void smoothSetWristPosition(double targetPos1, double targetPos2) {
        new Thread(() -> {
            smoothMoveServo(wristServo1, targetPos1);
        }).start();
        new Thread(() -> {
            smoothMoveServo(wristServo2, targetPos2);
        }).start();
    }

    private void smoothMoveServo(Servo servo, double targetPosition) {
        double currentPosition = servo.getPosition();
        while (Math.abs(currentPosition - targetPosition) > POSITION_INCREMENT) {
            currentPosition += Math.signum(targetPosition - currentPosition) * POSITION_INCREMENT;
            servo.setPosition(currentPosition);
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        servo.setPosition(targetPosition); // Ensure final precision
    }

    public void update() {

    }
}
