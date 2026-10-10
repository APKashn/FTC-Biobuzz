package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@TeleOp
public class Teleop extends OpMode {
    double forward, strafe, rotate, speed;
    double kP = 0.026;  //autotracking tune value

    private boolean initprevup = false;
    double Velocity = 1000;

    private boolean initprevdown = false;
    boolean Undertempo;

    Utilities utils = new Utilities();

    @Override
    public void init() {
        utils.init(hardwareMap, "FALSE");

        utils.SetGate(0.07);
       // utils.SetSweep(0.0);


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
        double x = utils.GetDistance();
       // double yr = utils.yawerror();
        forward = gamepad1.left_stick_y;
        strafe  = gamepad1.left_stick_x;
        rotate  = gamepad1.right_stick_x;
        speed   = 1.0; // full speed default

        if(!Double.isNaN(x) && x>0) {
            Velocity = Range.clip(
                    (-0.00815847) * x * x * x * x
                            + 1.22423 * x * x * x
                            - 68.62971 * x * x
                            + 1707.13647 * x
                            - 15008.5014,
                    916, 1000
            );

            utils.SetShooterVelocity(Velocity); //Turret power

        }



        if (gamepad1.left_trigger > 0.1) { // intake
            utils.Setintake(0.7);
            //utils.SetFeeder(-1.0);
            //utils.SetGate(0.07);

        } else if(gamepad1.left_bumper) {

            utils.Setintake(-0.4); //remove pollen

        } else if (gamepad1.right_trigger > 0.1) {

            utils.SetGate(0.7);
            utils.SetFeeder(1.0);

            if (gamepad1.left_trigger > 0.1 && utils.isTurretAtSpeed(Velocity)) {
                utils.Setintake(0.7);
            } else {
                utils.Setintake(0.0);
            }

            if (utils.IsAligned()) {
                rotate = 0.0;
            } else {
                if (utils.TX() == 0) {
                    rotate = gamepad1.right_stick_x * 2.25;
                } else {
                    rotate = utils.TX() * kP;
                }

                rotate = Math.max(-0.4, Math.min(0.4, rotate));
            }
        } else {
            utils.Setintake(0.0);
            utils.SetFeeder(0.0);
            utils.SetGate(0.07);
        }

        if(utils.getTurretVelocity() < Velocity) {
            Undertempo = true;
        } else {Undertempo = false;}
        utils.drive(forward, strafe, rotate, speed);

        //Telemetry updates

        telemetry.addData("tx:", utils.TX());
        telemetry.addData("aligned?:", utils.IsAligned());
        telemetry.addData("Distance:", utils.GetDistance());

        telemetry.addData("Commanded Velocity:", Velocity);
        telemetry.addData("Actual Velocity", utils.getTurretVelocity());
        telemetry.addData("Running low:", Undertempo);
        telemetry.update();
    }

    @Override
    public void stop() {
        utils.Setintake(0);
    }
} //End of class