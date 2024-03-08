/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opencv_auton;

import static org.firstinspires.ftc.teamcode.Presets.getkAutoScorePositionPLUS;
import static org.firstinspires.ftc.teamcode.Presets.kAutoScorePosition;
import static org.firstinspires.ftc.teamcode.Presets.kEndPosition;
import static org.firstinspires.ftc.teamcode.Presets.kNoTouchPosition;
import static org.firstinspires.ftc.teamcode.Presets.kStartingPosition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/*
 * This OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
@Autonomous(name = "Red_Far", group = "Concept")
public class Red_Far extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotorEx backleftMotor;
    private DcMotorEx backrightMotor;
    private DcMotorEx frontleftMotor;
    private DcMotorEx frontrightMotor;
    private DcMotorEx Lift_Motor_1;
    private Servo airplane;
    private Servo L_Lift;
    private Servo R_Lift;
    private DcMotor PULL;

    double tgtPower = 0;
    double clawupdate;

    boolean left = false;
    boolean center = false;
    boolean right = false;

    float pos = 0;



    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "model_20231213_154156.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.
    //private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/model_20231204_135821.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "Red Cat",
    };

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    private void initTfod() {


        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                .setIsModelTensorFlow2(true)
                .setIsModelQuantized(true)
                .setModelInputSize(300)
                .setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.85f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);


    }   // end method initTfod()

    private void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());


        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());

            if (x >= 10 && x <= 170) {
                telemetry.addData("Position", "Left");
                telemetry.addData("Center pos", x);
                pos = 1;
            } else if (x >= 250 && x <= 400) {
                telemetry.addData("Position", "Center");
                telemetry.addData("Center pos", x);
                pos = 2;
            } else if (x >= 450 && x <= 900) {
                telemetry.addData("Position", "Right");
                telemetry.addData("Center pos", x);
                pos = 3;
            } else {
                telemetry.addData("Position", "Unknown");
                telemetry.addData("position left", recognition.getLeft());
                telemetry.addData("position right", recognition.getRight());
                telemetry.addData("Center pos", x);

            }

            telemetry.update();
        }
    }   // end for() loop

    @Override
    public void runOpMode() {

        //hardware map
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        backleftMotor = hardwareMap.get(DcMotorEx.class, "backleftMotor");
        backrightMotor = hardwareMap.get(DcMotorEx.class, "backrightMotor");
        frontleftMotor = hardwareMap.get(DcMotorEx.class, "frontleftMotor");
        frontrightMotor = hardwareMap.get(DcMotorEx.class, "frontrightMotor");
        Lift_Motor_1 = hardwareMap.get(DcMotorEx.class, "Lift_Motor_1");
        airplane = hardwareMap.get(Servo.class, "airplane");
        L_Lift = hardwareMap.get(Servo.class, "L_Lift");
        R_Lift = hardwareMap.get(Servo.class, "R_Lift");
        PULL = hardwareMap.get(DcMotor.class, "PULL");


        initTfod();


        while (!opModeIsActive() && !isStopRequested()) {
            // Wait for the DS start button to be touched.
            telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
            telemetry.addData(">", "Touch Play to start OpMode");
            telemetryTfod();
            telemetry.update();
            airplane.setPosition(0);

        }


        if (opModeIsActive()) {
            while (opModeIsActive()) {
                telemetry.addData("Status", "Running");
                telemetry.addData("Encoder", Lift_Motor_1.getCurrentPosition());
                telemetry.update();

                airplane.setPosition(0);


                frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                Lift_Motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                frontleftMotor.setTargetPosition(0);
                frontrightMotor.setTargetPosition(0);
                backleftMotor.setTargetPosition(0);
                backrightMotor.setTargetPosition(0);

                Lift_Motor_1.setTargetPosition(0);

                frontleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                Lift_Motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                if (pos == 1) {
                    //left

                    Lift_Motor_1.setTargetPosition(kNoTouchPosition);
                    Lift_Motor_1.setPower(1);

                    forward (24.5, .5);
                    sleep(1000);

                    frontleftMotor.setTargetPosition(frontleftMotor.getCurrentPosition() - 1000);
                    frontrightMotor.setTargetPosition(frontrightMotor.getCurrentPosition() - 1000);
                    backleftMotor.setTargetPosition(backleftMotor.getCurrentPosition() - 1000);
                    backrightMotor.setTargetPosition(backrightMotor.getCurrentPosition() - 1000);

                    frontleftMotor.setPower(.8);
                    frontrightMotor.setPower(.8);
                    backleftMotor.setPower(.8);
                    backrightMotor.setPower(.8);

                    sleep(1000);

                    forward(6, .5);

                    sleep(500);

                    backwards(10, .5);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(1000);

                    left(30, .5);


                    sleep(500);

                    forward(80, .7);

                    right(30, .5);

                    Lift_Motor_1.setTargetPosition(getkAutoScorePositionPLUS);
                    Lift_Motor_1.setPower(1);

                    sleep(1000);

                    right(10, .5);

                    forward(15, .25);

                    backwards(5, .25);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(6000);

                    terminateOpModeNow();
                } else if (pos == 2) {
                    //middle

                    //extend slide

                    Lift_Motor_1.setTargetPosition(kEndPosition);
                    Lift_Motor_1.setPower(1);

                    forward(30, .5);
                    sleep (500);
                    backwards(10, .5);
                    left(16, .5);
                    forward(33.3, .5);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(1500);

                    frontleftMotor.setTargetPosition(frontleftMotor.getCurrentPosition() - 1050);
                    frontrightMotor.setTargetPosition(frontrightMotor.getCurrentPosition() - 1050);
                    backleftMotor.setTargetPosition(backleftMotor.getCurrentPosition() - 1050);
                    backrightMotor.setTargetPosition(backrightMotor.getCurrentPosition() - 1050);

                    frontleftMotor.setPower(.8);
                    frontrightMotor.setPower(.8);
                    backleftMotor.setPower(.8);
                    backrightMotor.setPower(.8);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(1500);

                    forward(80, .5);

                    right(40, .5);

                    frontleftMotor.setTargetPosition(frontleftMotor.getCurrentPosition() - 100);
                    frontrightMotor.setTargetPosition(frontrightMotor.getCurrentPosition() - 100);
                    backleftMotor.setTargetPosition(backleftMotor.getCurrentPosition() - 100);
                    backrightMotor.setTargetPosition(backrightMotor.getCurrentPosition() - 100);

                    frontleftMotor.setPower(.8);
                    frontrightMotor.setPower(.8);
                    backleftMotor.setPower(.8);
                    backrightMotor.setPower(.8);

                    Lift_Motor_1.setTargetPosition(kAutoScorePosition);
                    Lift_Motor_1.setPower(1);

                    sleep(1000);

                    forward(25, .25);

                    backwards(7, .25);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(2000);

                    terminateOpModeNow();
                } else if (pos == 3) {
                    //right

                    Lift_Motor_1.setTargetPosition(kEndPosition);
                    Lift_Motor_1.setPower(1);

                    forward (3, .5);

                    left (14.5, .5);

                    forward (23, .5);

                    backwards(25, .5);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(2000);

                    right(81, .7);

                    sleep(500);

                    //forward(25, .5);

                    frontleftMotor.setTargetPosition(frontleftMotor.getCurrentPosition() - 1000);
                    frontrightMotor.setTargetPosition(frontrightMotor.getCurrentPosition() - 1000);
                    backleftMotor.setTargetPosition(backleftMotor.getCurrentPosition() - 1000);
                    backrightMotor.setTargetPosition(backrightMotor.getCurrentPosition() - 1000);

                    frontleftMotor.setPower(.8);
                    frontrightMotor.setPower(.8);
                    backleftMotor.setPower(.8);
                    backrightMotor.setPower(.8);

                    sleep(1000);

                    left(36, .5);

                    frontleftMotor.setTargetPosition(frontleftMotor.getCurrentPosition() - 150);
                    frontrightMotor.setTargetPosition(frontrightMotor.getCurrentPosition() - 150);
                    backleftMotor.setTargetPosition(backleftMotor.getCurrentPosition() - 150);
                    backrightMotor.setTargetPosition(backrightMotor.getCurrentPosition() - 150);

                    frontleftMotor.setPower(.8);
                    frontrightMotor.setPower(.8);
                    backleftMotor.setPower(.8);
                    backrightMotor.setPower(.8);

                    sleep(200);

                    Lift_Motor_1.setTargetPosition(getkAutoScorePositionPLUS);
                    Lift_Motor_1.setPower(1);

                    sleep(1000);

                    forward(25, 1);
                    //left (2, .5);
                    forward(10, .25);

                    backwards(5, .25);

                    Lift_Motor_1.setTargetPosition(kStartingPosition);
                    Lift_Motor_1.setPower(1);

                    sleep(5000);

                    terminateOpModeNow();
                } else {
                    //middle code (Even if undetected)
                }


            }   // end for() loop

        }   // end method telemetryTfod()

    }// end of code

    void forward(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)(distance*(537.7/12.1211)*(30/26)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)(distance*(537.7/12.1211)*(30/26)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)(distance*(537.7/12.1211)*(30/26)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)(distance*(537.7/12.1211)*(30/26)));
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

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)(distance*(537.7/12.1211)*(30/26)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)(distance*(537.7/12.1211)*(30/26)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)(distance*(537.7/12.1211)*(30/26)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)(distance*(537.7/12.1211)*(30/26)));
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

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)((distance*(537.7/12.1211)*(30/26))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)((distance*(537.7/12.1211)*(30/26))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)((distance*(537.7/12.1211)*(30/26))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)((distance*(537.7/12.1211)*(30/26))));
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

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)((distance*(537.7/12.1211)*(30/26))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)((distance*(537.7/12.1211)*(30/26))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)((distance*(537.7/12.1211)*(30/26))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)((distance*(537.7/12.1211)*(30/26))));
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






