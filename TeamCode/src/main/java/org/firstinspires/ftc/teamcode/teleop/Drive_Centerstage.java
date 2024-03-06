/*
Copyright 2022 FIRST Tech Challenge Team 20177

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Presets;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp

public class Drive_Centerstage extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor backleftMotor;
    private DcMotor backrightMotor;
    private DcMotor frontleftMotor;
    private DcMotor frontrightMotor;

    private DcMotor intake;
    private DcMotorEx Lift_Motor_1;
    private CRServo Spin;
    private Servo airplane;
    private Servo L_Lift;
    private Servo R_Lift;
    private DcMotor PULL;

    private DigitalChannel lF_Green;
    private DigitalChannel lF_Red;
    private DigitalChannel lR_Green;
    private DigitalChannel lR_Red;
    private DigitalChannel rF_Green;
    private DigitalChannel rF_Red;
    private DigitalChannel rR_Green;
    private DigitalChannel rR_Red;

    double intakePwr = 1;
    double tgtPower = 0;
    double clawupdate;
    boolean toggledown = false;
    boolean drivesystem = true;
    int kStartingPosition = Presets.kStartingPosition;
    int kEndPosition = Presets.kEndPosition;


    double kPower = 0;

    @Override
    public void runOpMode() {

        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        backleftMotor = hardwareMap.get(DcMotor.class, "backleftMotor");
        backrightMotor = hardwareMap.get(DcMotor.class, "backrightMotor");
        frontleftMotor = hardwareMap.get(DcMotor.class, "frontleftMotor");
        frontrightMotor = hardwareMap.get(DcMotor.class, "frontrightMotor");
        intake = hardwareMap.get(DcMotor.class, "intake");
        Lift_Motor_1 = hardwareMap.get(DcMotorEx.class, "Lift_Motor_1");
        Spin = hardwareMap.get(CRServo.class, "Spin");
        airplane = hardwareMap.get(Servo.class, "airplane");
        L_Lift = hardwareMap.get(Servo.class, "L_Lift");
        R_Lift = hardwareMap.get(Servo.class, "R_Lift");
        PULL = hardwareMap.get(DcMotor.class, "PULL");

        lF_Green = hardwareMap.get(DigitalChannel.class, "lF_Green");
        lF_Red = hardwareMap.get(DigitalChannel.class, "lF_Red");
        lR_Green = hardwareMap.get(DigitalChannel.class, "lR_Green");
        lR_Red = hardwareMap.get(DigitalChannel.class, "lR_Red");
        rF_Green = hardwareMap.get(DigitalChannel.class, "rF_Green");
        rF_Red = hardwareMap.get(DigitalChannel.class, "rF_Red");
        rR_Green = hardwareMap.get(DigitalChannel.class, "rR_Green");
        rR_Red = hardwareMap.get(DigitalChannel.class, "rR_Red");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        boolean changed = false;

        Lift_Motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Lift_Motor_1.setTargetPosition(0);

        Lift_Motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Lift_Motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        airplane.setPosition(0);

        rF_Green.setMode(DigitalChannel.Mode.OUTPUT);
        rR_Green.setMode(DigitalChannel.Mode.OUTPUT);
        lR_Green.setMode(DigitalChannel.Mode.OUTPUT);
        lF_Green.setMode(DigitalChannel.Mode.OUTPUT);
        rF_Red.setMode(DigitalChannel.Mode.OUTPUT);
        rR_Red.setMode(DigitalChannel.Mode.OUTPUT);
        lR_Red.setMode(DigitalChannel.Mode.OUTPUT);
        lF_Red.setMode(DigitalChannel.Mode.OUTPUT);

        // Wait for the game to start (driver presses PLAY)
        //claw.setPosition(0);
        waitForStart();
        // run until the end of the match (driver presses STOP)


        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.addData("Slide Encoder", Lift_Motor_1.getCurrentPosition());
            telemetry.addData("L_Lift", L_Lift.getPosition());
            telemetry.addData("R_Lift", R_Lift.getPosition());
            telemetry.update();

            //setMotorPower
            double r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;
            //these had 'final' before them at one point "final double v1 = r * Math.cos(robotangle) + rightx"
            //-Team 15036


            if (gamepad1.left_stick_button) {
                double rightx = gamepad1.right_stick_x * .25;
                double v1 = (r * Math.cos(robotAngle)) * .45 - rightx;
                double v2 = (r * Math.sin(robotAngle)) * .45 + rightx;
                double v3 = (r * Math.sin(robotAngle)) * .45 - rightx;
                double v4 = (r * Math.cos(robotAngle)) * .45 + rightx;
                frontleftMotor.setPower(v1);
                frontrightMotor.setPower(-v2);
                backleftMotor.setPower(v3);
                backrightMotor.setPower(-v4);

                rF_Red.setState(true);
                rR_Red.setState(true);
                lR_Red.setState(true);
                lF_Red.setState(true);

                rF_Green.setState(false);
                rR_Green.setState(false);
                lR_Green.setState(false);
                lF_Green.setState(false);

            } else {
                double rightx = gamepad1.right_stick_x * .45;
                double v1 = (r * Math.cos(robotAngle)) * 1 - rightx;
                double v2 = (r * Math.sin(robotAngle)) * 1 + rightx;
                double v3 = (r * Math.sin(robotAngle)) * 1 - rightx;
                double v4 = (r * Math.cos(robotAngle)) * 1 + rightx;
                frontleftMotor.setPower(v1);
                frontrightMotor.setPower(-v2);
                backleftMotor.setPower(v3);
                backrightMotor.setPower(-v4);

                rF_Green.setState(true);
                rR_Green.setState(true);
                lR_Green.setState(true);
                lF_Green.setState(true);

                rF_Red.setState(false);
                rR_Red.setState(false);
                lR_Red.setState(false);
                lF_Red.setState(false);
            }

            if (gamepad1.right_bumper){
                Lift_Motor_1.setTargetPosition(kEndPosition);
                Lift_Motor_1.setPower(1);
            }else if (gamepad1.left_bumper){
                Lift_Motor_1.setTargetPosition(kStartingPosition);
                Lift_Motor_1.setPower(.6);
            }else{

            }


            if (gamepad1.x && !changed) {
                changed = true;
            } else if (gamepad1.y && changed) {
                changed = false;
                kPower = 0;
                Spin.setPower(kPower);
                intake.setPower(kPower);
            } else if (!gamepad1.a && changed) {
                kPower = 1;
                Spin.setPower(kPower);
                intake.setPower(kPower);
            }


            tgtPower = this.gamepad1.left_trigger;
            PULL.setPower(-tgtPower);
            tgtPower = this.gamepad1.right_trigger;
            PULL.setPower(tgtPower);

            if (gamepad1.dpad_up) {
                airplane.setPosition(180);
            }else{
                airplane.setPosition(.100);
            }
            if (gamepad1.a){
                L_Lift.setPosition(.45);
                R_Lift.setPosition(.0);
            }else if (gamepad1.b){
                L_Lift.setPosition(.0);
                R_Lift.setPosition(.45);
            }

        }
    }
}

