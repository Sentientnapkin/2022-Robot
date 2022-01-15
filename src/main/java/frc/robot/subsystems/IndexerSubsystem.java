package frc.robot.subsystems;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.MechanismConstants.IndexerMecanumMotorPort;

public class IndexerSubsystem extends SubsystemBase {

    private final TalonFX indexerMotor = new TalonFX(IndexerMecanumMotorPort);
    public boolean isRunning = false;

    public IndexerSubsystem() {

    }

    public void startIndexer() {
        indexerMotor.set(ControlMode.PercentOutput, 70);
        System.out.println("start indexer");
        isRunning = true;
    }

    public void stopIndexer() {
        indexerMotor.set(ControlMode.PercentOutput, 0);
        System.out.println("stop indexer");
        isRunning = false;
    }

    public void toggleIndexer() {
        if (isRunning) {
            stopIndexer();
        } else {
            startIndexer();
        }
    }

    @Override
    public void periodic() {
    }
}

