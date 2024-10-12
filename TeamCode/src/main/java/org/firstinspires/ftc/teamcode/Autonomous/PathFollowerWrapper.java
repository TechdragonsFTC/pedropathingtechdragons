package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


import org.firstinspires.ftc.teamcode.PID;

import java.util.Arrays;

import org.firstinspires.ftc.teamcode.Wheelie.Path;
import org.firstinspires.ftc.teamcode.Wheelie.PathFollower;
import org.firstinspires.ftc.teamcode.Wheelie.Pose2D;

public class PathFollowerWrapper {
    private Localization localization;

    private PathFollower follower;
    private double lookAhead;
    private PID xPID, yPID, hPID;
    private ElapsedTime PIDtimer;

    private static final double mP = 1./50., mI = 0, mD = 0,
                                hP = 1./Math.PI, hI = 0, hD = 0,
                                mMaxI = .25, hMaxI = .1;

    //The max speed of the motors
    public static double SPEED_PERCENT = 1;

    public PathFollowerWrapper(HardwareMap hw, Pose2D startPose, double look)
    {
        localization = new Localization(hw, startPose);
        lookAhead = look;

        xPID = new PID(mP,mI,mD);
        yPID = new PID(mP,mI,mD);
        hPID = new PID(hP,hI,hD);
        xPID.capI(mMaxI);
        yPID.capI(mMaxI);
        hPID.capI(hMaxI);
        PIDtimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    public PathFollowerWrapper(HardwareMap hw, Pose2D startPose, double look, double maxSpeed)
    {
        localization = new Localization(hw, startPose);
        lookAhead=look;

        SPEED_PERCENT = maxSpeed;

        xPID = new PID(mP,mI,mD);
        yPID = new PID(mP,mI,mD);
        hPID = new PID(hP,hI,hD);
    }

    /**
     * Sets the path for the robot to follow
     * @param startPose the robot's starting Pose
     * @param path the Path object the robot will follow
     */
    public void setPath(Pose2D startPose, Path path){
        follower = new PathFollower(startPose, lookAhead, path);
        PIDtimer.reset();
    }
    public PathFollower getFollower(){
        return follower;
    }

    /** Finds the movement vector by transforming the followPath vectors
     * @param forward the x component of the vector outputted by PathFollower.followPath
     * @param strafe the y component of the vector outputted by PathFollower.followPath
     * @param heading the h component of the vector outputted by PathFollower.followPath
     */
    public double[] moveTo(double forward, double strafe, double heading){
        double x = forward * Math.cos(getPose().h) - strafe * Math.sin(getPose().h);
        double y = forward * Math.sin(getPose().h) + strafe * Math.cos(getPose().h);
        double h = heading;

        double movementAngle = Math.atan2(y,x) - getPose().h,
                distance = Math.hypot(x,y);
        x = distance * Math.cos(movementAngle);
        y = distance * Math.sin(movementAngle);

        x *= mP;
        y *= mP;
        h *= hP;

        return new double[]{
                x, y, h
        };
    }

    /** Finds the movement vector using PID
     * @param move output from PathFollwer class, followPath method
     * @param time time elasped since path was started
     */
    public double[] moveToPID(Pose2D move, double time){
        double forward = move.x,
                strafe = move.y,
                heading = move.h;

        double x = Math.cos(getPose().h) * forward + Math.sin(getPose().h) * strafe;
        double y = Math.cos(getPose().h) * strafe - Math.sin(getPose().h) * forward;
        double h = hPID.pidCalc(heading, 0, time);

        x = xPID.pidCalc(x, 0, time);
        y = yPID.pidCalc(y, 0, time);

        return new double[]{
                x, y, h
        };
    }

    public void resetPID(){
        xPID.resetI();
        yPID.resetI();
        hPID.resetI();
    }

    public boolean targetReached(Pose2D target){
        return Math.hypot(target.x-getPose().x, target.y-getPose().y) <= 2;
    }
    public void concludePath(){
        follower = null;
    }

    public Pose2D getPose(){
        return localization.currentPosition;
    }
    public String getPoseString(){
        return localization.currentPosition.x + ", " +
                localization.currentPosition.y + ", " +
                localization.currentPosition.h;
    }

    public int getHoriOdom(){
        return localization.getHori();
    }
    public int getVertOdom(){
        return localization.getVert();
    }

    Pose2D m;
    public Pose2D followerValues(){
        return m;
    }

    public int getCurrentWayPoint(){
        return follower.getWayPoint();
    }

    /**
     * Finds the movement vector for the robot to move along the path until its conclusion
     * @return the movement vector
     */
    public double[] followPath(){
        if(follower != null) {
            if(targetReached(follower.getLastPoint())){
                concludePath();
                return new double[] {0,0,0};
            }
            m = follower.followPath(getPose());
            return moveTo(m.x, m.y, 0);
        }

        return new double[] {0,0,0};
    }

    /**
     * Finds the movement vector for the robot to move along the path until its conclusion (using PID)
     * @return the movement vector
     */
    public double[] followPathPID(){

        if(follower != null) {
            if(targetReached(follower.getLastPoint())){
                concludePath();
                return new double[] {0,0,0};
            }

            m = follower.followPath(getPose());
            return moveToPID(m, PIDtimer.time());
        }

        return new double[] {0,0,0};
    }

    /**
     * Updates the localization of the robot
     * @param vert the current tick position of the vertical wheel
     * @param hori the current tick position of the horizontal wheel
     * @param angle the current angle of the robot (radians)
     */
    public void updatePose(int vert, int hori, double angle){
        localization.update(vert, hori, angle);
    }

    @Override
    public String toString() {
        return "MecDrivebase{" +
                ", localization=" + localization +
                ", mxPID=" + xPID +
                ", myPID=" + yPID +
                ", hPID=" + hPID +
                ", follower=" + follower +
                ", m=" + m +
                '}';
    }
}
