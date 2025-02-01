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
    private static final double PICKUP_AUTO = 0.07;
    private static final double AUTO_PICKUP = 0.07;
    private static final long POSITIONING_TIME_MS = 1000; // Constant for positioning time

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

    public ElapsedTime timer = new ElapsedTime();

    public IntakeV4BSubsystem.Intakev4b_state CURRENT_STATE = IntakeV4BSubsystem.Intakev4b_state.UNINITIALISED;

    public IntakeV4BSubsystem(HardwareMap hardwareMap) {
        wristServo1 = hardwareMap.get(Servo.class, "wsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "wsrv2");

        wristServo1.setDirection(Servo.Direction.REVERSE);

        CURRENT_STATE = Intakev4b_state.INITIALISED;
    }

    public void setWristDropPosition() {
        if (CURRENT_STATE == Intakev4b_state.DROP_POSITIONING ||
        CURRENT_STATE == Intakev4b_state.DROP_POSITION) {
            return;
        }
        wristServo1.setPosition(DROP);
        wristServo1.setPosition(DROP);
        CURRENT_STATE = Intakev4b_state.DROP_POSITIONING;
        timer.reset();
    }

    public void setWristDefaultPosition() {
        if (CURRENT_STATE == Intakev4b_state.DEFAULT_POSITIONING ||
        CURRENT_STATE == Intakev4b_state.DEFAULT_POSITION) {
            return;
        }
        wristServo1.setPosition(DEFAULT);
        wristServo1.setPosition(DEFAULT);
        CURRENT_STATE = Intakev4b_state.DEFAULT_POSITIONING;
        timer.reset();
    }

    public void setWristPickPosition() {
        if (CURRENT_STATE == Intakev4b_state.PICK_POSITIONING ||
        CURRENT_STATE == Intakev4b_state.PICK_POSITION) {
            return;
        }
        wristServo1.setPosition(PICKUP);
        wristServo1.setPosition(PICKUP);
        CURRENT_STATE = Intakev4b_state.PICK_POSITIONING;
        timer.reset();
    }

    public void setWristPickAutoPosition() {
        if (CURRENT_STATE == Intakev4b_state.AUTO_POSITIONING ||
                CURRENT_STATE == Intakev4b_state.AUTO_POSITION) {
            return;
        }
        wristServo1.setPosition(AUTO_PICKUP);
        wristServo1.setPosition(AUTO_PICKUP);
        CURRENT_STATE = Intakev4b_state.AUTO_POSITIONING;
        timer.reset();
    }


    public void update() {
        switch (CURRENT_STATE) {
            case DROP_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = Intakev4b_state.DROP_POSITION;
                }
                break;
            case PICK_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = Intakev4b_state.PICK_POSITION;
                }
                break;
            case AUTO_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = Intakev4b_state.AUTO_POSITION;
                }
                break;
            case DEFAULT_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) > POSITIONING_TIME_MS) {
                    CURRENT_STATE = Intakev4b_state.DEFAULT_POSITION;
                }
                break;
            case DROP_POSITION:
            case PICK_POSITION:
            case AUTO_POSITION:
            case DEFAULT_POSITION:
            case UNINITIALISED:
            case INITIALISED:
            case STOPPED:
            default:
                break;
        }
    }
}
