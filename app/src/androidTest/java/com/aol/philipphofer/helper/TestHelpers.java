package com.aol.philipphofer.helper;

public class TestHelpers {

    public static void wait(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void waitForView() {
        wait(1000);
    }

    public static int generateFalseNumber(int solution) {
        return solution == 9 ? 8 : solution + 1;
    }
}
