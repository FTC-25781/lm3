package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

import java.util.concurrent.TimeUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DepositV4BSubsystem implements Subsystem {

    public final Servo wristServo1;
    public final Servo wristServo2;

    private static final double SPECIMEN_DROP = 1.0;
    private static final double DROP = 0.0;
    private static final double PICKUP = 0.70;

    private static final long POSITIONING_TIME_MS = 500; // Constant for positioning time

    public enum Depositv4b_state {
        INITIALISED,
        UNINITIALISED,
        PICK_POSITIONING,
        PICK_POSITION,
        DROP_POSITIONING,
        DROP_POSITION,
        SPECIMEN_POSITIONING,
        SPECIMEN_POSITION,
        STOPPED
    }

    private final ElapsedTime timer = new ElapsedTime();
    public long depositTimer = System.currentTimeMillis();

    public long depostTimerDiff;

    public final Telemetry telemetry;
    public Depositv4b_state CURRENT_STATE = DepositV4BSubsystem.Depositv4b_state.UNINITIALISED;

    public DepositV4BSubsystem(HardwareMap hardwareMap, Telemetry telemetry)
    {
        this.telemetry = telemetry;
        wristServo1 = hardwareMap.get(Servo.class, "dwsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "dwsrv2");

        wristServo1.setDirection(Servo.Direction.REVERSE);
        CURRENT_STATE = Depositv4b_state.INITIALISED;
    }

    public void setWristDropPosition() {
        if (CURRENT_STATE == Depositv4b_state.DROP_POSITIONING ||
        CURRENT_STATE == Depositv4b_state.DROP_POSITION) {
            return;
        }
        setWristPosition(DROP, DROP);
        CURRENT_STATE = Depositv4b_state.DROP_POSITIONING;
        timer.reset(); // Reset the timer
        depositTimer = System.currentTimeMillis();
    }

    public void setWristPickPosition() {
        if (CURRENT_STATE == Depositv4b_state.PICK_POSITIONING ||
        CURRENT_STATE == Depositv4b_state.PICK_POSITION) {
            return;
        }
        setWristPosition(PICKUP, PICKUP);
        timer.reset(); // Reset the timer
        CURRENT_STATE = Depositv4b_state.PICK_POSITIONING;
    }

    public void setWristSpecimenDropPosition() {
        if (CURRENT_STATE == Depositv4b_state.SPECIMEN_POSITIONING ||
        CURRENT_STATE == Depositv4b_state.SPECIMEN_POSITION) {
            return;
        }
        setWristPosition(SPECIMEN_DROP, SPECIMEN_DROP);
        timer.reset(); // Reset the timer
        CURRENT_STATE = Depositv4b_state.SPECIMEN_POSITIONING;
    }

    private void setWristPosition(double pos1, double pos2) {
        wristServo1.setPosition(pos1);
        wristServo2.setPosition(pos2);
    }

    @Override
    public void update() {
        switch (CURRENT_STATE) {
            case PICK_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) >= POSITIONING_TIME_MS) {
                    CURRENT_STATE = Depositv4b_state.PICK_POSITION;
                }
                break;
            case SPECIMEN_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) >= POSITIONING_TIME_MS) {
                    CURRENT_STATE = Depositv4b_state.SPECIMEN_POSITION;
                }
                break;
            case DROP_POSITIONING:
                if (timer.time(TimeUnit.MILLISECONDS) >= POSITIONING_TIME_MS) {
                    CURRENT_STATE = Depositv4b_state.DROP_POSITION;
                }
                break;
            case DROP_POSITION:
            case STOPPED:
            case SPECIMEN_POSITION:
            case INITIALISED:
            case UNINITIALISED:
            default:
                break;
        }
    }
}
