package org.firstinspires.ftc.teamcode.subsystems.pitch;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.sensors.MovingAverageWithOutlier;
import org.firstinspires.ftc.teamcode.sensors.UltrasonicDistanceSensor;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class PitchSlideSubsystem implements Subsystem {

    private final DigitalChannel depositLimitSwitch;
    public final DcMotorImplEx verticalSlideMotor;
    public final DcMotorImplEx verticalSlideMotor2;
    public PitchV4BSubsystem depositV4B;
    public UltrasonicDistanceSensor rangeSensor;
    public double verticalDistance = 0;
    private static final int MAX_HEIGHT = 42;
    private static final int V4B_HEIGHT = 30;
    private static final int RETRACT_HEIGHT = 9;

    private final MovingAverageWithOutlier movingAverage;

    public enum Deposit_state {
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

    public PitchSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        movingAverage = new MovingAverageWithOutlier(5, 9);

        depositV4B = new PitchV4BSubsystem(hardwareMap, telemetry);

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
        if (!depositLimitSwitch.getState()) {
            // Block further extension when limit switch is pressed
            verticalSlideMotor.setPower(0.1);
            verticalSlideMotor2.setPower(0.1);
        } else {
            verticalSlideMotor.setPower(clampPower(power * -0.6));
            verticalSlideMotor2.setPower(clampPower(power * -0.6));
        }
    }

    // auto get slides up
    public void extendDepositMainSlide() {
        if (CURRENT_STATE == Deposit_state.EXTENDING ||
                CURRENT_STATE == Deposit_state.EXTENDED ||
                verticalDistance >= MAX_HEIGHT) {
            return;
        }
        verticalSlideMotor.setPower(0.8);
        verticalSlideMotor2.setPower(0.8);
        CURRENT_STATE = Deposit_state.EXTENDING;
    }

    // auto retract slides
    public void retractDepositMainSlide() {
        if (CURRENT_STATE == Deposit_state.RETRACTING ||
                CURRENT_STATE == Deposit_state.RETRACTED ||
                verticalDistance <= RETRACT_HEIGHT ) {
            return;
        }

        verticalSlideMotor.setPower(-0.4);
        verticalSlideMotor2.setPower(-0.4);
        CURRENT_STATE = Deposit_state.RETRACTING;
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    public void update() {
        verticalDistance = movingAverage.add(rangeSensor.getDistance(DistanceUnit.INCH));

        switch (CURRENT_STATE) {
            case EXTENDING:
                if (verticalDistance < MAX_HEIGHT) {
                    telemetry.addData("current value: ", verticalDistance);
                    telemetry.update();
                } else {
                    verticalSlideMotor.setPower(0.2);
                    verticalSlideMotor2.setPower(0.2);
                    CURRENT_STATE = Deposit_state.EXTENDED;
                }
                break;
            case RETRACTING:
                if (verticalDistance <= 0) {
                    telemetry.addData("Warning", "Invalid distance detected: " + verticalDistance);
                    telemetry.update();
                    return; // Skip further processing
                }
                if (verticalDistance > RETRACT_HEIGHT) {
                    telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
                    telemetry.update();
                } else {
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
