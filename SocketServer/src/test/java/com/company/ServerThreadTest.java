package com.company;

import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class testing ServerThread
 */
public class ServerThreadTest {

    @Test
    public void constructor() {
        int port = 8987;
        Server server = new Server(port);
        ServerThread serverThread = new ServerThread(Server.serverLogger, server);

        assertEquals(Server.serverLogger, serverThread.getStLogger());
        assertEquals(server, serverThread.getServer());
        assertFalse(serverThread.isIfClose());
    }
}
