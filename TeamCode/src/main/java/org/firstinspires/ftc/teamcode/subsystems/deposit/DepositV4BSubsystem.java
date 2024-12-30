package org.firstinspires.ftc.teamcode.subsystems.deposit;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class DepositV4BSubsystem implements Subsystem {

    public final Servo wristServo1;
    public final Servo wristServo2;

    private static final double SPECIMEN_DROP = 0;
    private static final double DROP = 1.0;
    private static final double PICKUP = 0.29;

    public DepositV4BSubsystem(HardwareMap hardwareMap) {
        wristServo1 = hardwareMap.get(Servo.class, "dwsrv1");
        wristServo2 = hardwareMap.get(Servo.class, "dwsrv2");

        wristServo2.setDirection(Servo.Direction.REVERSE);
    }

    public void setWristDropPosition() {
        setWristPosition(DROP, DROP);
    }

    public void setWristPickPosition() {
        setWristPosition(PICKUP, PICKUP);
    }

    public void setWristSpecimenDropPosition() {
        setWristPosition(SPECIMEN_DROP, SPECIMEN_DROP);
    }

    private void setWristPosition(double pos1, double pos2) {
        wristServo1.setPosition(pos1);
        wristServo2.setPosition(pos2);
    }

    @Override
    public void update() {
        // No-op: Implement update logic if necessary.
    }
}
