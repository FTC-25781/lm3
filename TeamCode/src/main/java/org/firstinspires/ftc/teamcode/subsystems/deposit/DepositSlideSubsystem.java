package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.sensors.UltrasonicDistanceSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.dashboard.EnhancedDashboard;
import org.firstinspires.ftc.teamcode.dashboard.SubsystemTelemetryBuilder;
import org.firstinspires.ftc.teamcode.dashboard.messages.*;

import java.util.concurrent.TimeUnit;

public class DepositSlideSubsystem implements Subsystem {

    public DigitalChannel depositLimitSwitch;
    public DcMotorImplEx verticalSlideMotor;
    public DcMotorImplEx verticalSlideMotor2;
    public LynxModule hub;

    public DepositV4BSubsystem depositV4B;
    public DistanceSensor laserSensor;
    public double verticalDistance = 0;

    // Reference values
    private static final int    MAX_HEIGHT              = 90;
    private static final int    RETRACT_HEIGHT          = 52;
    private static final int    RETRACT_HEIGHT_2        = 37;
    private static final double NOMINAL_BATTERY_VOLTAGE = 12.0; // Fully charged battery
    private static final double MAX_MOTOR_CURRENT       = 5.0; // Max safe motor current in Amps

    public enum Deposit_state {
        INITIALISED,
        UNINITIALISED,
        EXTENDING,
        EXTENDED,
        RETRACTING,
        RETRACTED,
        STOPPED,
        LIMIT_SW_HIT,
        LIMIT_SW_NOT_HIT,
        RETRACT_PICKED,
        RETRACT_PICKING
    }

    public Deposit_state CURRENT_STATE = Deposit_state.UNINITIALISED;
    ElapsedTime timer = new ElapsedTime();
    public final Telemetry telemetry;
    private EnhancedDashboard enhancedDashboard = EnhancedDashboard.getInstance();

    public DepositSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        depositV4B = new DepositV4BSubsystem(hardwareMap, telemetry);

        verticalSlideMotor  = hardwareMap.get(DcMotorImplEx.class, "vsmot");
        verticalSlideMotor2 = hardwareMap.get(DcMotorImplEx.class, "vsmot2");
        depositLimitSwitch  = hardwareMap.get(DigitalChannel.class, "dpltsw");
        hub                 = hardwareMap.get(LynxModule.class, "Control Hub");

