package perrytestsuite;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
//import org.junit.runners.AllTests;

import perrytestsuite.TestSuite;

public class TestRunner {
	
	public static void main(String[] args) {
        System.out.println("Running Junit Test Suite. . .");
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString()+"\n");
        }
        System.out.println("Tests Complete: TestSuite" +
            " ran " + result.getRunCount() + " tests\n");
    }
}
