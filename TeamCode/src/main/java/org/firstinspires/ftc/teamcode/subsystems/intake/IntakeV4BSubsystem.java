package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositSlideSubsystem;

import java.util.concurrent.TimeUnit;

public class IntakeV4BSubsystem {

    public final Servo wristServo1;
    public final Servo wristServo2;

    private static final double POSITION_INCREMENT = 0.01;
    private static final int DELAY_MS = 20; // Delay between increments

    private static final double DEFAULT = 0.15;
    private static final double DROP = 0.4;
    private static final double PICKUP = 0.05;
    private static final double AUTO_PICKUP = 0.07;

    public static enum Intakev4b_state {
        INITIALISED,
        UNINITIALISED,
        PICK_POSITIONING,
        PICK_POSITION,
        DROP_POSITIONING,
        DROP_POSITION,
        DEFAULT_POSITIONING,
        DEFAULT_POSITION,
        AUTO_POSITIONING,
        AUTO_POSITION,
        STOPPED
    }

    ElapsedTime timer = new ElapsedTime();

    public IntakeV4BSubsystem.Intakev4b_state CURRENT_STATE = IntakeV4BSubsystem.Intakev4b_state.UNINITIALISED;

    public IntakeV4BSubsystem(HardwareMap hardwareMap) {
        wristServo1 = hardwareMap.get(Servo.class, "wsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "wsrv2");

        wristServo1.setDirection(Servo.Direction.REVERSE);

        CURRENT_STATE = Intakev4b_state.INITIALISED;
    }

    public Runnable setWristDropPosition() {
        wristServo1.setPosition(DROP);
        wristServo1.setPosition(DROP);
        CURRENT_STATE = Intakev4b_state.DROP_POSITIONING;
        timer.reset();
        return null;
    }

    public Runnable setWristDefaultPosition() {
        wristServo1.setPosition(DEFAULT);
        wristServo1.setPosition(DEFAULT);
        CURRENT_STATE = Intakev4b_state.DEFAULT_POSITIONING;
        timer.reset();
        return null;
    }

    public Runnable setWristPickPosition() {
        wristServo1.setPosition(PICKUP);
        wristServo1.setPosition(PICKUP);
        CURRENT_STATE = Intakev4b_state.PICK_POSITIONING;
        timer.reset();
        return null;
    }
    public Runnable setWristPickAutoPosition() {
        smoothSetWristPosition(AUTO_PICKUP, AUTO_PICKUP);
        CURRENT_STATE = Intakev4b_state.AUTO_POSITIONING;
        timer.reset();
        return null;
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
        switch (CURRENT_STATE) {
            case DROP_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = Intakev4b_state.DROP_POSITION;
                }
                break;
            case PICK_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = Intakev4b_state.PICK_POSITION;
                }
                break;
            case AUTO_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = Intakev4b_state.AUTO_POSITION;
                }
                break;
            case DEFAULT_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > 1000) {
                    CURRENT_STATE = Intakev4b_state.DEFAULT_POSITION;
                }
                break;
            case DROP_POSITION:
                break;
            case PICK_POSITION:
                break;
            case AUTO_POSITION:
                break;
            case DEFAULT_POSITION:
                break;
            case UNINITIALISED:
                break;
            case INITIALISED:
                break;
            case STOPPED:
                break;
            default:
                break;
        }
    }
}
