package org.firstinspires.ftc.teamcode.subsystems.intake;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.dashboard.EnhancedDashboard;
import org.firstinspires.ftc.teamcode.dashboard.SubsystemTelemetryBuilder;
import org.firstinspires.ftc.teamcode.dashboard.messages.*;

public class IntakeSlideSubsystem {

    public final DcMotor slideMotor;
    public final DistanceSensor sensorDistance;
    private final DigitalChannel intakeLimitSwitch;
    public final Telemetry telemetry;

    private final double MAX_POS = 36;
    private final int MIN_POS = 23;
    private final int EXCHANGE_POS = 20; // TODO: find exchange value
    
    private EnhancedDashboard enhancedDashboard = EnhancedDashboard.getInstance();

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
    
    public void sendEnhancedTelemetry() {
        // Create intake data
        IntakeData intakeData = new IntakeData();
        intakeData.setSlidePosition(sensorDistance.getDistance(DistanceUnit.CM));
        intakeData.setTargetPosition(MIN_POS); // You may want to track target position
        intakeData.setState(CURRENT_STATE.name());
        intakeData.setMotorPower(slideMotor.getPower());
        intakeData.setLimitSwitch(!intakeLimitSwitch.getState());
        intakeData.setSampleDetected(false); // Set based on your detection logic
        
        // Send subsystem update
        enhancedDashboard.sendSubsystemUpdate(
            new SubsystemUpdateMessage("intake", intakeData)
        );
        
        // Send telemetry values
        TelemetryUpdateMessage telemetryMessage = new SubsystemTelemetryBuilder("intake")
            .addValue("slide_position", sensorDistance.getDistance(DistanceUnit.CM), "cm")
            .addValue("motor_current", ((DcMotorEx) slideMotor).getCurrent(CurrentUnit.AMPS), "A")
            .addValue("state", CURRENT_STATE.name())
            .addValue("limit_switch", !intakeLimitSwitch.getState())
            .build();
        
        enhancedDashboard.sendTelemetryUpdate(telemetryMessage);
    }
}