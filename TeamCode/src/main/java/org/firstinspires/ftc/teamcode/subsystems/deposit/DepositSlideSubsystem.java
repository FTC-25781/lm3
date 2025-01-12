package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.sensors.UltrasonicDistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

import java.util.concurrent.TimeUnit;

public class DepositSlideSubsystem implements Subsystem {

    private final DigitalChannel depositLimitSwitch;
    public final DcMotorImplEx verticalSlideMotor;
    public final DcMotorImplEx verticalSlideMotor2;
    public DepositV4BSubsystem depositV4B;
    public UltrasonicDistanceSensor rangeSensor;

    private static final int MAX_HEIGHT = 44;
    private static final int V4B_HEIGHT = 22;
    private static final int RETRACT_HEIGHT = 18;


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

    public Deposit_state CURRENT_STATE = Deposit_state.UNINITIALISED;
    ElapsedTime timer = new ElapsedTime();
    public final Telemetry telemetry;

    public DepositSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        depositV4B = new DepositV4BSubsystem(hardwareMap, telemetry);

        verticalSlideMotor = hardwareMap.get(DcMotorImplEx.class, "vsmot");
        verticalSlideMotor2 = hardwareMap.get(DcMotorImplEx.class, "vsmot2");
        depositLimitSwitch = hardwareMap.get(DigitalChannel.class, "dpltsw");
        rangeSensor = new UltrasonicDistanceSensor(hardwareMap.get(AnalogInput.class, "vdist1"));

        verticalSlideMotor.setDirection(DcMotor.Direction.REVERSE);
        verticalSlideMotor2.setDirection(DcMotor.Direction.FORWARD);

        verticalSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        verticalSlideMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        depositLimitSwitch.setMode(DigitalChannel.Mode.INPUT);
        CURRENT_STATE = Deposit_state.INITIALISED;
    }

    // Gamepad slides go up
    public void manualExtension(double power) {
        if (!depositLimitSwitch.getState())  {
            // Block further extension when limit switch is pressed
            verticalSlideMotor.setPower(0.1);
            verticalSlideMotor2.setPower(0.1);
        } else {
            verticalSlideMotor.setPower(clampPower(power*-0.8));
            verticalSlideMotor2.setPower(clampPower(power*-0.8));
        }
    }

    // auto get slides up
    public Runnable extendDepositMainSlide() {
        if(CURRENT_STATE== DepositSlideSubsystem.Deposit_state.EXTENDING ||
        CURRENT_STATE == Deposit_state.EXTENDED)
            return null;


        if (rangeSensor.getDistance(DistanceUnit.INCH) >= MAX_HEIGHT) {
            return null;
        }

        verticalSlideMotor.setPower(0.8);
        verticalSlideMotor2.setPower(0.8);
        CURRENT_STATE = Deposit_state.EXTENDING;
        return null;
    }

    // auto retract slides
    public Runnable retractDepositMainSlide() {
        //ToDo: Check is already at or below retract position if so return null.
        if (rangeSensor.getDistance(DistanceUnit.INCH) <= RETRACT_HEIGHT) {
            return null;
        }

        verticalSlideMotor.setPower(-1);
        verticalSlideMotor2.setPower(-1);
        // ToDo: set the state
        CURRENT_STATE = Deposit_state.RETRACTING;
        return null;
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    public void update() {
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (rangeSensor.getDistance(DistanceUnit.INCH) < MAX_HEIGHT) {
                    telemetry.addData("current value: ", rangeSensor.getDistance(DistanceUnit.INCH));
                    telemetry.update();
                }
                else {
                    verticalSlideMotor.setPower(0.2);
                    verticalSlideMotor2.setPower(0.2);
                    CURRENT_STATE = Deposit_state.EXTENDED;
                }
//
//                if (rangeSensor.getDistance(DistanceUnit.INCH) > V4B_HEIGHT) {
//                    depositV4B.setWristSpecimenDropPosition();
//                }

                break;
            case RETRACTING:
                if (rangeSensor.getDistance(DistanceUnit.INCH) < RETRACT_HEIGHT) {
                    telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
                    telemetry.update();
                }
                else {
                    verticalSlideMotor.setPower(0.2);
                    verticalSlideMotor2.setPower(0.2);
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
    }
}
