package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "PIDMotorTest", group = "Test")
public class PIDMotorTest extends LinearOpMode {
    public void RunOpMode() {}

    private DcMotorEx pidMotor;

    // pid values
    private double Kp = 0; //put values here when sarcs slide bot is made
    private double Kd = 0; //put values here when sarcs slide bot is made

    // variables
    private double setPoint = 0; //put values here when sarcs slide bot is made
    private double lastError = 0; //put values here when sarcs slide bot is made
    private long lastTime = 0; //put values here when sarcs slide bot is made

    @Override
    public void runOpMode() {
        pidMotor = hardwareMap.get(DcMotorEx.class, "pidMotor");

        pidMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pidMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        lastTime = System.currentTimeMillis();

        while (opModeIsActive()) {
            double power = determinePID(setPoint, pidMotor.getCurrentPosition());

            pidMotor.setPower(power);

            //telemetry
            telemetry.addData("Target Position", setPoint);
            telemetry.addData("Current Position", pidMotor.getCurrentPosition());
            telemetry.addData("Power", power);
            telemetry.update();
        }
    }

    private double determinePID(double target, double current) {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastTime) / 1000.0;  // Convert to seconds
//      lastTime = currentTime;  (ASK COACH)

        double error = target - current;


        double proportional = Kp * error;


        double derivative = Kd * (error - lastError) / deltaTime;
        lastError = error;

        return proportional + derivative;
    }
}