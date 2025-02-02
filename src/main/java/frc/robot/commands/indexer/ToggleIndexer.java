package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class ToggleIndexer extends CommandBase {
    // Setup indexer subsystem
    private final IndexerSubsystem indexerSubsystem;

    public ToggleIndexer(IndexerSubsystem indexerSubsystem) {
        this.indexerSubsystem = indexerSubsystem;
        addRequirements(this.indexerSubsystem);
    }

    @Override
    public void initialize() { // On initialize, toggle the indexer and its belt
        indexerSubsystem.toggleIndexer();
        indexerSubsystem.toggleBelt();
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void end(boolean interrupted) {}
}
