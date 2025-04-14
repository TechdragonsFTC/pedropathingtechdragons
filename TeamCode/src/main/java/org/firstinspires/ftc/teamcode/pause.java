package org.firstinspires.ftc.teamcode;

import com.pedropathing.util.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.PIDFController;



public class pause {

    PIDFController controller; ///Importando classe do PIDF


    boolean holdingPosition = false;
    boolean holdingPosition2 = false;
    boolean holdingPosition3 = false;
    int MAXLIMITL = 700, MAXLIMITR = 700;

    DcMotorEx leftArm, rightArm;

    PIDFController controller;

    @Override
    public void init(){

    }

    public void move_Base(){
        int currentPosition2 = leftArm.getCurrentPosition();
        int currentPosition3 = rightArm.getCurrentPosition();
        double minPower = 0.2;
        double maxPower = 0.2;
        controller = new PIDFController(10, 3, 4, 12);
        controller.setInputRange(-4000, 4000);
        controller.setSetPoint(encoderPoint);
        controller.setOutputRange(minPower, maxPower);

        double input = -gamepad2.right_stick_y;

        double powerM = maxPower + controller.getComputedOutput(leftArm.getCurrentPosition());
        double powerM1 = maxPower + controller.getComputedOutput(rightArm.getCurrentPosition());


        if(currentPosition2 < MAXLIMITL && currentPosition3 < MAXLIMITR) {
            if (input > 0) {
                leftArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                leftArm.setPower(powerM);
                rightArm.setPower(powerM1);

                holdingPosition2 = false;
            } else if (input < 0) {
                leftArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                leftArm.setPower(-powerM);
                rightArm.setPower(-powerM1);

                holdingPosition2 = false;
            } else if (!holdingPosition2) {

                leftArm.setTargetPosition(currentPosition2);
                leftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftArm.setPower(0);
                leftArm.setPower(powerM*2);

                rightArm.setTargetPosition(currentPosition3);
                rightArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightArm.setPower(0);
                rightArm.setPower(powerM1*2);

                holdingPosition2 = true;

            }
        }else {
            if (input < 0) {
                leftArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                leftArm.setPower(-powerM);
                rightArm.setPower(-powerM1);

                holdingPosition3 = false;
            } else if (!holdingPosition3) {
                leftArm.setTargetPosition(currentPosition2);
                leftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftArm.setPower(0);
                leftArm.setPower(powerM*2);

                rightArm.setTargetPosition(currentPosition3);
                rightArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightArm.setPower(0);
                rightArm.setPower(powerM1*2);

                holdingPosition3 = true;

            }
        }
}
