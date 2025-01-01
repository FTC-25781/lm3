package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.sensors.UltrasonicDistanceSensor;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class DepositSlideSubsystem implements Subsystem {

    private final DigitalChannel depositLimitSwitch;
    public final DcMotor verticalSlideMotor;
    public final DcMotor verticalSlideMotor2;
    public DepositV4BSubsystem depositV4B;
    public UltrasonicDistanceSensor rangeSensor;

    private static final int SLIDE_EXTEND_POS = 3500;
    private static final int SLIDE_RETRACT_POS = 2000;

    public static enum Deposit_state {
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

    public Deposit_state CURRENT_STATE= Deposit_state.UNINITIALISED;

    public final Telemetry telemetry;

    public DepositSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        depositV4B = new DepositV4BSubsystem(hardwareMap);

        verticalSlideMotor = hardwareMap.get(DcMotor.class, "vsmot");
        verticalSlideMotor2 = hardwareMap.get(DcMotor.class, "vsmot2");
        depositLimitSwitch = hardwareMap.get(DigitalChannel.class, "dpltsw");
        rangeSensor = new UltrasonicDistanceSensor(hardwareMap.get(AnalogInput.class, "vdist1"));

        verticalSlideMotor.setDirection(DcMotor.Direction.REVERSE);
        verticalSlideMotor2.setDirection(DcMotor.Direction.FORWARD);

        verticalSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        verticalSlideMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        depositLimitSwitch.setMode(DigitalChannel.Mode.INPUT);
        CURRENT_STATE = Deposit_state.INITIALISED;
    }

    public void manualExtension(double power) {
        if (!depositLimitSwitch.getState() && power > 0) {
            // Block further extension when limit switch is pressed
            verticalSlideMotor.setPower(0);
            verticalSlideMotor2.setPower(0);
        } else {
            verticalSlideMotor.setPower(clampPower(power*-1));
            verticalSlideMotor2.setPower(clampPower(power*-1));
        }
    }

    public Runnable extendDepositMainSlide() {
        if(CURRENT_STATE== DepositSlideSubsystem.Deposit_state.EXTENDING ||
        CURRENT_STATE == Deposit_state.EXTENDED)
            return null;

        verticalSlideMotor.setTargetPosition(SLIDE_EXTEND_POS);
        verticalSlideMotor2.setTargetPosition(SLIDE_EXTEND_POS);
        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        verticalSlideMotor.setPower(1);
        verticalSlideMotor2.setPower(1);
        CURRENT_STATE = Deposit_state.EXTENDING;
        return null;
    }

    public Runnable retractDepositMainSlide() {
        verticalSlideMotor.setTargetPosition(SLIDE_RETRACT_POS);
        verticalSlideMotor2.setTargetPosition(SLIDE_RETRACT_POS);
        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalSlideMotor.setPower(-1);
        verticalSlideMotor2.setPower(-1);
        return null;
    }


    public void stopSlides() {
        verticalSlideMotor.setPower(0);
        verticalSlideMotor2.setPower(0);
        verticalSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalSlideMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    public boolean isLimitSwitchPressed() {
        return !depositLimitSwitch.getState();
    }

    public void update() {
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (verticalSlideMotor.isBusy() && verticalSlideMotor2.isBusy()) {
                    telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
                    telemetry.update();
                }
                else {
                    verticalSlideMotor.setPower(0);
                    verticalSlideMotor2.setPower(0);
                    verticalSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    CURRENT_STATE = Deposit_state.EXTENDED;
                }
                break;
            case RETRACTING:
                if (verticalSlideMotor.isBusy() && verticalSlideMotor2.isBusy()) {
                    telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
                    telemetry.update();
                }
                else {
                    verticalSlideMotor.setPower(0);
                    verticalSlideMotor2.setPower(0);

                    verticalSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    CURRENT_STATE = Deposit_state.RETRACTED;
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
        telemetry.addData("Current encode value: ", verticalSlideMotor.getCurrentPosition());
        telemetry.addData("current state: ", CURRENT_STATE.name() );
        telemetry.update();
    }
}
