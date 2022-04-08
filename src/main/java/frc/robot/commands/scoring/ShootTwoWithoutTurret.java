package frc.robot.commands.scoring;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.hood.SetHoodAngleWithLimelightTimeSafe;
import frc.robot.commands.indexer.ShootIndexedBallsForever;
import frc.robot.commands.intake.DisableIntake;
import frc.robot.commands.shooter.DisableShooter;
import frc.robot.commands.shooter.SetShooterPowerWithLimelight;
import frc.robot.lib.shooterData.ShooterDataTable;
import frc.robot.subsystems.*;

// FOR AUTOS
public class ShootTwoWithoutTurret extends SequentialCommandGroup {
  public ShootTwoWithoutTurret(
      IndexerSubsystem indexer,
      IntakeSubsystem intake,
      ShooterSubsystem shooter,
      HoodSubsystem hood,
      PortalSubsystem portal,
      LimelightSubsystem limelight,
      ShooterDataTable shooterDataTable) {
    addCommands(
        // Prepare
        new DisableIntake(intake),
        // Align to shoot,
        new SetShooterPowerWithLimelight(shooterDataTable, limelight, shooter),
        new SetHoodAngleWithLimelightTimeSafe(shooterDataTable, limelight, hood),
        // Shoot Balls
        new ShootIndexedBallsForever(indexer, intake, portal).withTimeout(2.2),
        // Return to
        new DisableShooter(shooter));
  }
}
