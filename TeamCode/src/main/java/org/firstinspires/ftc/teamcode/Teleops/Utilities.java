package org.firstinspires.ftc.teamcode.Teleops;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class Utilities {

    private DcMotor FrontLeft, FrontRight, BackLeft, BackRight, Intake, Shooter2;
    private CRServo HFeeder1,HFeeder2;
    private DcMotorEx Shooter1;

    private Servo Feeder, FrontalSweep;
    private ElapsedTime driveTimer = new ElapsedTime();

    private Limelight3A limelight;
    int currentPipeline = 2; // 2 = red
    private final double DEAD_ZONE_DEG = 2.0;

    public void init(HardwareMap hwMap, String autoState ) {
        FrontLeft = hwMap.get(DcMotor.class, "front_left");
        FrontRight = hwMap.get(DcMotor.class, "front_right");
        BackLeft = hwMap.get(DcMotor.class, "back_left");
        BackRight = hwMap.get(DcMotor.class, "back_right");
        Intake = hwMap.get(DcMotor.class, "Intake");
        Shooter1 = hwMap.get(DcMotorEx.class, "Shooter1");
        PIDFCoefficients pidfCoefficients =
                new PIDFCoefficients(4.4, 0, 0, 16.6);  //Might change later with tweaks
        Shooter1.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        Shooter2 = hwMap.get(DcMotor.class, "Shooter2");
        //-----------------------------------------------------------

        Feeder = hwMap.get(Servo.class, "Feeder");
        //FrontalSweep = hwMap.get(Servo.class, "FrontalSweep");

        //-----------------------------------------------------------
        HFeeder1 = hwMap.get(CRServo.class, "Hfeeder1");
        HFeeder2 = hwMap.get(CRServo.class, "Hfeeder2");
        //-----------------------------------------------------------

        limelight = hwMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(currentPipeline);
        limelight.start();
        //-----------------------------------------------------------


        FrontRight.setDirection(DcMotor.Direction.REVERSE);
        BackRight.setDirection(DcMotor.Direction.REVERSE);
        FrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Shooter1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        FrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        driveTimer.reset();

    }
    public void drive(double forward, double strafe, double rotate, double speed) {
        double FrontLeftPower = forward - strafe - rotate;
        double FrontRightPower = forward + strafe + rotate;
        double BackLeftPower =  forward + strafe - rotate;
        double BackRightPower = forward -strafe + rotate;

        double maxPower = speed;
        double maxSpeed = speed;

        maxPower = Math.max(maxPower, Math.abs(FrontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(FrontRightPower));
        maxPower = Math.max(maxPower, Math.abs(BackLeftPower));
        maxPower = Math.max(maxPower, Math.abs(BackRightPower));

        FrontLeft.setPower(maxSpeed * FrontLeftPower / maxPower);
        FrontRight.setPower(maxSpeed * FrontRightPower / maxPower);
        BackLeft.setPower(maxSpeed * BackLeftPower / maxPower);
        BackRight.setPower(maxSpeed * BackRightPower / maxPower);
    }

    public void Setintake(double speed) {
        Intake.setPower(speed);
    }

    public boolean IsAligned() {
        LLResult result = limelight.getLatestResult();

        double tx = result.getTx();

        if(Math.abs(tx) < DEAD_ZONE_DEG && result.isValid()) {
            return true;
        } else {
            return false;
        }
    }

    public double TX() {
        LLResult result = limelight.getLatestResult();

        return result.getTx();

    }

    public void SetPipeline(int selection) {
        limelight.pipelineSwitch(selection);
    }
    public void updatePipelineMenu(boolean dpadUp, boolean dpadDown) {

        int selectedPipeline = currentPipeline;

        if (dpadUp) {
            selectedPipeline = 2;
        }
        else if (dpadDown) {
            selectedPipeline = 3;
        }

        if (selectedPipeline != currentPipeline) {
            currentPipeline = selectedPipeline;
            limelight.pipelineSwitch(currentPipeline);
        }
    }
    public int getCurrentPipeline() {
        if (currentPipeline == 2) {
            return 2;
        } else if (currentPipeline == 3) {
            return 3;
        }
        return -1;
    }

    public void SetTurrets(double power) {
        Shooter1.setPower(power);
        //Shooter2.setPower(power);
    }

    public double GetDistance() {
        LLResult result = limelight.getLatestResult();
        double h1 = 6.5; // Camera height in inches
        double h2 = 50.25; // Target AprilTag height in inches
        double a1 = Math.toRadians(63.43); // Camera mounting angle converted to radians
        //63.43
        // Get ty from Limelight NetworkTables and convert to radians
        double ty = result.getTy();
        double a2 = Math.toRadians(ty);

        // Calculate distance
        double distance = (h2 - h1) / Math.tan(a1 + a2);
        return distance;
    }
    public double yawerror() {
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            Pose3D botpose = result.getBotpose();

            // Extract Position (Translational Offsets in meters)
            double xOffset = botpose.getPosition().x; // Lateral offset (Left/Right)
            double yDistance = botpose.getPosition().y; // Distance to tag (Forward/Back)

            // Extract Orientation (Yaw Angle in degrees or radians)
            double yawDegrees = botpose.getOrientation().getYaw();

            // Check alignment even if tx == 0
            return yawDegrees;
        }
        return 0;
    }
    public boolean isTurretAtSpeed(double velocity) {

        return Math.abs(getTurretVelocity() - velocity) < 40;
    }

    public double getTurretVelocity() {
        return Shooter1.getVelocity();
    }

    public void SetFeeder(double power) {
        HFeeder1.setPower(power);
        HFeeder2.setPower(-power);
    }


    public void SetGate(double pos) {
        Feeder.setPosition(pos);
    }

    //public void SetSweep(double pos) {FrontalSweep.setPosition(pos);}

    public void SetShooterVelocity(double velocity) {
        Shooter1.setVelocity(velocity);
    }

} //End of class