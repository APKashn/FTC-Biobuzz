package org.firstinspires.ftc.teamcode.Autonoumous;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.util.Timer;
import com.pedropathing.follower.Follower;


@TeleOp
public class RedAuto extends OpMode {
    private Follower follower; // ts not working
    private Timer pathTimer, opModeTimer;

    public enum PathState {
        STARTPOS_SHOOTPOS,
        FIRST_SHOOT,
        PRE_GET_POLLEN,
        GET_POLLEN
    }

    PathState pathstate, ;

    private final Pose startPos = new Pose(56, 8, Math.toRadians(90));
    private final Pose FirstShoot = new Pose(56, 36, Math.toRadians(90));
    private final Pose PreGetPollen = new Pose(27.8, 8.4, Math.toRadians(180));
    private final Pose GetPollen = new Pose(8.6, 8.4, Math.toRadians(180));

    private PathChain startPos_FirstShoot, FirstShoot_PreGetPollen, PreGetPollen_GetPollen;

    public void buildPaths(){
        startPos_FirstShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPos, FirstShoot))
                .setLinearHeadingInterpolation(startPos.getHeading(), FirstShoot.getHeading())
                .build();

        FirstShoot_PreGetPollen = follower.pathBuilder()
                .addPath(new BezierLine(FirstShoot, PreGetPollen))
                .setLinearHeadingInterpolation(FirstShoot.getHeading(), PreGetPollen.getHeading())
                .build();

        PreGetPollen_GetPollen = follower.pathBuilder()
                .addPath(new BezierLine(PreGetPollen, GetPollen))
                .setLinearHeadingInterpolation(startPos.getHeading(), FirstShoot.getHeading())
                .build();
    }

    public void statePathUpdate() {
        switch(pathstate){
            case STARTPOS_SHOOTPOS:
                follower.followPath(startPos_FirstShoot, true);
                setPathState(PathState.FIRST_SHOOT);
                break;

            case FIRST_SHOOT:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.5 ){
                    telemetry.addLine("Went to Shooting Position");
                }
                break;

            case PRE_GET_POLLEN:
                if (!follower.isBusy()) {
                    telemetry.addLine("About to get el pollen");
                }

            case GET_POLLEN:
                if (!follower.isBusy()) {
                    telemetry.addLine("Hopefully got el pollen");
                }

            default:
                telemetry.addLine("You sold");
                break;
        }
    }

    public void setPathState(PathState newState){
        pathstate = newState;
        pathTimer.resetTimer();

    }


    @Override
    public void init() {
        pathstate = PathState.STARTPOS_SHOOTPOS;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        opModeTimer.resetTimer();
        follower = Constants.createFollower(hardwareMap);


        buildPaths();
        follower.setPose(startPos);
    }

    public void start() {
        opModeTimer.resetTimer();
        setPathState(pathstate);
    }


    @Override
    public void loop() {
        follower.update();
        startPathUpdate();
    }
}

