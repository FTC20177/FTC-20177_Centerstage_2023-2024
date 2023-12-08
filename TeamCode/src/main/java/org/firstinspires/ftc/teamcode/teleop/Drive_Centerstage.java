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
//import org.firstinspires.ftc.teamcode.Presets;


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
    private DcMotorEx Lift_Motor_2;

    private CRServo Spin;


    double intakePwr = 1;
    double tgtPower = 0;
    double clawupdate;
    boolean toggledown = false;
    boolean drivesystem = true;


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
        Lift_Motor_2 = hardwareMap.get(DcMotorEx.class, "Lift_Motor_2");
        Spin = hardwareMap.get(CRServo.class, "Spin");


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        boolean changed = false;
        // Wait for the game to start (driver presses PLAY)
        //claw.setPosition(0);
        waitForStart();
        // run until the end of the match (driver presses STOP)


        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();

            //setMotorPower
            double r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;
            //these had 'final' before them at one point "final double v1 = r * Math.cos(robotangle) + rightx"
            //-Team 15036

            double rightx = gamepad1.right_stick_x * .75;
            double v1 = (r * Math.cos(robotAngle)) * 1 - rightx;
            double v2 = (r * Math.sin(robotAngle)) * 1 + rightx;
            double v3 = (r * Math.sin(robotAngle)) * 1 - rightx;
            double v4 = (r * Math.cos(robotAngle)) * 1 + rightx;
            frontleftMotor.setPower(v1);
            frontrightMotor.setPower(-v2);
            backleftMotor.setPower(v3);
            backrightMotor.setPower(-v4);

            //intake.setPower(intakePwr);

            //tgtPower = this.gamepad1.left_trigger;
            //Lift_Motor_1.setVelocity(-2400);
            //Lift_Motor_2.setVelocity(2400);
            //tgtPower = this.gamepad1.right_trigger;
            //Lift_Motor_1.setVelocity(2400);
            //Lift_Motor_2.setVelocity(-2400);

            tgtPower = this.gamepad1.left_trigger;
            Lift_Motor_1.setPower(-tgtPower);
            Lift_Motor_2.setPower(tgtPower);
            tgtPower = this.gamepad1.right_trigger;
            Lift_Motor_1.setPower(tgtPower);
            Lift_Motor_2.setPower(-tgtPower);


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


        }
    }
}

