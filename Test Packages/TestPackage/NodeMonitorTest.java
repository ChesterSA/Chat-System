/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import middleware.NewAgent;
import middleware.NewPortal;
import org.junit.Test;

/**
 *
 * @author s6089488
 */
public class NodeMonitorTest
{

    NewPortal p;

    @Test
    public void NodeMonitorTests()
    {
        NewPortal p = new NewPortal("portal");
        p.addNodeMonitor();
        
        NewAgent a1 = new NewAgent("agentone", p);
        a1.addNodeMonitor();
        
        NewAgent a2 = new NewAgent("agenttwo", p);
        a2.addNodeMonitor();
        
        a1.sendMessage("agenttwo", "test message");
        a2.sendMessage("agentone", "response message");
    }
}
