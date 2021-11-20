package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Class testing ServerThread
 */
public class ServerThreadTest {
    int port = 1000;
    private Server server;

    private ServerThread serverThread;
    private ServerSocket serverSocket;

    private String name;
    private UserThread newUser;
    private Socket socket;
    private String user;

    private String name1;
    private UserThread newUser1;
    private Socket socket1;
    private String user1;

    private String name2;
    private UserThread newUser2;
    private Socket socket2;
    private String user2;


    @Before
    public void setUp() throws IOException {
        String inDeck = ", in deck: ";

        this.port = 2001;
        this.server = new Server(port);
        this.serverThread = new ServerThread(Server.serverLogger, server);

        RawConnectionTest rawConnectionTest = new RawConnectionTest(port);
        this.serverSocket = new ServerSocket(port);
        rawConnectionTest.start();

        this.socket = serverSocket.accept();
        this.name = "michal";
        this.newUser = new UserThread(socket, server, Server.serverLogger);
        this.serverThread = new ServerThread(Server.serverLogger, server);
        OutputStream output = socket.getOutputStream();
        this.newUser.setWriter(new PrintWriter(output, true));
        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        this.socket1 = serverSocket.accept();
        this.name1 = "wojtek";
        this.newUser1 = new UserThread(socket1, server, Server.serverLogger);
        OutputStream output1 = socket.getOutputStream();
        newUser1.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        this.socket2 = serverSocket.accept();

        this.name2 = "ola";
        this.newUser2 = new UserThread(socket2, server, Server.serverLogger);
        OutputStream output2 = socket.getOutputStream();
        newUser2.setWriter(new PrintWriter(output2, true));
        server.addUserThread(newUser2);
        server.addUser(newUser2);
        server.addUserName(name2, newUser2);

        this.user = name + inDeck + "" + ", " + newUser;
        this.user1 = name1 + inDeck + "" + ", " + newUser1;
        this.user2 = name2 + inDeck + "" + ", " + newUser2;
    }

    @After
    public void tearDown() throws IOException {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);

        socket.close();
        socket1.close();
        socket2.close();

        serverSocket.close();
    }

    @Test
    public void constructor() {
        assertEquals(Server.serverLogger, serverThread.getStLogger());
        assertEquals(server, serverThread.getServer());
        assertFalse(serverThread.isIfClose());
    }

    @Test
    public void actionHelp() {
        assertEquals("""
                ###########################################################
                commands:\s
                \\help - print all commands
                \\showusers - print all users
                \\showdecks - print all running decks
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\CLOSE - exit
                ###########################################################""", serverThread.action(new Server.Split("\\help")));
    }

    @Test
    public void actionShowUsersEmpty() {
        String inDeck = ", in deck: ";

        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        assertEquals("[]", serverThread.action(new Server.Split("\\showusers")));
    }

    @Test
    public void actionShowUsersOne() {
        String inDeck = ", in deck: ";

        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);

        assertEquals("[" + name + inDeck + "" + ", " + newUser + "]",
                serverThread.action(new Server.Split("\\showusers")));
    }

    @Test
    public void actionShowUsersFew() {
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user));
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user1));
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user2));
    }

    @Test
    public void showDecks() {

    }

    @Test
    public void msgAll() {
        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall czesc")));
        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall")));
    }

    @Test
    public void defaultAction() {
        assertEquals("unknown command!", serverThread.action(new Server.Split("\\dasda")));
        assertEquals("[SERVER]: siema", serverThread.action(new Server.Split("\\michal siema")));
    }
}
