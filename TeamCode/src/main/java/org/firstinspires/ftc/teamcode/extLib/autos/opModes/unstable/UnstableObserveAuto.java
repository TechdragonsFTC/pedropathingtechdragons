package org.firstinspires.ftc.teamcode.extLib.autos.opModes.unstable;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.extLib.autos.systems.WheelOp;

import Wheelie.Pose2D;

@Autonomous (group = "UNSTABLE")
public class UnstableObserveAuto extends WheelOp {
    private Pose2D[] path1 = new Pose2D[]{
            new Pose2D(0, 0),
            new Pose2D(19, 4)
    };

    @Override
    public void onStart() {
    }

    @Override
    public void run() {
        followPath(path1);
        sleep(1000);

        while(opModeIsActive()) {
            telemetry.addLine();
        }
    }
}