package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;

@TeleOp(name = "Laser Test", group = "Linear OpMode")
public class REV2mDistanceTest extends LinearOpMode {
    private IntakeSlideSubsystem slides;
    private DistanceSensor sensorDistance;

    @Override
    public void runOpMode() {
        slides = new IntakeSlideSubsystem(hardwareMap, telemetry);
        sensorDistance = hardwareMap.get(DistanceSensor.class, "hlas");
        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor) sensorDistance;

        waitForStart();

        while(opModeIsActive()) {
            if (gamepad1.a) {
                slides.extendMainSlide();
            }

            if (gamepad1.b) {
                slides.retractMainSlide();
            }

            slides.update();

            telemetry.addData("range", String.format("%.01f cm", sensorDistance.getDistance(DistanceUnit.CM)));

            // Rev2mDistanceSensor specific methods.
            telemetry.addData("ID", String.format("%x", sensorTimeOfFlight.getModelID()));
            telemetry.addData("did time out", Boolean.toString(sensorTimeOfFlight.didTimeoutOccur()));

            telemetry.update();
        }
    }
}
