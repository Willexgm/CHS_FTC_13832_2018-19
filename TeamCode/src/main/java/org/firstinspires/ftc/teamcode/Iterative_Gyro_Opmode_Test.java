/* Copyright (c) 2017 FIRST. All rights reserved.
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

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;



/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

//reeeeeeeeeeeeee
@TeleOp(name="Basic: Iterative OpMode", group="Iterative Opmode")
@Disabled
public class Iterative_Gyro_Opmode_Test extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private IntegratingGyroscope gyro;
    private ModernRoboticsI2cGyro modernRoboticsI2cGyro;

    boolean lastResetState = false;
    boolean curResetState  = false;



    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        ElapsedTime timer = new ElapsedTime();

        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");

        // Get a reference to a Modern Robotics gyro object. We use several interfaces
        // on this object to illustrate which interfaces support which functionality.
        modernRoboticsI2cGyro = hardwareMap.get(ModernRoboticsI2cGyro.class, "gyro");
        gyro = (IntegratingGyroscope)modernRoboticsI2cGyro;
        // If you're only interested int the IntegratingGyroscope interface, the following will suffice.
        // gyro = hardwareMap.get(IntegratingGyroscope.class, "gyro");
        // A similar approach will work for the Gyroscope interface, if that's all you need.

        // Start calibrating the gyro. This takes a few seconds and is worth performing
        // during the initialization phase at the start of each opMode.
        telemetry.log().add("Gyro Calibrating. Do Not Move!");
        modernRoboticsI2cGyro.calibrate();

        timer.reset();

        while (modernRoboticsI2cGyro.isCalibrating())  {
            telemetry.addData("calibrating", "%s", Math.round(timer.seconds())%2==0 ? "|.." : "..|");
            telemetry.update();

        }

        telemetry.log().clear(); telemetry.log().add("Gyro Calibrated. Press Start.");
        telemetry.clear(); telemetry.update();

        // Wait for the start button to be presse
        telemetry.log().clear();



    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        curResetState = (gamepad1.a && gamepad1.b);
        if (curResetState && !lastResetState) {
            modernRoboticsI2cGyro.resetZAxisIntegrator();
        }
        lastResetState = curResetState;

        int rawX = modernRoboticsI2cGyro.rawX();
        int rawY = modernRoboticsI2cGyro.rawY();
        int rawZ = modernRoboticsI2cGyro.rawZ();
        int heading = modernRoboticsI2cGyro.getHeading();
        int integratedZ = modernRoboticsI2cGyro.getIntegratedZValue();

        AngularVelocity rates = gyro.getAngularVelocity(AngleUnit.DEGREES);

        double actual_turning_rate = rates.xRotationRate;

        telemetry.addLine().addData("dx", formatRate(((float) actual_turning_rate)));




        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;

        double game_pad_left = gamepad1.left_stick_y;
        double game_pad_right  =  gamepad1.right_stick_y;

        double difference = game_pad_left-game_pad_right;

        double difference_threshold_stick_movement = .10;

        if (difference < difference_threshold_stick_movement){
            game_pad_left = game_pad_right;
        }

        double constant = 5;

        double turning_rate_goal = difference*constant;

        //If difference = positive, turn right, else turn left
        double zero_threshold = .05;

        if (((0-zero_threshold)<game_pad_left &&
                (0+zero_threshold>game_pad_left)) &&
                ((0-zero_threshold)<game_pad_right &&
                (0+zero_threshold>game_pad_right))) {
            game_pad_left = 0;
            game_pad_right = 0;
        }

        double average = (game_pad_left+game_pad_right)/2;

        //forward
        if (game_pad_left>0 && game_pad_right >0) {
            leftPower = average + turning_rate_goal;
            rightPower = average - turning_rate_goal;
        }
        //backward
        if (game_pad_left<0 && game_pad_right<0) {
            leftPower = average - turning_rate_goal;
            rightPower = average + turning_rate_goal;
        }
        //wheels in different directions
        if (game_pad_left*game_pad_right<0) {
            leftPower = turning_rate_goal;
            rightPower = -turning_rate_goal;

        }
        //no movement
        if (game_pad_left == 0 && game_pad_right == 0){
            leftPower = 0;
            rightPower = 0;
        }
        else {
            leftPower = 0;
            rightPower = 0;
        }









        leftPower    = Range.clip(leftPower, -1.0, 1.0) ;
        rightPower   = Range.clip(rightPower, -1.0, 1.0) ;
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    String formatRate(float rate) {
        return String.format("%.3f", rate);
    }

}
