
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
package org.firstinspires.ftc.teamcode.opencv_auton;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Blinker;
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
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
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
@Autonomous

public class Blue_Auto extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor backleftMotor;
    private DcMotor backrightMotor;
    private DcMotor frontleftMotor;
    private DcMotor frontrightMotor;

    double tgtPower = 0;
    double clawupdate;

    @Override
    public void runOpMode() {

        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        backleftMotor = hardwareMap.get(DcMotor.class, "backleftMotor");
        backrightMotor = hardwareMap.get(DcMotor.class, "backrightMotor");
        frontleftMotor = hardwareMap.get(DcMotor.class, "frontleftMotor");
        frontrightMotor = hardwareMap.get(DcMotor.class, "frontrightMotor");



        telemetry.addData("Status", "Initialized");
        telemetry.update();
        //Encoders Init Start
        frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontleftMotor.setTargetPosition(0);
        frontrightMotor.setTargetPosition(0);
        backleftMotor.setTargetPosition(0);
        backrightMotor.setTargetPosition(0);

        frontleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        boolean running = true;

        while (opModeIsActive()) {
            telemetry.update();
            if (running == true) {
                forward(30, .5);
                sleep(5000);
                backwards(28, .5);
                left(30, .7);
                running = false;
                terminateOpModeNow();
            }else{
                terminateOpModeNow();
            }
        }
    }


    //END OF CODE

    void forward(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(47.5/23)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(47.5/23)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(47.5/23)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(47.5/23)));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(1000);
    }

    void backwards(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(47.5/23)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(47.5/23)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(47.5/23)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(47.5/23)));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(1000);
    }
    void left(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(47.5/23))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(47.5/23))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(47.5/23))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(47.5/23))));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(2000);
    }

    void right(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(47.5/23))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(47.5/23))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(47.5/23))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(47.5/23))));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
            telemetry.update();
        }
        sleep(2000);

    }
    void updatedTelemetry(){
        telemetry.addData("Status", "Running");
        telemetry.addData("FL", frontleftMotor.getCurrentPosition());
        telemetry.addData("FR", frontrightMotor.getCurrentPosition());
        telemetry.addData("BL", backleftMotor.getCurrentPosition());
        telemetry.addData("BR", backrightMotor.getCurrentPosition());
        telemetry.update();
    }


}

