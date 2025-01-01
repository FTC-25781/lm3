package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.TimeUnit;

public class IntakeSlideSubsystem {

    public final DcMotor slideMotor;
    private final DigitalChannel intakeLimitSwitch;
    public final Telemetry telemetry;

    private static final int SLIDE_EXTEND_POS = -5700;
    private static final double SLIDE_EXTEND_SPEED = 0.5;

    public static enum Intake_state {
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
    public IntakeSlideSubsystem.Intake_state CURRENT_STATE= IntakeSlideSubsystem.Intake_state.UNINITIALISED;

    public IntakeSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        slideMotor = hardwareMap.get(DcMotor.class, "hsmot");
        intakeLimitSwitch = hardwareMap.get(DigitalChannel.class, "inltsw");

        intakeLimitSwitch.setMode(DigitalChannel.Mode.INPUT);

        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.INITIALISED;
    }

    public void manualExtension(double power) {
        slideMotor.setPower(clampMotorPower(power));
    }

    public Runnable extendMainSlide() {
        if(CURRENT_STATE== IntakeSlideSubsystem.Intake_state.EXTENDING ||
                CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED)
            return null;

        slideMotor.setTargetPosition(SLIDE_EXTEND_POS);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setPower(-1);

        CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDING;
        return null;
    }

    public Runnable retractMainSlide() {
        if (!intakeLimitSwitch.getState()) {
            stopAndResetSlide();
        } else {
            slideMotor.setPower(1);
        }

        CURRENT_STATE = Intake_state.RETRACTING;
        return null;
    }

    private void stopAndResetSlide() {
        slideMotor.setPower(0);
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private double clampMotorPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    public void update() {
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (slideMotor.isBusy()) {
                    telemetry.addData("current position: ", slideMotor.getCurrentPosition());
                    telemetry.update();
                }
                else {
                    slideMotor.setPower(0);
                    slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    CURRENT_STATE = IntakeSlideSubsystem.Intake_state.EXTENDED;
                }
                break;
            case RETRACTING:
                if (slideMotor.isBusy()) {
                    telemetry.addData("current position: ", slideMotor.getCurrentPosition());
                    telemetry.update();
                }
                else {
                    slideMotor.setPower(0);
                    slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        telemetry.addData("Current encoder value: ", slideMotor.getCurrentPosition());
        telemetry.addData("current state: ", CURRENT_STATE.name() );
        telemetry.update();
    }
}