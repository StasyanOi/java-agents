package com;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        long applicationPid = ProcessHandle.allProcesses()
                .filter(processHandle -> processHandle.info()
                        .commandLine()
                        .orElse("")
                        .contains("application-SNAPSHOT-1.0.jar"))
                .mapToLong(ProcessHandle::pid)
                .findAny()
                .orElse(-1);

        if (applicationPid == -1) {
            throw new IllegalStateException("No proper process found");
        }

        VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(applicationPid));

        virtualMachine.loadAgent("../dynamic-java-agent/agent-jar/target/agent-jar-1.0.jar");
    }
}
