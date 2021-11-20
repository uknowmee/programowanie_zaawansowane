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
 * Class testing UserThread
 */
public class UserThreadTest {
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

    private String name3;
    private UserThread newUser3;
    private Socket socket3;
    private String user3;


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

        this.socket3 = serverSocket.accept();
        this.name3 = "kacper";
        this.newUser3 = new UserThread(socket3, server, Server.serverLogger);
        OutputStream output3 = socket.getOutputStream();
        newUser3.setWriter(new PrintWriter(output3, true));
        server.addUserThread(newUser3);
        server.addUser(newUser3);
        server.addUserName(name3, newUser3);

        this.user = name + inDeck + "";
        this.user1 = name1 + inDeck + "";
        this.user2 = name2 + inDeck + "";
        this.user3 = name3 + inDeck + "";
    }

    @After
    public void tearDown() throws IOException {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        socket.close();
        socket1.close();
        socket2.close();
        socket3.close();

        serverSocket.close();
    }

    @Test
    public void constructor() {
        assertEquals(Server.serverLogger, newUser.getUtLogger());
        assertEquals(server, newUser.getServer());
        assertEquals(socket, newUser.getSocket());
    }

    @Test
    public void actionHelp() {
        assertEquals("""
                ###########################################################
                commands:\s
                \\help - print all commands
                \\showusers - print all users
                \\showdecks - print all running decks
                \\adddeck <nameOfDeck> <numberOfPlayers>
                \\joindeck <nameOfDeck>
                \\leavedeck
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\bye - exit
                ###########################################################
                """, newUser.action(newUser.getName(), "\\help"));
    }

    @Test
    public void actionShowUsersEmpty() {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);
        assertEquals("[]", newUser.action(name, "\\showusers"));
    }

    @Test
    public void actionShowUsersOne() {
        String inDeck = ", in deck: ";

        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        assertEquals("[" + name + inDeck + "" + ", " + newUser + "]",
                newUser.action(newUser.getName(), "\\showusers"));
    }

    @Test
    public void actionShowUsersFew() {
        assertTrue(newUser.action(name, "\\showusers").contains(user));
        assertTrue(newUser1.action(name1, "\\showusers").contains(user1));
        assertTrue(newUser2.action(name2, "\\showusers").contains(user2));
    }


    @Test
    public void actionShowDecks() {

    }

    @Test
    public void defaultActionUnknownCommand() {
        assertEquals("unknown command!", newUser.action(name, "\\" + name));
        assertEquals("unknown command!", newUser.action(name, "asd"));
        assertEquals("unknown command!", newUser.action(name, "asddasdgswf"));
    }

    @Test
    public void defaultActionOnlyOneUser() {
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser3);
        server.removeUser(name3, newUser3);

        assertEquals("unknown command!", newUser.action(name, "\\asda asd"));
        assertEquals("unknown command!", newUser.action(name, "\\asd"));
    }

    @Test
    public void defaultActionWhisper() {
        assertEquals(name + ": " + "asdasd", newUser.action(name, "\\" + name1 + " asdasd"));
    }

    @Test
    public void actionAddDeck() {
        assertEquals("Invalid deck name or number of players", newUser.action(name, "\\adddeck hihi "));

        assertEquals("You have created a deck named: hihi for: 3 players", newUser.action(name, "\\adddeck hihi 3"));
        assertEquals("unknown command!", newUser.action(name, "\\addDeck hihi 2"));
        assertEquals("unknown command!", newUser.action(name, "\\addDeck hihi"));

        assertEquals("unknown command!", newUser1.action(name1, "\\adddeck hihi 2"));
        assertEquals("unknown command!", newUser1.action(name1, "\\joindeck"));
        assertEquals("You have joined a deck named: hihi", newUser1.action(name1, "\\joindeck hihi"));
        assertEquals("you already are in deck", newUser1.action(name1, "\\joindeck hihi"));
        assertEquals("You have joined a deck named: hihi", newUser2.action(name2, "\\joindeck hihi"));
        assertEquals("unknown command!", newUser3.action(name3, "\\joindeck hihi"));
        assertEquals("unknown command!", newUser3.action(name3, "\\adddeck hihi"));
        assertEquals("You have created a deck named: hihig for: 3 players", newUser3.action(name3, "\\adddeck hihig 3"));
        assertEquals(2, server.getDecks().size());
        assertEquals("You have left a deck named: hihig", newUser3.action(name3, "\\leavedeck"));
        assertEquals("you are already not in deck", newUser3.action(name3, "\\leavedeck"));

        assertEquals("You have left a deck named: hihi", newUser3.action(name, "\\leavedeck"));
        assertEquals("You have left a deck named: hihi", newUser3.action(name1, "\\leavedeck"));
        assertEquals("You have left a deck named: hihi", newUser3.action(name2, "\\leavedeck"));

        assertEquals("You have created a deck named: hihi for: 2 players", newUser.action(name, "\\adddeck hihi 2"));
        assertEquals("You have left a deck named: hihi", newUser3.action(name, "\\leavedeck"));
        assertEquals("You have created a deck named: hihi for: 4 players", newUser.action(name, "\\adddeck hihi 4"));
        assertEquals("You have left a deck named: hihi", newUser3.action(name, "\\leavedeck"));

        assertEquals(0, server.getDecks().size());
    }

    @Test
    public void actionMsgAll() {
        assertEquals("messaged all", newUser.action(name, "\\msgall hello everybody"));
    }

}
