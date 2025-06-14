package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class StateMachine extends LinearOpMode {
    private AutoState currentState;

    public final DcMotor slideMotor;
    public final DistanceSensor sensorDistance;
    private final DigitalChannel intakeLimitSwitch;
    public final Telemetry telemetry;

    public final double MAX_POS = 36;
    public final int MIN_POS = 23;

    public StateMachine(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        slideMotor = hardwareMap.get(DcMotor.class, "hsmot");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "hlas");
        intakeLimitSwitch = hardwareMap.get(DigitalChannel.class, "inltsw");
    }

    public void setState (AutoState state, LinearOpMode opmode) {

    }

    @Override
    public void runOpMode() throws InterruptedException {

    }
}
