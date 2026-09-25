package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    double forward, strafe, rotate, speed;
    double kP = 0.026;

    Utilities utils = new Utilities();

    @Override
    public void init() {
        utils.init(hardwareMap, "FALSE");
        telemetry.addLine("Press start");
        telemetry.update();
    }

    @Override
    public void loop() {
        // 1. Read joysticks first
        forward = gamepad1.left_stick_y;
        strafe  = gamepad1.left_stick_x;
        rotate  = gamepad1.right_stick_x;
        speed   = 1.0; // full speed default

        if (gamepad1.left_trigger > 0.1) { // intake

            utils.Setintake(0.6);

        } else if (gamepad1.right_trigger > 0.1) { // outtake / auto-align
            if (utils.IsAligned()) {
                rotate = 0.0; // Stay still if aligned
            } else {
                if(utils.TX() == 0)
                {
                    rotate = 60 * kP;

                } else {
                    rotate = utils.TX() * kP;
                }
                rotate = Math.max(-0.4, Math.min(0.4, rotate)); //turn
            }
        } else {
            utils.Setintake(0.0);
        }

        utils.drive(forward, strafe, rotate, speed);

        //Telemetry updates

        telemetry.addData("tx:", utils.TX());
        telemetry.addData("aligned?:", utils.IsAligned());
        telemetry.update();
    }

    @Override
    public void stop() {
        utils.Setintake(0);
    }
}