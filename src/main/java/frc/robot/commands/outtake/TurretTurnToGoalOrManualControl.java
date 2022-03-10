package frc.robot.commands.outtake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.lib.GoalNotFoundException;
import frc.robot.lib.controllers.FightStick;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.OuttakeSubsystem;

import static frc.robot.Constants.MechanismConstants.minimumTurretAngle;
import static frc.robot.Constants.MechanismConstants.maximumTurretAngle;
import static frc.robot.Constants.MechanismConstants.turretTurnSpeed;


public class TurretTurnToGoalOrManualControl extends CommandBase {
    private final OuttakeSubsystem outtakeSubsystem;
    private final LimelightSubsystem limelightSubsystem;
    private final double hardstopDeadzoneBuffer = 10;
    private final double hardstopMidpoint = (maximumTurretAngle + minimumTurretAngle) / 2;
    private long hardstopToggleCountdown = Long.MAX_VALUE;
    private double lastVelocity = 0;

    public TurretTurnToGoalOrManualControl(OuttakeSubsystem outtakeSubsystem, LimelightSubsystem limelightSubsystem) {
        this.outtakeSubsystem = outtakeSubsystem;
        this.limelightSubsystem = limelightSubsystem;
        addRequirements(this.outtakeSubsystem, this.limelightSubsystem);
    }

    @Override
    public void initialize() {
        hardstopToggleCountdown = Long.MAX_VALUE;
        lastVelocity = 0;
    }

    @Override
    public void execute() {
        if (FightStick.fightStickJoystick.getX() < -0.75) {
            outtakeSubsystem.turnTurret(-turretTurnSpeed);
            lastVelocity = 0;
        } else if (FightStick.fightStickJoystick.getX() > 0.75) {
            outtakeSubsystem.turnTurret(turretTurnSpeed);
            lastVelocity = 0;
        } else if (System.currentTimeMillis() - hardstopToggleCountdown > 1000) {
            new TurretTurnToAngle(outtakeSubsystem, outtakeSubsystem.getTurretPosition() > hardstopMidpoint ? minimumTurretAngle + 20 : maximumTurretAngle - 20).schedule();
        } else if (limelightSubsystem.isTargetFound()) {
            try {
                double goalOffset = limelightSubsystem.getLimelightOutputAtIndex(1);
                lastVelocity = -outtakeSubsystem.turretAnglePID.calculate(goalOffset);
                outtakeSubsystem.turnTurret(lastVelocity);

                if (outtakeSubsystem.getTurretPosition() > maximumTurretAngle - hardstopDeadzoneBuffer && goalOffset > hardstopDeadzoneBuffer
                        || outtakeSubsystem.getTurretPosition() < minimumTurretAngle + hardstopDeadzoneBuffer && goalOffset < hardstopDeadzoneBuffer) {
                    hardstopToggleCountdown = System.currentTimeMillis();
                } else {
                    hardstopToggleCountdown = Long.MAX_VALUE;
                }
            } catch (GoalNotFoundException ignored) {}
        } else {
            outtakeSubsystem.turnTurret(lastVelocity);
            hardstopToggleCountdown = Long.MAX_VALUE;
            lastVelocity += lastVelocity > 0 ? -0.003 : 0.003;
        }
    }

    @Override
    public void end(boolean interrupted) {
        outtakeSubsystem.turnTurret(0);
    }
}
