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

    private static final int SLIDE_EXTEND_POS = 800;
    private static final double SLIDE_EXTEND_SPEED = 0.5;

    public IntakeSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        slideMotor = hardwareMap.get(DcMotor.class, "hsmot");
        intakeLimitSwitch = hardwareMap.get(DigitalChannel.class, "inltsw");

        intakeLimitSwitch.setMode(DigitalChannel.Mode.INPUT);

        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void manualExtension(double power) {
        slideMotor.setPower(clampMotorPower(power));
    }

    public void extendMainSlide() {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        slideMotor.setPower(-0.8);

        while (timer.time(TimeUnit.MILLISECONDS) > 1500) {
            telemetry.addData("Distance: ", slideMotor.getCurrentPosition());
            telemetry.update();
        }
        slideMotor.setPower(0);
    }

    public void retractMainSlide() {
        if (!intakeLimitSwitch.getState()) {
            stopAndResetSlide();
        } else {
            slideMotor.setPower(-SLIDE_EXTEND_SPEED);
        }
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
        // Placeholder for periodic updates if needed
    }
}
