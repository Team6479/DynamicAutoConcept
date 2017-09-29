package org.usfirst.frc.team6479.robot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import edu.wpi.first.wpilibj.SpeedController;

public class AutoLogger {
	
	private SpeedController[] allMotorControllers;
	private PrintWriter write;
	private FileReader read;
	public AutoLogger(SpeedController... allMotorControllers) {
		this.allMotorControllers = allMotorControllers;
	}
	//return true if succsefull
	public boolean open() {
		try 
		{
			write = new PrintWriter(Robot.AUTO_FILE);
			read = new FileReader(Robot.AUTO_FILE);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//return true if succsefull
	public boolean close() {
		try 
		{
			write.close();
			read.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//called every cycle, logs the speed controllers values and what speed controller it came from
	public void log() {
		for(SpeedController sc: allMotorControllers) {
			//get the speed
			double speed = sc.get();
			//write the speed to the file
			write.print("[" + speed + "]");
		}
		//append a \n to denote end of this cycle
		write.print("\n");
	}
	
	//called every cycle, reads the log and gets the value of the speed controller and sets the speed controller to it
	//returns false when end of file is reached
	public boolean run() {
		//store read in speeds here
		String input = "";
		boolean endOfCycle = false;
		while(!endOfCycle) {
			int next;
			try {
				next = read.read();
			} catch (IOException e) {
				e.printStackTrace();
				//if there was a IO error, end auto routine immediately
				return false;
			}
			//10 is ascii for \n
			//if next char is a new line, end the cycle and calculate speed, otherwise keep adding the charcters to input
			if(next == 10) {
				endOfCycle = true;
			}
			//if end of file, return false
			else if(next == -1)	{
				return false;
			}
			else {
				input += (char)next;
			}
		}
		
		//compute the speeds
		//the string is in format [s1][s2]...[sN]
		//remove the ending brackets
		input = input.substring(1, input.length() - 1);
		//split the string for charcter arrangment ][
		String[] speeds = input.split("][");
		
		//loop through each speed and speed controller and set the speed controler to the speed
		for(int i = 0; i < speeds.length && i < allMotorControllers.length; i++) {
			allMotorControllers[i].set(Double.parseDouble(speeds[i]));
		}
		
		return true;
	}
	
	//when auto is done, Robot will tell robot to stop, do this by setting all speed controllers to zero
	public void stop() {
		for(SpeedController sc: allMotorControllers) {
			sc.set(0);
		}
	}
}
