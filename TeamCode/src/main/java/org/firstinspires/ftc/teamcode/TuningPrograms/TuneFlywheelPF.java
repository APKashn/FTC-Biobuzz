package org.firstinspires.ftc.teamcode.TuningPrograms;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/*
https://www.youtube.com/watch?v=aPNCpZzCTKg
 */
@Disabled
@TeleOp

public class TuneFlywheelPF extends OpMode {

    public DcMotorEx FlyWheelMotor;

    double highVelocity = 2230;
    double lowVelocity = 200;

    double curTargetVelocity = highVelocity;

    double F = 0;
    double P = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001};

    int stepIndex = 1;

    @Override
    public void init() {
        FlyWheelMotor = hardwareMap.get(DcMotorEx.class, "Shooter1");
        FlyWheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FlyWheelMotor.setDirection(DcMotor.Direction.REVERSE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F );
        FlyWheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }
    @Override
    public void loop() {
        //switch between high and low velocity
        if (gamepad1.yWasPressed()) {
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            }
            else {
                curTargetVelocity = highVelocity;
            }
        }

        //choose initial P and F value from stepSizes array
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        //Options to increase or decrease F values.
        if (gamepad1.dpadLeftWasPressed()) {
            F -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()) {
            F += stepSizes[stepIndex];
        }

        //Options to increase or decrease P values.
        if (gamepad1.dpadUpWasPressed()) {
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()) {
            P -= stepSizes[stepIndex];
        }

        //set new PIDF Coefficients
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F );
        FlyWheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        //set velocity
        FlyWheelMotor.setVelocity(curTargetVelocity);

        double curVelocity = FlyWheelMotor.getVelocity();
        double error = curTargetVelocity - curVelocity;

        telemetry.addData("Target Velocity", curTargetVelocity);
        telemetry.addData("Current Velocity", "%.2f", curVelocity);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("_____________________________");
        telemetry.addData("Tuning P", "%.4f (Dpad U/D)", P);
        telemetry.addData("Tuning F", "%4f (Dpad L/R", F);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
    }
}