        laserSensor = hardwareMap.get(DistanceSensor.class, "vlas");

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
            verticalSlideMotor.setPower(adjMotorPower(power * -0.6,
                    hub.getInputVoltage(VoltageUnit.VOLTS),
                    verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
            verticalSlideMotor2.setPower(adjMotorPower(power * -0.6,
                    hub.getInputVoltage(VoltageUnit.VOLTS),
                    verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        }
    }

    // auto get slides up
    public void extendDepositMainSlide() {
        if (CURRENT_STATE == DepositSlideSubsystem.Deposit_state.EXTENDING ||
                CURRENT_STATE == Deposit_state.EXTENDED ||
                verticalDistance >= MAX_HEIGHT) {
            return;
        }
        verticalSlideMotor.setPower(adjMotorPower(0.8,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        verticalSlideMotor2.setPower(adjMotorPower(0.8,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        CURRENT_STATE = Deposit_state.EXTENDING;
        timer.reset();
    }

    // auto retract slides
    public void retractDepositMainSlide() {
        if (CURRENT_STATE == Deposit_state.RETRACTING ||
                CURRENT_STATE == Deposit_state.RETRACTED ||
                verticalDistance <= RETRACT_HEIGHT ) {
            return;
        }

        verticalSlideMotor.setPower(adjMotorPower(-0.6,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        verticalSlideMotor2.setPower(adjMotorPower(-0.6,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        CURRENT_STATE = Deposit_state.RETRACTING;
        timer.reset();
    }

    public void retractPickDepositMainSlide() {
        if (CURRENT_STATE == Deposit_state.RETRACT_PICKING ||
                CURRENT_STATE == Deposit_state.RETRACT_PICKED ||
                verticalDistance <= RETRACT_HEIGHT_2 ) {
            return;
        }
        verticalSlideMotor.setPower(adjMotorPower(-0.6,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
        verticalSlideMotor2.setPower(adjMotorPower(-0.6,
                hub.getInputVoltage(VoltageUnit.VOLTS),
                verticalSlideMotor.getCurrent(CurrentUnit.AMPS)));
//        verticalSlideMotor.setPower(-0.6);
//        verticalSlideMotor2.setPower(-0.6);
        CURRENT_STATE = Deposit_state.RETRACT_PICKING;
        timer.reset();
    }

    private double clampPower(double power) {
        return Math.max(-1.0, Math.min(1.0, power));
    }

    private double adjMotorPower(double basePower, double batteryVoltage, double motorCurrent){
        // Compute power adjustment based on battery voltage
        double voltageCompensation = NOMINAL_BATTERY_VOLTAGE / batteryVoltage;

        // Limit power if current draw is too high (overload protection)
        double currentCompensation = 1.0;
        if (motorCurrent > MAX_MOTOR_CURRENT) {
            currentCompensation = MAX_MOTOR_CURRENT / motorCurrent;
        }

        // Calculate final motor power
        double adjustedPower = basePower * voltageCompensation * currentCompensation;
        return Math.max(-1, Math.min(1.0, adjustedPower)); // Ensure -1.0 <= power <= 1.0
    }

    public void update() {
        verticalDistance = laserSensor.getDistance(DistanceUnit.CM);
        switch (CURRENT_STATE) {
            case EXTENDING:
                if (verticalDistance > MAX_HEIGHT ||
                        timer.time(TimeUnit.MILLISECONDS) > 4000) {
                    verticalSlideMotor.setPower(0.2);
                    verticalSlideMotor2.setPower(0.2);
                    CURRENT_STATE = Deposit_state.EXTENDED;
                    timer.reset();
                }
                break;
            case RETRACTING:
                if (verticalDistance < RETRACT_HEIGHT) {
                    verticalSlideMotor.setPower(0.1);
                    verticalSlideMotor2.setPower(0.1);
                    CURRENT_STATE = Deposit_state.RETRACTED;
                }
                break;
            case RETRACT_PICKING:
                if (verticalDistance < RETRACT_HEIGHT_2) {
                    verticalSlideMotor.setPower(0.1);
                    verticalSlideMotor2.setPower(0.1);
                    CURRENT_STATE = Deposit_state.RETRACT_PICKED;
                }
                break;
            case RETRACT_PICKED:
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
    
    public void sendEnhancedTelemetry() {
        // Create deposit data
        DepositData depositData = new DepositData();
        depositData.setSlidePosition(verticalDistance);
        depositData.setSlidePosition2(verticalSlideMotor2.getCurrentPosition());
        depositData.setTargetPosition(MAX_HEIGHT); // You may want to track actual target
        depositData.setState(CURRENT_STATE.name());
        depositData.setLeftMotorPower(verticalSlideMotor.getPower());
        depositData.setRightMotorPower(verticalSlideMotor2.getPower());
        depositData.setLaserDistance(laserSensor.getDistance(DistanceUnit.CM));
        depositData.setDepositComplete(CURRENT_STATE == Deposit_state.STOPPED || 
                                      CURRENT_STATE == Deposit_state.RETRACTED);
        
        // Send subsystem update
        enhancedDashboard.sendSubsystemUpdate(
            new SubsystemUpdateMessage("deposit", depositData)
        );
        
        // Send telemetry values
        TelemetryUpdateMessage telemetryMessage = new SubsystemTelemetryBuilder("deposit")
            .addValue("slide_position", verticalDistance, "mm")
            .addValue("left_motor_pos", verticalSlideMotor.getCurrentPosition(), "ticks")
            .addValue("right_motor_pos", verticalSlideMotor2.getCurrentPosition(), "ticks")
            .addValue("left_motor_current", verticalSlideMotor.getCurrent(CurrentUnit.AMPS), "A")
            .addValue("right_motor_current", verticalSlideMotor2.getCurrent(CurrentUnit.AMPS), "A")
            .addValue("state", CURRENT_STATE.name())
            .addValue("limit_switch", !depositLimitSwitch.getState())
            .addValue("laser_distance", laserSensor.getDistance(DistanceUnit.CM), "cm")
            .build();
        
        enhancedDashboard.sendTelemetryUpdate(telemetryMessage);
    }
}
