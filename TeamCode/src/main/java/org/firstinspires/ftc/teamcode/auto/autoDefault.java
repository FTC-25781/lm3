package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.*;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Autonomous(name = "Example Auto Blue", group = "Examples")
public class autoDefault extends OpMode {
    private ExecutorService executorService;
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    public DepositClawSubsystem depositClaw;
    public DepositSlideSubsystem depositSlide;
    public DepositV4BSubsystem depositV4B;
    public IntakeClawSubsystem intakeClaw;
    public IntakeSlideSubsystem intakeSlide;
    public IntakeV4BSubsystem intakeV4B;


    /**
     * This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method.
     */
    private int pathState=0;

    /**
     * Create and Define Poses + Paths
     */
    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
    private final Pose scorePose = new Pose(14, 129, Math.toRadians(315));
    private final Pose pickup1Pose = new Pose(37, 121, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(43, 130, Math.toRadians(0));
    private final Pose Score2ControlPose = new Pose(37, 121, Math.toRadians(0));
    private final Pose pickup3Pose = new Pose(45.68, 142.11, Math.toRadians(90));
    private final Pose pickup3ControlPose = new Pose(38.9, 80.3, Math.toRadians(0));
    private final Pose parkPose = new Pose(60, 98, Math.toRadians(90));
    private final Pose parkControlPose = new Pose(60, 98, Math.toRadians(90));
    private Path park;
    private PathChain scorePreload, grabPickup1, grabPickup2, grabPickup3, scorePickup1, scorePickup2, scorePickup3;

    /**
     * Build the paths for the auto
     */
    public void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .addParametricCallback(1, depositSlide.extendDepositMainSlide())
                .build();

        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

        scorePickup1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup1Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup2Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
                .build();

        scorePickup2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickup2Pose), new Point(Score2ControlPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();

        grabPickup3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose), new Point(pickup3ControlPose), new Point(pickup3Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3Pose.getHeading())
                .build();

        scorePickup3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup3Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
    }

    /**
     * Path update logic
     */
    private long stateStartTime = System.currentTimeMillis(); // Track the time when the state starts
    private static final long DELAY_MILLIS = 1000; // Delay duration in milliseconds (1 second)

    public void autonomousPathUpdate() {
        long currentTime = System.currentTimeMillis(); // Get the current time

        switch (pathState) {
            case 0: // Score the preload
                if (isStateReady(currentTime)) {
                    follower.followPath(scorePreload);
                    setPathState(1);
                }
                break;
            case 1: // Pick first yellow sample
                if (follower.getPose().getX() > (scorePose.getX() - 1) &&
                        follower.getPose().getY() > (scorePose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(grabPickup1, true);
                    setPathState(2);
                }
                break;
            case 2: // Score first yellow sample
                if ( follower.getPose().getX() > (pickup1Pose.getX() - 1) &&
                        follower.getPose().getY() > (pickup1Pose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(scorePickup1, true);
                    setPathState(3);
                }
                break;
            case 3: // navigate to Pick second yellow sample from score position
                if (follower.getPose().getX() > (scorePose.getX() - 1) &&
                        follower.getPose().getY() > (scorePose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(grabPickup2, true);
                    setPathState(4);
                }
                break;
            case 4: // navigate to Score position from pick second sample
                if (follower.getPose().getX() > (pickup2Pose.getX() - 1) &&
                        follower.getPose().getY() > (pickup2Pose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(scorePickup2, true);
                    setPathState(5);
                }
                break;
            case 5: // navigate to pick third sample position from score position
                if (follower.getPose().getX() > (scorePose.getX() - 1) &&
                        follower.getPose().getY() > (scorePose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(grabPickup3, true);
                    setPathState(6);
                }
                break;
            case 6: // Come back to drop the sample
                if (follower.getPose().getX() > (pickup3Pose.getX() - 1) &&
                        follower.getPose().getY() > (pickup3Pose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(scorePickup3, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (follower.getPose().getX() > (scorePose.getX() - 1) &&
                        follower.getPose().getY() > (scorePose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    follower.followPath(park, true);
                    setPathState(8);
                }
                break;
            case 8:
                if (follower.getPose().getX() > (parkPose.getX() - 1) &&
                        follower.getPose().getY() > (parkPose.getY() - 1) &&
                        isStateReady(currentTime)) {
                    setPathState(-1); // End state
                }
                break;
        }
    }

    /**
     * Checks if the state is ready to transition after the delay.
     */
    private boolean isStateReady(long currentTime) {
        return (currentTime - stateStartTime) > DELAY_MILLIS;
    }

    /**
     * Change the state of the paths
     */
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
        stateStartTime = System.currentTimeMillis();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
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

        // Initialize ExecutorService
        executorService = Executors.newFixedThreadPool(3); // 3 threads for subsystem tasks

        buildPaths();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void stop() {
    }
}
