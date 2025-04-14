package org.firstinspires.ftc.teamcode;

import android.app.Notification;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@Autonomous
public class atuadorTest extends LinearOpMode {

    Servo servo;

    public void runOpMode(){

        servo = hardwareMap.get(Servo.class, "garra");

        servo.setDirection(Servo.Direction.REVERSE);

        servo.setPosition(1.0);
    }
}
