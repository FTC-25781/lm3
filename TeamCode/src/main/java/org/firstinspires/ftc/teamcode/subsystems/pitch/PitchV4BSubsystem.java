package org.firstinspires.ftc.teamcode.subsystems.pitch;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

import java.util.concurrent.TimeUnit;

public class PitchV4BSubsystem implements Subsystem {

    public final Servo wristServo1;
    public final Servo wristServo2;

    private static final double SPECIMEN_DROP = 1.0;
    private static final double DROP = 0.0;
    private static final double PICKUP = 0.70;

    private static final long POSITIONING_TIME_MS = 1000; // Constant for positioning time

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
    public Depositv4b_state CURRENT_STATE = Depositv4b_state.UNINITIALISED;

    public PitchV4BSubsystem(HardwareMap hardwareMap, Telemetry telemetry)
    {
        this.telemetry = telemetry;
        wristServo1 = hardwareMap.get(Servo.class, "dwsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "dwsrv2");

        wristServo1.setDirection(Servo.Direction.REVERSE);
        CURRENT_STATE = Depositv4b_state.INITIALISED;
    }

    public Runnable setWristDropPosition() {
        setWristPosition(DROP, DROP);
        CURRENT_STATE = Depositv4b_state.DROP_POSITIONING;
        depositTimer = System.currentTimeMillis();
        timer.reset(); // Reset the timer
        timer.startTime();
        return null;
    }

    public Runnable setWristPickPosition() {
        setWristPosition(PICKUP, PICKUP);
        CURRENT_STATE = Depositv4b_state.PICK_POSITIONING;
        timer.reset(); // Reset the timer
        timer.startTime();
        return null;
    }

    public Runnable setWristSpecimenDropPosition() {
        timer.reset(); // Reset the timer
        setWristPosition(SPECIMEN_DROP, SPECIMEN_DROP);
        CURRENT_STATE = Depositv4b_state.SPECIMEN_POSITIONING;
        return null;
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
                depostTimerDiff = timer.time(TimeUnit.MILLISECONDS);
 //               if ( depostTimerDiff > 1000) {
                if (timer.time(TimeUnit.MILLISECONDS) >= POSITIONING_TIME_MS) {
                    CURRENT_STATE = Depositv4b_state.DROP_POSITION;
                }
                break;

            case PICK_POSITION:
                break;

            case DROP_POSITION:
                break;

            case STOPPED:
                break;

            case SPECIMEN_POSITION:
                break;

            case INITIALISED:
                break;

            case UNINITIALISED:
                break;

            default:
                break;
        }
    }
}
