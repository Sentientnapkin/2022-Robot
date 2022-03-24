package frc.robot.commands.outtake;


import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants;
import frc.robot.commands.drive.DisableDrivetrain;
import frc.robot.commands.indexer.ShootIndexedBallForever;
import frc.robot.commands.intake.DisableIntake;
import frc.robot.commands.intake.PulseIntakeToIndexerMotor;
import frc.robot.commands.intake.RunIntakeWithoutPneumatics;
import frc.robot.commands.limelight.GuaranteeLimelightData;
import frc.robot.commands.limelight.GuaranteeLimelightDataEquals;
import frc.robot.lib.limelight.LimelightDataType;
import frc.robot.lib.shooterData.ShooterDataTable;
import frc.robot.subsystems.*;


public class ShootBalls extends SequentialCommandGroup {
    public ShootBalls(ClimberSubsystem climber, DrivetrainSubsystem drivetrain, IndexerSubsystem indexer, IntakeSubsystem intake, OuttakeSubsystem outtake, LimelightSubsystem limelight, ShooterDataTable shooterDataTable) {
        if (climber.getLeftHeightPercent() > 0.1 || climber.getRightHeightPercent() > 0.1) this.cancel();
            addCommands(
                    //Prepare
                    new DisableDrivetrain(drivetrain),
                    new DisableIntake(intake),
                    //Guarantee ball indexed and limelight data
                    new ParallelDeadlineGroup(
                            new ParallelCommandGroup(
                                    new ConditionalCommand(
                                            new WaitCommand(0),
                                            new RunIntakeWithoutPneumatics(intake, indexer).withTimeout(0.75),
                                            indexer::ballPrimed
                                    ), new GuaranteeLimelightData(limelight)
                            ),
                          new ManualAdjustTurret(outtake)
                    ),
                    //Align and shoot first
                    new ParallelCommandGroup(
                            new SetShooterPowerWithLimelight(shooterDataTable, limelight, outtake),
                            new SetHoodAngleWithLimelightTimeSafe(shooterDataTable, limelight, outtake)
                    ),
                    new ParallelDeadlineGroup(
                            new ConditionalCommand(
                                    new SequentialCommandGroup(
                                            new GuaranteeLimelightDataEquals(limelight, LimelightDataType.HORIZONTAL_OFFSET, 0, Math.toRadians(outtake.currentShooterToleranceDegrees)).withTimeout(2),
                                            new ParallelCommandGroup(
                                                    new PulseIntakeToIndexerMotor(intake).withTimeout(0.3),
                                                    new ShootIndexedBallForever(indexer, outtake).withTimeout(1.5)
                                            ), new RunIntakeWithoutPneumatics(intake, indexer).withTimeout(0.75)
                                    ),
                                    new ConditionalCommand(
                                            new SequentialCommandGroup(
                                                    new SetShooterPower(outtake, 15),
                                                    new ParallelCommandGroup(
                                                            new PulseIntakeToIndexerMotor(intake).withTimeout(0.3),
                                                            new ShootIndexedBallForever(indexer, outtake).withTimeout(1.5)
                                                    ), new RunIntakeWithoutPneumatics(intake, indexer).withTimeout(0.75)
                                            ),
                                            new WaitCommand(0),
                                            indexer::ballPrimed
                                    ),
                                    indexer::allianceBallIndexed
                            ),
                            new AlwaysTurretTurnToGoalWithLimelight(limelight, outtake)
                    ),
                    //Align and shoot second
                    new ParallelCommandGroup(
                            new SetShooterPowerWithLimelight(shooterDataTable, limelight, outtake),
                            new SetHoodAngleWithLimelightTimeSafe(shooterDataTable, limelight, outtake)
                    ),
                    new ParallelDeadlineGroup(
                            new ConditionalCommand(
                                    new SequentialCommandGroup(
                                            new GuaranteeLimelightDataEquals(limelight, LimelightDataType.HORIZONTAL_OFFSET, 0, Math.toRadians(outtake.currentShooterToleranceDegrees)).withTimeout(2),
                                            new ParallelCommandGroup(
                                                    new PulseIntakeToIndexerMotor(intake).withTimeout(0.3),
                                                    new ShootIndexedBallForever(indexer, outtake).withTimeout(1.5)
                                            ), new RunIntakeWithoutPneumatics(intake, indexer).withTimeout(0.75)
                                    ),
                                    new ConditionalCommand(
                                            new SequentialCommandGroup(
                                                    new SetShooterPower(outtake, 15),
                                                    new ParallelCommandGroup(
                                                            new PulseIntakeToIndexerMotor(intake).withTimeout(0.3),
                                                            new ShootIndexedBallForever(indexer, outtake).withTimeout(1.5)
                                                    ), new RunIntakeWithoutPneumatics(intake, indexer).withTimeout(0.75)
                                            ),
                                            new WaitCommand(0),
                                            indexer::ballPrimed
                                    ),
                                    indexer::allianceBallIndexed
                            ),
                            new AlwaysTurretTurnToGoalWithLimelight(limelight, outtake)
                    ),
                    //Return to teleop
                    new ParallelCommandGroup(
                            new DisableShooter(outtake),
                            new SetHoodAngle(outtake, Constants.MechanismConstants.defaultHoodAngle)
                    )
            );
    }
}