package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierCurve;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Path;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.PathChain;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point;
import org.firstinspires.ftc.teamcode.pedroPathing.util.Timer;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.deposit.DepositV4BSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeClawSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeV4BSubsystem;

import java.util.concurrent.TimeUnit;

@Autonomous(name = "Example Auto Blue", group = "Examples")
public class autoDefault extends OpMode {
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    public DepositClawSubsystem depositClaw;
    public DepositSlideSubsystem depositSlide;
    public DepositV4BSubsystem depositV4B;
    public IntakeClawSubsystem intakeClaw;
    public IntakeSlideSubsystem intakeSlide;
    public IntakeV4BSubsystem intakeV4B;



    private int pathState = 0;  // This is the variable where we store the state of our auto.

    private final int RESOLUTION = 2; // Error of 2 inches

    // Create and Define Poses + Paths
    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
    private final Pose scorePose = new Pose(10, 132, Math.toRadians(315));
    private final Pose scoreSlidesPose = new Pose(15, 128, Math.toRadians(315));
    private final Pose pickup1Pose = new Pose(23, 121, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(24, 129, Math.toRadians(0));
    private final Pose parkPose = new Pose(60, 98, Math.toRadians(90));
    private final Pose parkControlPose = new Pose(60, 98, Math.toRadians(90));
    private Path park;
    private PathChain slidesUp, scorePreload, grabPickup1, slidesUpPick1, grabPickup2, slidesUpPick2, scorePickup1, scorePickup2, scorePickup3;
    ElapsedTime timers = new ElapsedTime();

    // Build the paths for the auto
    public void buildPaths() {
        slidesUp = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scoreSlidesPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scoreSlidesPose.getHeading())
                .build();

        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreSlidesPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(scoreSlidesPose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

        slidesUpPick1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup1Pose), new Point(scoreSlidesPose)))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scoreSlidesPose.getHeading())
                .build();

        scorePickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreSlidesPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(scoreSlidesPose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup2Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
                .build();

        slidesUpPick2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup2Pose), new Point(scoreSlidesPose)))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scoreSlidesPose.getHeading())
                .build();

        scorePickup2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scoreSlidesPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(scoreSlidesPose.getHeading(), scorePose.getHeading())
                .build();

        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
    }


    // Path update logic
    private long stateStartTime = System.currentTimeMillis(); // Track the time when the state starts
    private static final long DELAY_MILLIS = 1000; // Delay duration in milliseconds (1 second)

    private boolean isWithinResolution(Pose currentPose, Pose targetPose) {
        return Math.abs(currentPose.getX() - targetPose.getX()) <= RESOLUTION &&
                Math.abs(currentPose.getY() - targetPose.getY()) <= RESOLUTION;
    }

    public void autonomousPathUpdate() {
        long currentTime = System.currentTimeMillis(); // Get the current time

        switch (pathState) {
            case 0:
                if (isStateReady(currentTime)) {
                    follower.followPath(slidesUp, true);
                    setPathState(1);
                }
                break;

            case 1: // Score the preload (5 sec)
                if (isWithinResolution(follower.getPose(), scoreSlidesPose) && isStateReady(currentTime)) {
                    long stateStartTimeSlides = System.currentTimeMillis();
                    depositClaw.closeDepositClaw();
                    depositSlide.extendDepositMainSlide();
                    if (System.currentTimeMillis() - stateStartTimeSlides > 1000) {
                        depositV4B.setWristSpecimenDropPosition();
                    }

                    if (depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.EXTENDED) {
                        depositV4B.setWristDropPosition();
                        follower.followPath(scorePreload, true);
                        setPathState(2);
                        timers.reset();
                        timers.startTime();
                    }
                }
                break;

            case 2:
                if (isWithinResolution(follower.getPose(), scorePose) &&
                        isStateReady(currentTime) && timers.time(TimeUnit.MILLISECONDS) > 1000) {
                    depositClaw.openDepositClaw();
                    if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.OPENED) {
                        follower.followPath(grabPickup1, true);
                        setPathState(3);
                    }
                }
                break;

            case 3:
                if (isWithinResolution(follower.getPose(), pickup1Pose) &&
                        isStateReady(currentTime)) {
                    long stateStartTimeRetract = System.currentTimeMillis();
                    if (System.currentTimeMillis() - stateStartTimeRetract > 2000) {
                        intakeClaw.orientationServo.setPosition(0);
                        depositSlide.retractDepositMainSlide();
                        depositV4B.setWristPickPosition();
                        intakeV4B.setWristDefaultPosition();
                        intakeSlide.extendMainSlide();
                        if (intakeSlide.CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED) {
                            intakeV4B.setWristPickAutoPosition();
                            follower.followPath(slidesUpPick1, true);
                            setPathState(4);
                        }
                    }
                }
                break;

            case 4: // Score first yellow sample
                if (isWithinResolution(follower.getPose(), scoreSlidesPose) &&
                        isStateReady(currentTime)) {
                    follower.followPath(scorePickup1, true);
                    setPathState(5);
                }
                break;

            case 5: // Navigate to pick second yellow sample
                if (isWithinResolution(follower.getPose(), scorePose) && isStateReady(currentTime)) {
                    follower.followPath(grabPickup2, true);
                    setPathState(6);
                }
                break;

            case 6:
                if (isWithinResolution(follower.getPose(), pickup2Pose) && isStateReady(currentTime)) {
                    follower.followPath(slidesUpPick2, true);
                    setPathState(7);
                }
                break;

            case 7: // Navigate to score position from second sample
                if (isWithinResolution(follower.getPose(), scoreSlidesPose) && isStateReady(currentTime)) {
                    follower.followPath(scorePickup2, true);
                    setPathState(8);
                }
                break;

            case 8:
                if (isWithinResolution(follower.getPose(), scorePose) && isStateReady(currentTime)) {
                    follower.followPath(park, true);
                    setPathState(9);
                }
                break;

            case 9:
                if (isWithinResolution(follower.getPose(), parkPose) && isStateReady(currentTime)) {
                    setPathState(-1); // End state
                }
                break;
        }
    }

    // Checks if the state is ready to transition after the delay.
    private boolean isStateReady(long currentTime) {
        return (currentTime - stateStartTime) > DELAY_MILLIS;
    }

    // Change the state of the paths
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
        stateStartTime = System.currentTimeMillis();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();
        depositSlide.update();
        depositV4B.update();
        depositClaw.update();
        intakeSlide.update();
        intakeV4B.update();
        intakeClaw.update();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("Motor 1 Power Consumption: ", depositSlide.verticalSlideMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Motor 2 Power Consumption: ", depositSlide.verticalSlideMotor2.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Current sensor value: ", depositSlide.rangeSensor.getDistance(DistanceUnit.INCH));
        telemetry.update();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        // Initialize subsystems
        depositClaw = new DepositClawSubsystem(hardwareMap);
        depositSlide = new DepositSlideSubsystem(hardwareMap, telemetry);
        depositV4B = new DepositV4BSubsystem(hardwareMap);
        intakeClaw = new IntakeClawSubsystem(hardwareMap);
        intakeSlide = new IntakeSlideSubsystem(hardwareMap, telemetry);
        intakeV4B = new IntakeV4BSubsystem(hardwareMap);

        buildPaths();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void stop() {
    }
}
