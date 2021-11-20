package com.company;


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

    @Test
    public void constructor() {
        int port = 3001;
        Server server = new Server(port);
        UserThread userThread = new UserThread(null, server, Server.serverLogger);

        assertEquals(Server.serverLogger, userThread.getUtLogger());
        assertEquals(server, userThread.getServer());
        assertNull(userThread.getSocket());

    }

    @Test
    public void actionHelp() throws IOException {
        int port = 3002;
        Server server = new Server(port);

        RawConnectionTest  rawConnectionTest = new RawConnectionTest(port);
        rawConnectionTest.start();

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();

        ServerThread serverThread = new ServerThread(Server.serverLogger, server);

        String name = "michal";
        UserThread newUser = new UserThread(socket, server, Server.serverLogger);
        OutputStream output = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output, true));
        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

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

        server.removeUser("michal", newUser);
        socket.close();
    }

    @Test
    public void actionShowUsers() throws IOException {
        String inDeck = ", in deck: ";
        int port = 3003;
        Server server = new Server(port);

        RawConnectionTest  rawConnectionTest = new RawConnectionTest(port);
        rawConnectionTest.start();

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();

        String name = "michal";
        UserThread newUser = new UserThread(socket, server, Server.serverLogger);
        ServerThread newUserThread = new ServerThread(Server.serverLogger, server);
        OutputStream output = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output, true));

        assertEquals("[]", newUserThread.action(new Server.Split("\\showusers")));

        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        assertEquals("[" + name + inDeck + "" + ", " + newUser + "]",
                newUser.action(newUser.getName(), "\\showusers"));

        Socket socket1 = serverSocket.accept();

        String name1 = "wojtek";
        UserThread newUser1 = new UserThread(socket1, server, Server.serverLogger);
        ServerThread newUserThread1 = new ServerThread(Server.serverLogger, server);
        OutputStream output1 = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        Socket socket2 = serverSocket.accept();

        String name2 = "ola";
        UserThread newUser2 = new UserThread(socket2, server, Server.serverLogger);
        ServerThread newUserThread2 = new ServerThread(Server.serverLogger, server);
        OutputStream output2 = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output2, true));
        server.addUserThread(newUser2);
        server.addUser(newUser2);
        server.addUserName(name2, newUser2);

        String user =  name + inDeck + "";
        String user1 = name1 + inDeck + "";
        String user2 = name2 + inDeck + "";

        assertTrue(newUser.action("michal","\\showusers").contains(user));
        assertTrue(newUser.action("wojtek","\\showusers").contains(user1));
        assertTrue(newUser.action("ola","\\showusers").contains(user2));

        server.removeUser("michal", newUser);
        server.removeUser("wojtek", newUser1);
        server.removeUser("ola", newUser2);

        socket.close();
        socket1.close();
        socket2.close();

    }

    @Test
    public void actionShowDecks() {

    }

    @Test
    public void actionAddDeck() throws IOException {
        String inDeck = ", in deck: ";
        int port = 3004;
        Server server = new Server(port);

        RawConnectionTest  rawConnectionTest = new RawConnectionTest(port);
        rawConnectionTest.start();

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();

        String name = "michal";
        UserThread newUser = new UserThread(socket, server, Server.serverLogger);
        ServerThread newUserThread = new ServerThread(Server.serverLogger, server);
        OutputStream output = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output, true));
        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        Socket socket1 = serverSocket.accept();

        String name1 = "wojtek";
        UserThread newUser1 = new UserThread(socket1, server, Server.serverLogger);
        ServerThread newUserThread1 = new ServerThread(Server.serverLogger, server);
        OutputStream output1 = socket.getOutputStream();
        newUser1.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        Socket socket2 = serverSocket.accept();

        String name2 = "ola";
        UserThread newUser2 = new UserThread(socket2, server, Server.serverLogger);
        ServerThread newUserThread2 = new ServerThread(Server.serverLogger, server);
        OutputStream output2 = socket.getOutputStream();
        newUser2.setWriter(new PrintWriter(output2, true));
        server.addUserThread(newUser2);
        server.addUser(newUser2);
        server.addUserName(name2, newUser2);

        Socket socket3 = serverSocket.accept();

        String name3 = "kacper";
        UserThread newUser3 = new UserThread(socket3, server, Server.serverLogger);
        ServerThread newUserThread3 = new ServerThread(Server.serverLogger, server);
        OutputStream output3 = socket.getOutputStream();
        newUser3.setWriter(new PrintWriter(output3, true));
        server.addUserThread(newUser3);
        server.addUser(newUser3);
        server.addUserName(name3, newUser3);

        assertEquals("Invalid deck name or number of players",newUser.action("michal", "\\adddeck hihi "));

        assertEquals("You have created a deck named: hihi for: 3 players",newUser.action("michal","\\adddeck hihi 3"));
        assertEquals("unknown command!",newUser.action("michal","\\addDeck hihi 2"));

        assertEquals("unknown command!", newUser1.action("wojtek","\\adddeck hihi 2"));
        assertEquals("unknown command!", newUser1.action("wojtek","\\joindeck"));
        assertEquals("You have joined a deck named: hihi",newUser1.action("wojtek","\\joindeck hihi"));
        assertEquals("you already are in deck",newUser1.action("wojtek","\\joindeck hihi"));
        assertEquals("You have joined a deck named: hihi", newUser2.action("ola","\\joindeck hihi"));
        assertEquals("unknown command!", newUser3.action("kacper","\\joindeck hihi"));
        assertEquals("unknown command!", newUser3.action("kacper","\\adddeck hihi"));
        assertEquals("You have created a deck named: hihig for: 3 players", newUser3.action("kacper","\\adddeck hihig 3"));
        assertEquals(2, server.getDecks().size());
        assertEquals("You have left a deck named: hihig", newUser3.action("kacper","\\leavedeck"));
        assertEquals("you are already not in deck", newUser3.action("kacper","\\leavedeck"));

        assertEquals("You have left a deck named: hihi", newUser3.action("michal","\\leavedeck"));
        assertEquals("You have left a deck named: hihi", newUser3.action("ola","\\leavedeck"));
        assertEquals("You have left a deck named: hihi", newUser3.action("wojtek","\\leavedeck"));

        assertEquals(0, server.getDecks().size());

        server.removeUser("michal", newUser);
        server.removeUser("wojtek", newUser1);
        server.removeUser("ola", newUser2);
        server.removeUser("kacper", newUser3);

        socket.close();
        socket1.close();
        socket2.close();
        socket3.close();

    }

    @Test
    public void actionMsgAll() {

    }

}
