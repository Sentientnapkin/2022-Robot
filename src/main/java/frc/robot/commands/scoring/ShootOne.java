package frc.robot.commands.scoring;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.indexer.ShootIndexedBallForever;
import frc.robot.commands.limelight.GuaranteeLimelightDataEquals;
import frc.robot.commands.portal.PulsePortal;
import frc.robot.commands.shooter.SetShooterPowerWithLimelight;
import frc.robot.lib.limelight.LimelightDataType;
import frc.robot.lib.shooterData.ShooterDataTable;
import frc.robot.subsystems.*;

// ARCHIVED
public class ShootOne extends SequentialCommandGroup {
  public ShootOne(
      DrivetrainSubsystem drivetrain,
      IndexerSubsystem indexer,
      IntakeSubsystem intake,
      ShooterSubsystem shooter,
      PortalSubsystem portal,
      LimelightSubsystem limelight,
      ShooterDataTable shooterDataTable) {
    addCommands(
        // Prepare
        new InstantCommand(drivetrain::disable, drivetrain),
        new InstantCommand(intake::disable, intake),
        // Align to shoot
        new SetShooterPowerWithLimelight(shooterDataTable, limelight, shooter),
        new GuaranteeLimelightDataEquals(
            limelight, LimelightDataType.HORIZONTAL_OFFSET, 0, Math.toRadians(1)),
        new ParallelCommandGroup(
            new PulsePortal(portal, 0.5), new ShootIndexedBallForever(indexer).withTimeout(2)),
        // Return to teleop
        new InstantCommand(shooter::disable, shooter));
  }
}
