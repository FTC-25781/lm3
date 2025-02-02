package org.firstinspires.ftc.teamcode.subsystems.intake;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class IntakeSlideSubsystem {

    public final DcMotor slideMotor;
    public final DistanceSensor sensorDistance;
    private final DigitalChannel intakeLimitSwitch;
    public final Telemetry telemetry;

    private final double MAX_POS = 37;
    private final int MIN_POS = 23;
    private final int EXCHANGE_POS = 20; // TODO: find exchange value

    public enum Intake_state {
        INITIALISED,
        UNINITIALISED,
        EXTENDING,
        EXTENDED,
        RETRACTING,
        RETRACTED,
        STOPPED,
        LIMIT_SW_HIT,
        LIMIT_SW_NOT_HIT
    }

    public IntakeSlideSubsystem.Intake_state CURRENT_STATE = IntakeSlideSubsystem.Intake_state.UNINITIALISED;

    public IntakeSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        slideMotor = hardwareMap.get(DcMotor.class, "hsmot");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "hlas");
        intakeLimitSwitch = hardwareMap.get(DigitalChannel.class, "inltsw");

        intakeLimitSwitch.setMode(DigitalChannel.Mode.INPUT);

        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.INITIALISED;
    }

    public void manualExtension(double power) {
        slideMotor.setPower(clampMotorPower(power));
    }

    public void extendMainSlide() {
        if (CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDING ||
                CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED ||
                sensorDistance.getDistance(DistanceUnit.CM) >= MAX_POS) {
            return;
        }

        slideMotor.setPower(-1);
        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDING;
    }

    public void retractMainSlide() {
        if (CURRENT_STATE == Intake_state.RETRACTING ||
                CURRENT_STATE == Intake_state.RETRACTED ||
                sensorDistance.getDistance(DistanceUnit.CM) <= MIN_POS) {
            return;
        }

        slideMotor.setPower(1);
        CURRENT_STATE = Intake_state.RETRACTING;
    }

    private double clampMotorPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    @SuppressLint("DefaultLocale")
    public void update() {
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (sensorDistance.getDistance(DistanceUnit.CM) > MAX_POS) {
                    slideMotor.setPower(0);
                    CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDED;
                }
                break;
            case RETRACTING:
                if (sensorDistance.getDistance(DistanceUnit.CM) < MIN_POS) {
                    slideMotor.setPower(0);
                    CURRENT_STATE = IntakeSlideSubsystem.Intake_state.RETRACTED;
                }
                break;
            case EXTENDED:
            case STOPPED:
            case RETRACTED:
            case INITIALISED:
            case LIMIT_SW_HIT:
            case UNINITIALISED:
            case LIMIT_SW_NOT_HIT:
            default:
                break;
        }

    }
}