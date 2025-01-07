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
    private final DistanceSensor sensorDistance;
    private final DigitalChannel intakeLimitSwitch;
    public final Telemetry telemetry;

    private final double MAX_POS = 15.1;
    private final int MIN_POS = 8;
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

        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor) sensorDistance;

        intakeLimitSwitch.setMode(DigitalChannel.Mode.INPUT);

        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.INITIALISED;
    }

    public void manualExtension(double power) {
        slideMotor.setPower(clampMotorPower(power));
    }

    public Runnable extendMainSlide() {
        if (CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDING ||
                CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED)
            return null;

        if (sensorDistance.getDistance(DistanceUnit.CM) >= MAX_POS) return null;

        slideMotor.setPower(-1);

        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDING;
        return null;
    }

    public Runnable retractMainSlide() {
        if (CURRENT_STATE == Intake_state.RETRACTING ||
                CURRENT_STATE == Intake_state.RETRACTED)
            return null;

        if (sensorDistance.getDistance(DistanceUnit.CM) <= MIN_POS) return null;

        slideMotor.setPower(1);
        CURRENT_STATE = Intake_state.RETRACTING;
        return null;
    }

    private void stopAndResetSlide() {
        slideMotor.setPower(0);
    }

    private double clampMotorPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    @SuppressLint("DefaultLocale")
    public void update() {
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (sensorDistance.getDistance(DistanceUnit.CM) < MAX_POS) {
                    telemetry.addData("current position: ", slideMotor.getCurrentPosition());
                    telemetry.update();
                } else {
                    slideMotor.setPower(0);
                    CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDED;
                }
                break;
            case RETRACTING:
                if (sensorDistance.getDistance(DistanceUnit.CM) > MIN_POS) {
                    telemetry.addData("current position: ", slideMotor.getCurrentPosition());
                    telemetry.update();
                } else {
                    slideMotor.setPower(0);
                    CURRENT_STATE = IntakeSlideSubsystem.Intake_state.RETRACTED;
                }
                break;
            case EXTENDED:
                break;
            case STOPPED:
                break;
            case RETRACTED:
                break;
            case INITIALISED:
                break;
            case LIMIT_SW_HIT:
                break;
            case UNINITIALISED:
                break;
            case LIMIT_SW_NOT_HIT:
                break;
            default:
                break;
        }

        telemetry.addData("range", String.format("%.01f cm", sensorDistance.getDistance(DistanceUnit.CM)));
        telemetry.addData("Current encoder value: ", slideMotor.getCurrentPosition());
        telemetry.addData("current state: ", CURRENT_STATE.name());
        telemetry.update();
    }
}