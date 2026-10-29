package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Auto", group="Robot")
public class Auto extends LinearOpMode { 
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private DcMotor intake; 

    // Convert from encoder counts to inches
    static final double HD_COUNTS_PER_REV = 28; 
    static final double DRIVE_GEAR_REDUCTION = 20.15293; 
    static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI; 
    static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM; 
    static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4; 

    private ElapsedTime runtime = new ElapsedTime();

    @Override 
    public void runOpMode() { 
        // Motor hardware mapping
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");
        backLeftMotor = hardwareMap.dcMotor.get("backLeft");
        frontRightMotor = hardwareMap.dcMotor.get("frontRight");
        backRightMotor = hardwareMap.dcMotor.get("backRight");
        intake = hardwareMap.dcMotor.get("intake");

        // Motor Direction (MATCHES YOUR TELEOP EXACTLY)
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        // Zero Power Behavior (MATCHES YOUR TELEOP EXACTLY)
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        resetDriveEncoders();

        waitForStart(); 

        if (opModeIsActive()) { 
            // ----------------------------------------------------
            // YOUR AUTONOMOUS MOVEMENT COMMANDS
            // ----------------------------------------------------
            
            // Move Straight FORWARD 24 inches (Max 4-second safety timeout)
            driveStraight(24, 0.5, 4.0);
            sleep(500);

            // Move Straight BACKWARD 12 inches
            driveStraight(-12, 0.5, 3.0);
            sleep(500);
        } 
    } 

    /**
     * Drives the robot straight forward or backward.
     * @param inches Positive for forward, negative for backward.
     */
    public void driveStraight(double inches, double power, double timeoutS) {
        // Based on your TeleOp's trigonometric mapping:
        // Forward requires all wheels to receive positive targets relative to their base configuration
        encoderMove(inches, inches, inches, inches, power, timeoutS);
    }

    /**
     * Core movement processor using RUN_TO_POSITION and safety timeouts
     */
    private void encoderMove(double flInches, double frInches, double blInches, double brInches, double power, double timeoutS) {
        if (opModeIsActive()) {
            // Calculate relative targets
            int flTarget = frontLeftMotor.getCurrentPosition() + (int)(flInches * DRIVE_COUNTS_PER_IN);
            int frTarget = frontRightMotor.getCurrentPosition() + (int)(frInches * DRIVE_COUNTS_PER_IN);
            int blTarget = backLeftMotor.getCurrentPosition() + (int)(blInches * DRIVE_COUNTS_PER_IN);
            int brTarget = backRightMotor.getCurrentPosition() + (int)(brInches * DRIVE_COUNTS_PER_IN);

            frontLeftMotor.setTargetPosition(flTarget);
            frontRightMotor.setTargetPosition(frTarget);
            backLeftMotor.setTargetPosition(blTarget);
            backRightMotor.setTargetPosition(brTarget);

            setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            runtime.reset();

            // Send power to motors
            frontLeftMotor.setPower(Math.abs(power));
            frontRightMotor.setPower(Math.abs(power));
            backLeftMotor.setPower(Math.abs(power));
            backRightMotor.setPower(Math.abs(power));

            // Keep moving until time runs out OR all active motors finish
            while (opModeIsActive() && (runtime.seconds() < timeoutS) && 
                  (frontLeftMotor.isBusy() && frontRightMotor.isBusy() && backLeftMotor.isBusy() && backRightMotor.isBusy())) {
                
                telemetry.addData("Time left", "%.1f s", timeoutS - runtime.seconds());
                telemetry.addData("FL Pos", "%d -> %d", frontLeftMotor.getCurrentPosition(), flTarget);
                telemetry.addData("FR Pos", "%d -> %d", frontRightMotor.getCurrentPosition(), frTarget);
                telemetry.update();
            }

            stopRobot();
            setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void stopRobot() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        frontLeftMotor.setMode(mode);
        frontRightMotor.setMode(mode);
        backLeftMotor.setMode(mode);
        backRightMotor.setMode(mode);
    }

    private void resetDriveEncoders() {
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
