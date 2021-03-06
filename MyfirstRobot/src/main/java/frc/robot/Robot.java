/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.AutoTensionLift;
import frc.robot.subsystems.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    public static Wrist wrist;
    public static Chassis chassis;
    public static Climber climber;
    public static ClimberDriver climberDriver;
    public static Limelight limelight;
    public static IntakeTilter tilter;
    public static FondlerLift fondlerLift;
    public static OI m_oi;
    private Command m_autonomousCommand;
    public static BallIntake ballIntake;
    private SendableChooser<Command> m_chooser = new SendableChooser<>();

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        wrist = new Wrist();

        ballIntake = new BallIntake();

        // chooser.addOption("My Auto", new MyAutoCommand());
        chassis = new Chassis();
        climber = new Climber();
        climberDriver = new ClimberDriver();
        tilter = new IntakeTilter();
        limelight = new Limelight();
        fondlerLift = new FondlerLift();
        if (Config.USING_YOKE) {
            m_oi = new OI(new YokeInputManager());
        } else {
            m_oi = new OI(new JoystickInputManager());
        }

        m_chooser.setDefaultOption("Default Auto", null);

        SmartDashboard.putData("Auto mode", m_chooser);

        //initialize camera server
        CameraServer.getInstance().startAutomaticCapture().setResolution(640, 480);
    }

    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        Joystick joystick = m_oi.getJoystick(0);
        double driveInput = -joystick.getRawAxis(1);
        double turnInput = joystick.getRawAxis(0);
        double drive = Util.applyDeadband(Math.copySign(Math.pow(Math.abs(driveInput), 1.6), driveInput), 0.05),
                turn = Util.applyDeadband(Math.copySign(Math.pow(Math.abs(turnInput), 1.6), turnInput), 0.05) + 0.2 * joystick.getRawAxis(2);
        SmartDashboard.putNumber("Drive Input", drive);
        SmartDashboard.putNumber("Turn Input", turn);
    }

    /**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
     * the robot is disabled.
     */
    @Override
    public void disabledInit() {
        climber.resetEncoders();
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString code to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional commands to the
     * chooser code above (like the commented example) or additional comparisons
     * to the switch structure below with additional strings & commands.
     */
    @Override
    public void autonomousInit() {
        m_autonomousCommand = m_chooser.getSelected();

        /*
         * String autoSelected = SmartDashboard.getString("Auto Selector",
         * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
         * = new MyAutoCommand(); break; case "Default Auto": default:
         * autonomousCommand = new ExampleCommand(); break; }
         */

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.start();
        }
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        new AutoTensionLift().start();
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }
}
