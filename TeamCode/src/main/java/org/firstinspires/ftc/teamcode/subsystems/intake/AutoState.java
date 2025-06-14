package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public enum AutoState {
    INITIALISED {

    },
    UNINITIALISED {

    },
    EXTENDING {
        public void onEnter(StateMachine sm, LinearOpMode opMode) {
            opMode.telemetry.addData("State", "EXTENDING");
            opMode.telemetry.update();
            sm.slideMotor.setPower(-1);
        }

        public void update(StateMachine sm, LinearOpMode opMode) {
            if (sm.sensorDistance.getDistance(DistanceUnit.CM) > sm.MAX_POS) {
                sm.slideMotor.setPower(0);
                sm.setState(EXTENDED, opMode);
            }
        }

    },
    EXTENDED {

    },
    RETRACTING {

    },
    RETRACTED {

    },
    STOPPED {

    },
}
