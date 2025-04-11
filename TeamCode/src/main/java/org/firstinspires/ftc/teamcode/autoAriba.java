package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.constants.FConstants;
import org.firstinspires.ftc.teamcode.constants.LConstants;

@Autonomous(name = "Ariba", group = "Examples")
public class autoAriba extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
    // x = lados
    // y = frente e tras

    private final Pose startPose = new Pose(0, 71, Math.toRadians(0));

    private final Pose ClipPose = new Pose(10, 71, Math.toRadians(0)); //clipa

    private final Pose move1 = new Pose(10, 30, Math.toRadians(0));

    private final Pose move2 = new Pose(50, 30, Math.toRadians(0));

    private final Pose move3 = new Pose(50, 15, Math.toRadians(0));// frente do primeiro sample

    private final Pose move4 = new Pose(5, 15, Math.toRadians(0)); //empurra o sample

    private final Pose move5 = new Pose(50, 15, Math.toRadians(0));
    private final Pose move6 = new Pose(50,-10, Math.toRadians(0));// frente do segundo sample
    private final Pose move7 = new Pose(5, -10, Math.toRadians(0));

    private Path scorePreload, park, clipa;
    private PathChain clipada, grabPickup2, grabPickup3, aposClip, scorePickup2, scorePickup3;
    private PathChain traj1;
    public void buildPaths() {
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(ClipPose)));
        scorePreload.setTangentHeadingInterpolation();

        clipa = new Path(new BezierLine(new Point(ClipPose), new Point(move1)));
        clipa.setConstantHeadingInterpolation(Math.toRadians(0));

        traj1 = follower.pathBuilder()
                //vai para frente
                .addPath(new BezierLine(new Point(startPose), new Point(ClipPose)))
                .setTangentHeadingInterpolation()
                //vai para o lado
                .addPath(new BezierLine(new Point(ClipPose), new Point(move1)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                //vai para frente próximo do sample
                .addPath(new BezierLine(new Point(move1), new Point(move2)))
                .setTangentHeadingInterpolation()
                //vai para o lado na frente do sample
                .addPath(new BezierLine(new Point(move2), new Point(move3)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                //vai para tras empurrando o sample para o jogador
                .addPath(new BezierLine(new Point(move3), new Point(move4)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                //vai para frente para perto do próximo sample
                .addPath(new BezierLine(new Point(move4), new Point(move5)))
                .setTangentHeadingInterpolation()
                .addPath(new BezierLine(new Point(move5), new Point(move6)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(new BezierLine(new Point(move6), new Point(move7)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();



        clipada = follower.pathBuilder()
                .addPath(new BezierLine(new Point(ClipPose), new Point(move1)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();

        aposClip = follower.pathBuilder()
                .addPath(new BezierLine(new Point(move1), new Point(move2)))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();

        //grabPickup2 = follower.pathBuilder()
                //.addPath(new BezierLine(new Point(Clip), new Point(move2)))
                //.setLinearHeadingInterpolation(Clip.getHeading(), move2.getHeading())
                //.build();

        //scorePickup2 = follower.pathBuilder()
                //.addPath(new BezierLine(new Point(move2), new Point(Clip)))
                //.setLinearHeadingInterpolation(move2.getHeading(), Clip.getHeading())
                //.build();

        //grabPickup3 = follower.pathBuilder()
                //.addPath(new BezierLine(new Point(Clip), new Point(pickup3Pose)))
                //.setLinearHeadingInterpolation(Clip.getHeading(), pickup3Pose.getHeading())
                //.build();

        //scorePickup3 = follower.pathBuilder()
                //.addPath(new BezierLine(new Point(pickup3Pose), new Point(Clip)))
                //.setLinearHeadingInterpolation(pickup3Pose.getHeading(), Clip.getHeading())
                //.build();

        //park = new Path(new BezierCurve(new Point(Clip), /* Control Point */ new Point(parkControlPose), new Point(parkPose)));
        //park.setLinearHeadingInterpolation(Clip.getHeading(), parkPose.getHeading());
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(traj1, true);
                setPathState(3);
                break;
            case 1:

                if(!follower.isBusy()) {

                    follower.followPath(clipa);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {

                    follower.followPath(aposClip, true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {

                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
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

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void stop() {
    }
}
