@Autonomous
public class Auto extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor frontRight;  
  private DcMotor backLeft;
  private DcMotor backRight;
  
  //Convert from the counts per revolution of the encoder to counts per inch
  static final double HD_COUNTS_PER_REV = 28;
  static final double DRIVE_GEAR_REDUCTION = 20.15293;
  static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
  static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
  static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;
    
  @Override
  public void runOpMode() {

    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    backRight = hardwareMap.get(DcMotor.class, "backRight");

   // reverse left drive motor direciton
    backRight.setDirection(DcMotorSimple.Direction.FORWARD);
    frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
    backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    
    waitForStart();
    if (opModeIsActive()) {
      // Create target positions
      int rightTarget = frontRight.getCurrentPosition() + (int)(30 * DRIVE_COUNTS_PER_IN);
      int leftTarget = frontLeft.getCurrentPosition() + (int)(15 * DRIVE_COUNTS_PER_IN);
      
      // set target position
      frontLeft.setTargetPosition(leftTarget);
      frontRight.setTargetPosition(rightTarget);
      backLeft.setTargetPosition(leftTarget);
      backRight.setTargetPosition(rightTarget);
      
      //switch to run to position mode
      frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      
      //run to position at the desiginated power
      frontLeft.setPower(0.7);
      frontRight.setPower(0.7);
      backLeft.setPower(0.7);
      backRight.setPower(0.7);
      
      // wait until both motors are no longer busy running to position
      while (opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy())) {
      }
      
      // set motor power back to 0 
      frontLeft.setPower(0);
      frontRight.setPower(0);
      backLeft.setPower(0);
      backRight.setPower(0);
        }
     }
 }
