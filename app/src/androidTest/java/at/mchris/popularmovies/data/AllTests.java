package at.mchris.popularmovies.data;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;


public class AllTests {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class)
                .includeAllPackagesUnderHere().build();
    }
}