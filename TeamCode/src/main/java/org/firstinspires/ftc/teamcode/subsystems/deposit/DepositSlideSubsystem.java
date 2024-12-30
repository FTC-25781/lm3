package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositV4BSubsystem;

import java.util.concurrent.TimeUnit;

public class DepositSlideSubsystem {

    private final DigitalChannel depositLimitSwitch;
    public final DcMotor verticalSlideMotor;
    public final DcMotor verticalSlideMotor2;
    public DepositV4BSubsystem depositV4B;

    private static final int SLIDE_EXTEND_POS = 10500;
    private static final int SLIDE_RETRACT_POS = 2000;

    public final Telemetry telemetry;

    public DepositSlideSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        depositV4B = new DepositV4BSubsystem(hardwareMap);

        verticalSlideMotor = hardwareMap.get(DcMotor.class, "vsmot");
        verticalSlideMotor2 = hardwareMap.get(DcMotor.class, "vsmot2");
        depositLimitSwitch = hardwareMap.get(DigitalChannel.class, "dpltsw");


        verticalSlideMotor.setDirection(DcMotor.Direction.REVERSE);
        verticalSlideMotor2.setDirection(DcMotor.Direction.FORWARD);

        verticalSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        verticalSlideMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        depositLimitSwitch.setMode(DigitalChannel.Mode.INPUT);
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

    public void extendDepositMainSlide() {
        verticalSlideMotor.setTargetPosition(SLIDE_EXTEND_POS);
        verticalSlideMotor2.setTargetPosition(SLIDE_EXTEND_POS);
        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        verticalSlideMotor.setPower(1);
        verticalSlideMotor2.setPower(1);

        while (verticalSlideMotor.isBusy() && verticalSlideMotor2.isBusy()) {
            telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
            telemetry.update();
        }

        verticalSlideMotor.setPower(0);
        verticalSlideMotor2.setPower(0);

        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void retractDepositMainSlide() {
        verticalSlideMotor.setTargetPosition(SLIDE_RETRACT_POS);
        verticalSlideMotor2.setTargetPosition(SLIDE_RETRACT_POS);
        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        verticalSlideMotor.setPower(-1);
        verticalSlideMotor2.setPower(-1);

        while (verticalSlideMotor.isBusy() && verticalSlideMotor2.isBusy()) {
            telemetry.addData("current position: ", verticalSlideMotor.getCurrentPosition());
            telemetry.update();
        }

        verticalSlideMotor.setPower(0);
        verticalSlideMotor2.setPower(0);

        verticalSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verticalSlideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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

    }
}
