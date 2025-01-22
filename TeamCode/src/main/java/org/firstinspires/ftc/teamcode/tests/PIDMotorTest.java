package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "PIDMotorTest", group = "Test")
public class PIDMotorTest extends LinearOpMode {
    public void RunOpMode() {}

    private DcMotorEx pidMotor;
    private DcMotorEx pidMotor2;


    // pid values
    private final double Kp = 0.1;
    private final double Kd = 0.1;

    // variables
    private double targetPosition = 100;
    private double lastError = 0;
    private double lastTime = 0;

    @Override
    public void runOpMode() {
        pidMotor2 = hardwareMap.get(DcMotorEx.class, "vsmot");
        pidMotor = hardwareMap.get(DcMotorEx.class, "vsmot2");


        pidMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pidMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pidMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pidMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pidMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pidMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pidMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pidMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        pidMotor2.setTargetPosition(100);
        pidMotor.setTargetPosition(100);


        waitForStart();
        lastTime = System.currentTimeMillis();

        while (opModeIsActive()) {
            if (gamepad2.a) {
                targetPosition = 100;
            } else if (gamepad2.b) {
                targetPosition = 0;
            }
            double power = calculatePID(targetPosition, pidMotor.getCurrentPosition());

            pidMotor.setPower(0.1);
            pidMotor2.setPower(0.1);

            //telemetry
            telemetry.addData("Target Position", targetPosition);
            telemetry.addData("Current Position", pidMotor.getCurrentPosition());
            telemetry.addData("Power", power);
            telemetry.update();
        }
    }

    private double calculatePID(double target, double current) {
        double currentTime = getRuntime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        double error = target - current;

        double proportional = Kp * error;


        double derivative = Kd * (error - lastError) / (deltaTime + 1e-6);
        lastError = error;

        return proportional + derivative;
    }
}