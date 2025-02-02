package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.PathChain;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point;
import org.firstinspires.ftc.teamcode.pedroPathing.util.Timer;

@Autonomous(name = "Inactive Auto Blue", group = "Examples")
public class inactiveAutoDefault extends OpMode {
    private Follower follower;
    private Timer pathTimer, opmodeTimer;

    private int pathState = 0;  // This is the variable where we store the state of our auto.

    private final int RESOLUTION = 2; // Error of 2 inches

    // Create and Define Poses + Paths
    private final Pose startPose = new Pose(9.7, 62.6, Math.toRadians(0));
    private final Pose parkPose = new Pose(8, 8, Math.toRadians(0));

    private PathChain park;
    public void buildPaths() {
        park = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(parkPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), parkPose.getHeading())
                .build();
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
            case 0: // goto slide up position
                if (isStateReady(currentTime)) {
                    follower.followPath(park, true);
                    setPathState(-1);
                }
                break;
        }
    }

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
        follower.update(false);
        autonomousPathUpdate();
    }

    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        buildPaths();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void stop() {
    }
}
