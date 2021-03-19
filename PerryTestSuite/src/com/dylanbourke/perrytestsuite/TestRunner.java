package com.dylanbourke.perrytestsuite;

import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
//import org.junit.runners.AllTests;

public class TestRunner {
	public static void main(String[] args) {
		
		Computer computer = new Computer();

		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.run(computer, TestSuite.class);
    }
}
