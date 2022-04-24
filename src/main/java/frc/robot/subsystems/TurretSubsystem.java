package frc.robot.subsystems;

import static com.ctre.phoenix.motorcontrol.TalonFXControlMode.PercentOutput;
import static frc.robot.Constants.MechanismConstants.turretMotorPort;
import static frc.robot.Constants.MechanismConstants.turretTurnSpeed;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.lib.limelight.GoalNotFoundException;
import frc.robot.lib.limelight.LimelightDataLatch;
import frc.robot.lib.limelight.LimelightDataType;

public class TurretSubsystem extends SubsystemBase {
  public final WPI_TalonFX turretMotor = new WPI_TalonFX(turretMotorPort);
  public final ProfiledPIDController turretPID;

  private final LimelightSubsystem limelight;
  private final LimelightDataLatch turretToleranceDistanceLatch;

  public double currentTurretToleranceRadians = Math.toRadians(5);
  public boolean PIDRunning = false;

  public TurretSubsystem() {
    this.limelight = RobotContainer.limelight;
    turretMotor.setInverted(false);
    turretMotor.setNeutralMode(NeutralMode.Brake);

    turretToleranceDistanceLatch = new LimelightDataLatch(LimelightDataType.DISTANCE, 16);
    turretPID =
        new ProfiledPIDController(
            2.4, // 3.9
            0,
            0.1,
            new TrapezoidProfile.Constraints(Math.PI / 2, Math.PI / 4)
            // 5.85, 1.188
            // 3.85, 0.02, 0.068, new TrapezoidProfile.Constraints(Math.PI / 2, Math.PI / 4)
            );

    // 3.85, 0.02, 0.068 new trap(pi/2,pi/4)

    setTurretStartingAngleDegrees(
        -180); // assume default position is turret starting facing backwards counterclockwise
    setTurretSetpointRadians(getTurretAngleRadians());
  }

  public void turnTurret(double power) {
    turretMotor.set(
        ControlMode.PercentOutput,
        power > turretTurnSpeed ? turretTurnSpeed : Math.max(power, -turretTurnSpeed));
    SmartDashboard.putNumber("motor PID output", power);
  }

  // Primarily for use in auto routines where we need to know where the shooter starts
  public void setTurretStartingAngleDegrees(double position) {
    turretMotor.setSelectedSensorPosition(2048 * position / 36);
    turretPID.setGoal(Math.toRadians(position));
  }

  // CW Positive
  public void setTurretSetpointRadians(double angle) {
    turretPID.setGoal(angle);
    PIDRunning = true;
  }

  public double getTurretAngleRadians() {
    return Math.toRadians(turretMotor.getSelectedSensorPosition() * 36 / 2048);
  }

  private void updateCurrentTurretTolerance() {
    try {
      if (turretToleranceDistanceLatch.unlocked()) {
        currentTurretToleranceRadians = Math.toRadians(3.2) / turretToleranceDistanceLatch.open();
        turretPID.setTolerance(currentTurretToleranceRadians);
        SmartDashboard.putNumber("turret tolerance", Math.toDegrees(currentTurretToleranceRadians));
      }
    } catch (GoalNotFoundException e) {
      limelight.addLatch(turretToleranceDistanceLatch.reset());
    }
  }

  public void disable() {
    turretMotor.set(PercentOutput, 0);
    setTurretSetpointRadians(getTurretAngleRadians());
    PIDRunning = false;
  }

  @Override
  public void periodic() {
    updateCurrentTurretTolerance();
    if (PIDRunning) {
      turnTurret(turretPID.calculate(getTurretAngleRadians()));
    }
  }
}
