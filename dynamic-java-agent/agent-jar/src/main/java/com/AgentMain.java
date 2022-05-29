package com;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("Dynamic agent attached");
    }
}
