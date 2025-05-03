// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

import java.io.File;

import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import java.io.*;
import java.util.*;

public class Robot extends LoggedRobot {
  public final String FRAME_DATA_PATH = "C:\\Users\\Jasee\\Stuff\\bad-apple-ascope\\framedata.txt";
  public final double VIDEO_HEIGHT = 3.5;
  int curFrame = 0;
  ArrayList<ArrayList<Pose3d>> videoData;
  Timer t = new Timer();

  /** Called once at the beginning of the robot program. */
  public Robot() {

    //To run the sim at 30fps
    super(1);

    Logger.recordMetadata("Bad-Apple-Ascope", "WIP");

    if (isReal()) {
      Logger.addDataReceiver(new WPILOGWriter()); // Log to a USB stick ("/U/logs")
      Logger.addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
    } else if (isSimulation()) {
      Logger.addDataReceiver(new NT4Publisher());
    } else {
      setUseTiming(false); // Run as fast as possible
      String logPath = LogFileUtil.findReplayLog(); // Pull the replay log from AdvantageScope (or prompt the user)
      Logger.setReplaySource(new WPILOGReader(logPath)); // Read replay log
      Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))); // Save outputs to a new log
    }
  
    Logger.start();
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    curFrame = 0;

    videoData = new ArrayList<>();

    try {
      Scanner s = new Scanner(new File(FRAME_DATA_PATH));
      int cur = 0;
      ArrayList<Pose3d> arr = new ArrayList<>();

      while(s.hasNextLine()) {
        String line = s.nextLine().trim();

        if(line.contains("BREAK")) {
          videoData.add(new ArrayList<>(arr));
          arr.clear();

          System.out.println("Done processing frame " + cur);
        } else {

          String[] parts = line.split(",");
          double x = Double.parseDouble(parts[0]);
          double y = Double.parseDouble(parts[1]);
  
          Pose3d pose = new Pose3d(new Translation3d(x, y, VIDEO_HEIGHT), new Rotation3d());
          arr.add(pose);
          //System.out.println(pose);
          
          cur++;

        }
      }

    } catch (Exception e) {
      System.out.println("An error occurred");
      e.printStackTrace();
    }

    System.out.println("Init done");
    for(int i = 0; i < videoData.size(); i++) {
      //System.out.println(videoData.get(i));
    }
    t.start();

  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    if (curFrame < videoData.size()) {

      System.out.println("Frame #" + curFrame);
      ArrayList<Pose3d> frameData = videoData.get(curFrame);

      Pose3d[] arr = new Pose3d[frameData.size()];
      frameData.toArray(arr);

      Logger.recordOutput("test", arr);
      curFrame++;
    } else {
      //System.out.println(t.get());
    }
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
