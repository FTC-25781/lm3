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
    private boolean retractSlides = false;
    boolean isTimeSet = true;
    boolean isTimeSet2 = true;
    long retractTimer = 0;
    long stateStartTimeSlides = 0;
    long stateStartTimeSlides2 = 0;

    private int pathState = 0;  // This is the variable where we store the state of our auto.

    private final int RESOLUTION = 2; // Error of 2 inches

    // Create and Define Poses + Paths
    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
    private final Pose scorePose = new Pose(10, 132, Math.toRadians(315));
    private final Pose scoreSlidesPose = new Pose(16, 126, Math.toRadians(315));
    private final Pose pickup1Pose = new Pose(15.9, 123, Math.toRadians(0));
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

//        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
//        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
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

    private void pickSample() {
        intakeClaw.orientationServo.setPosition(0.0);

        depositSlide.retractDepositMainSlide();

        if (depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
                depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITIONING) {
            depositV4B.setWristPickPosition();
        }

        if (intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DEFAULT_POSITION &&
                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DEFAULT_POSITIONING) {
            intakeV4B.setWristDefaultPosition();
        }

        if (intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.EXTENDED &&
                intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.EXTENDING) {
            intakeSlide.extendMainSlide();
        }

        if (intakeSlide.CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED &&
                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.PICK_POSITION &&
                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.PICK_POSITIONING) {
            intakeV4B.setWristPickPosition();
        }


        if (intakeV4B.CURRENT_STATE == IntakeV4BSubsystem.Intakev4b_state.PICK_POSITION &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENING) {
            intakeClaw.openClaw();
        }

        if (intakeClaw.CURRENT_STATE == IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.CLOSED &&
                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.CLOSING) {
            intakeClaw.closeClaw();
        }

        if (intakeClaw.CURRENT_STATE == IntakeClawSubsystem.IntakeClaw_state.CLOSED &&
                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION &&
                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITIONING) {
            intakeV4B.setWristDropPosition();
        }
    }

    private void scoreSample() {
        intakeSlide.retractMainSlide();

        if (depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING) {
            depositClaw.openDepositClaw();
        }

        long stateStartTimeSlides = System.currentTimeMillis();
        if (depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSED &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSING)
        {
            depositClaw.closeDepositClaw();
        }

        if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.CLOSED) {
            depositSlide.extendDepositMainSlide();
        }

        // if time is more that 1 sec since we enter this state raise deposit arm
        if ((System.currentTimeMillis() - stateStartTimeSlides) > 1000) {
            depositV4B.setWristSpecimenDropPosition();
        }

        //when slides are extended bring the deposit arm to drop position
        if (depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.EXTENDED &&
                depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITIONING &&
                depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITION)
        {
            depositV4B.setWristDropPosition();
        }

        // Make sure wrist is in drop before next step
        if (depositV4B.CURRENT_STATE == DepositV4BSubsystem.Depositv4b_state.DROP_POSITION &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING &&
                depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED) {
            depositClaw.openDepositClaw();
        }
    }

    public void autonomousPathUpdate() {
        long currentTime = System.currentTimeMillis(); // Get the current time

        switch (pathState) {
            case 0: // goto slide up position
                if (isStateReady(currentTime)) {
                    follower.followPath(slidesUp, true);
                    setPathState(1);
                }
                break;

            case 1: // Score the preload (5 sec)
                // check if we are at score slide position and is time passed from than 1 sec since last run
                if (isWithinResolution(follower.getPose(), scoreSlidesPose) && isStateReady(currentTime)) {
                    // get current time to compare later for lifting deposit arm after one sec

                    // make sure we already set it
                    if (isTimeSet) {
                        stateStartTimeSlides = System.currentTimeMillis();
                        isTimeSet = false;
                    }
                    // before go up close the deposit claw
                    if (depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSED &&
                        depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.CLOSING)
                    {
                        depositClaw.closeDepositClaw();
                    }

                    // extend deposit main slides
                    if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.CLOSED) {
                        depositSlide.extendDepositMainSlide();
                    }

                    //when slides are extended bring the deposit arm to drop position
                    if (depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.EXTENDED &&
                        depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITIONING &&
                        depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.DROP_POSITION) {
                        depositV4B.setWristDropPosition();
                    }

                    // Make sure wrist is in drop before next step
                    // start driving to score position and move to next case-2
                    if (depositV4B.CURRENT_STATE == DepositV4BSubsystem.Depositv4b_state.DROP_POSITION) {
                        follower.followPath(scorePreload, true);
                        setPathState(2);
                    }
                }
                break;

            case 2: // we are at score position with slide up and deposit arm at drop level
                if (isWithinResolution(follower.getPose(), scorePose) &&
                        isStateReady(currentTime)) {
                    if (isTimeSet2) {
                        stateStartTimeSlides2 = System.currentTimeMillis();
                        isTimeSet2 = false;
                    }

                    // open the drop claw
                    if ((System.currentTimeMillis() - stateStartTimeSlides2) > 1000 &&
                        depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENED &&
                        depositClaw.CURRENT_STATE != DepositClawSubsystem.DepositClaw_state.OPENING) {
                        depositClaw.openDepositClaw();
                    }

                    // once claw is open move to grab pick position
                    if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.OPENED) {
                        // goto grab pick position
                        follower.followPath(grabPickup1, true);
                        setPathState(3);
                    }
                }
                break;

            case 3: // we at grab first pick position
                if (isWithinResolution(follower.getPose(), pickup1Pose) && isStateReady(currentTime)) {
                    // pick orientation
                    intakeClaw.orientationServo.setPosition(0.0);

                    if (!retractSlides) {
                        retractSlides = true;
                        isTimeSet = false;
                    }

                    if (retractSlides && !isTimeSet) {
                        retractTimer = System.currentTimeMillis();;
                        isTimeSet = true;
                    }

                    // move deposit arm to pick position
                    if (depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
                        depositV4B.CURRENT_STATE != DepositV4BSubsystem.Depositv4b_state.PICK_POSITIONING) {
                        depositV4B.setWristPickPosition();
                    }

                    if (depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.RETRACTED &&
                        depositV4B.CURRENT_STATE == DepositV4BSubsystem.Depositv4b_state.PICK_POSITION &&
                        (System.currentTimeMillis() - retractTimer) > 1000) {
                        depositSlide.retractDepositMainSlide();
                    }

                    if (depositSlide.CURRENT_STATE == DepositSlideSubsystem.Deposit_state.RETRACTED &&
                        depositSlide.CURRENT_STATE != DepositSlideSubsystem.Deposit_state.RETRACTING) {

                        if (intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.EXTENDED &&
                                intakeSlide.CURRENT_STATE != IntakeSlideSubsystem.Intake_state.EXTENDING) {
                            intakeSlide.extendMainSlide();
                        }

                        if ( intakeSlide.CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDING &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DEFAULT_POSITION &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DEFAULT_POSITIONING) {
                            intakeV4B.setWristDefaultPosition();
                        }

                        if (intakeSlide.CURRENT_STATE == IntakeSlideSubsystem.Intake_state.EXTENDED &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.PICK_POSITION &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.PICK_POSITIONING) {
                            intakeV4B.setWristPickPosition();
                        }


                        if (intakeV4B.CURRENT_STATE == IntakeV4BSubsystem.Intakev4b_state.PICK_POSITION &&
                                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                                intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.OPENING) {
                            intakeClaw.openClaw();
                        }

                        }

                        if (intakeClaw.CURRENT_STATE == IntakeClawSubsystem.IntakeClaw_state.OPENED &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION &&
                                intakeV4B.CURRENT_STATE != IntakeV4BSubsystem.Intakev4b_state.DROP_POSITIONING) {
                            intakeV4B.setWristDropPosition();
                        }
                    if (intakeV4B.CURRENT_STATE == IntakeV4BSubsystem.Intakev4b_state.DEFAULT_POSITION &&
                            intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.CLOSED &&
                            intakeClaw.CURRENT_STATE != IntakeClawSubsystem.IntakeClaw_state.CLOSING) {
                        intakeClaw.closeClaw();
                    }

                    if (intakeV4B.CURRENT_STATE == IntakeV4BSubsystem.Intakev4b_state.DROP_POSITION) {
                        follower.followPath(slidesUpPick1, true);
                        setPathState(-1);
                    }

                }
                break;

            case 4: // Score first yellow sample
                if (isWithinResolution(follower.getPose(), scoreSlidesPose) && isStateReady(currentTime)) {
                    scoreSample();
                    if (depositClaw.CURRENT_STATE == DepositClawSubsystem.DepositClaw_state.OPENED) {
                        follower.followPath(scorePickup1, true);
                        setPathState(5);
                    }
                }
                break;

            case 5:
                if (isWithinResolution(follower.getPose(), scorePose) && isStateReady(currentTime)) {
                    follower.followPath(park, true);
                    setPathState(6);
                }
                break;

            case 6:
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

        // Deposit
        telemetry.addData("deposit claw current state: ",  depositClaw.CURRENT_STATE.name());
        telemetry.addData("deposit ARM current state: ",   depositV4B.CURRENT_STATE.name());
        telemetry.addData("deposit slide current state: ", depositSlide.CURRENT_STATE.name());
        telemetry.addData("Deposit timer: ",               depositV4B.depostTimerDiff);
        telemetry.addData("Vertical height(inch): ",       depositSlide.rangeSensor.getDistance(DistanceUnit.INCH));

        telemetry.addData("Motor 1 Power Consumption: ", depositSlide.verticalSlideMotor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Motor 2 Power Consumption: ", depositSlide.verticalSlideMotor2.getCurrent(CurrentUnit.AMPS));

        // intake
        telemetry.addData("Intake Claw current state: ",   intakeClaw.CURRENT_STATE.name());
        telemetry.addData("Intake ARM current state: ", intakeV4B.CURRENT_STATE.name());
        telemetry.addData("Intake slide current state: ",  intakeSlide.CURRENT_STATE.name());
        telemetry.addData("Horizontal range",              intakeSlide.sensorDistance.getDistance(DistanceUnit.CM));

        //follower
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());;
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
        depositV4B = new DepositV4BSubsystem(hardwareMap, telemetry);
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
