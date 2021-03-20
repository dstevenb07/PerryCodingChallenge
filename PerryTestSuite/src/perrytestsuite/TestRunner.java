package perrytestsuite;

import org.junit.runner.Computer;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
//import org.junit.runners.AllTests;

import perrytestsuite.TestSuite;


public class TestRunner {
	
	public static void main(String[] args) {
		
		/*
		Computer computer = new Computer();

		JUnitCore jUnitCore = new JUnitCore();
		Result result = jUnitCore.run(computer, TestSuite.class);
		
		System.out.println("Running Junit Test Suite.");
        //JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        
        System.out.println("Successful: " + result.wasSuccessful() +
            " ran " + result.getRunCount() + " tests");
		*/
		
        System.out.println("Running Junit Test Suite.");
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println("Successful: " + result.wasSuccessful() +
            " ran " + result.getRunCount() + " tests");
        
		//print out successes & failures
		
		//suppress warning
        
        //add print progress statements in each test case
    }
}
