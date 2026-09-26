package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    double forward, strafe, rotate, speed;
    double kP = 0.026;  //autotracking tune value

    private boolean initprevup = false;
    private boolean initprevdown = false;

    Utilities utils = new Utilities();

    @Override
    public void init() {
        utils.init(hardwareMap, "FALSE");

    }

    public void init_loop() {
        boolean upPressed = gamepad1.dpad_up && !initprevup;
        boolean downPressed = gamepad1.dpad_down && !initprevdown;

        // select alliance color menu in init
        initprevup = gamepad1.dpad_up;
        initprevdown = gamepad1.dpad_down;
        String selection = "";
        telemetry.addLine("Select Alliance Color:");
        telemetry.addLine("\n---> DPAD_UP = RED");
        telemetry.addLine("---> DPAD_DOWN = BLUE");
        if (utils.getCurrentPipeline() == 2) {
            selection = "RED";
        } else {
            selection = "BLUE";
        }

        telemetry.addData("\nCURRENT SELECTION:", selection);

        telemetry.update();
        utils.updatePipelineMenu(upPressed, downPressed);
    }

    @Override
    public void loop() {
        // 1. Read joysticks first
        forward = gamepad1.left_stick_y;
        strafe  = gamepad1.left_stick_x;
        rotate  = gamepad1.right_stick_x;
        speed   = 1.0; // full speed default

        if (gamepad1.left_trigger > 0.1) { // intake

            utils.Setintake(0.7);

        } else if (gamepad1.right_trigger > 0.1) { // outtake / auto-align
            if (utils.IsAligned()) {
                rotate = 0.0; // Stay still if aligned
            } else {
                if(utils.TX() == 0)
                {
                    rotate = gamepad1.right_stick_x * 2.25; //60 * kP

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