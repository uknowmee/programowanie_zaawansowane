package com.company;


import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Class testing UserThread
 */
public class UserThreadTest {

    @Test
    public void constructor() {
        int port = 8987;
        Server server = new Server(port);
        UserThread userThread = new UserThread(null, server, Server.serverLogger);

        assertEquals(Server.serverLogger, userThread.getUtLogger());
        assertEquals(server, userThread.getServer());
        assertNull(userThread.getSocket());

    }
}
