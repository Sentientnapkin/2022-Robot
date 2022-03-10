package frc.robot.commands.outtake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.lib.controllers.FightStick;
import frc.robot.subsystems.OuttakeSubsystem;


public class ManualAdjustHoodAngle extends CommandBase {
    private final OuttakeSubsystem outtakeSubsystem;

    public ManualAdjustHoodAngle(OuttakeSubsystem outtakeSubsystem) {
        this.outtakeSubsystem = outtakeSubsystem;
        addRequirements(this.outtakeSubsystem);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        // HOOD ANGLE LINEAR SERVOS
        if (FightStick.fightStickJoystick.getY() < 0) { // Inverted
            outtakeSubsystem.manualAdjustHoodAngle(1);
        } else if (FightStick.fightStickJoystick.getY() > 0) {
            outtakeSubsystem.manualAdjustHoodAngle(-1);
        } else {

        }
    }

    @Override
    public boolean isFinished() { return false; } //TODO CHANGE

    @Override
    public void end(boolean interrupted) { }
}
