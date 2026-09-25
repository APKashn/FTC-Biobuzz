package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp

public class Teleop extends OpMode {
    double forward,strafe,rotate,speed;

    Utilities utils = new Utilities();
    @Override
    public void init() {
        utils.init(hardwareMap, "FALSE");

        telemetry.addLine("Press start");
        telemetry.update();
    }


    @Override
    public void loop() {
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        speed = 1.0; // full

        utils.drive(forward, strafe, rotate, speed);

        // Combine into a single conditional block:
        if (gamepad1.left_trigger > 0.1) {
            utils.Setintake(1.0);
        } else if (gamepad1.right_trigger > 0.1) {
            utils.Setintake(-1.0);
        } else {
            utils.Setintake(0.0); // Only stop if NEITHER trigger is pressed
        }
    }

    @Override
    public void stop() {
        utils.Setintake(0);
    }

}
