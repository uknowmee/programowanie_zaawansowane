package com.company;

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

    @Test
    public void constructor() {
        int port = 2001;
        Server server = new Server(port);
        ServerThread serverThread = new ServerThread(Server.serverLogger, server);

        assertEquals(Server.serverLogger, serverThread.getStLogger());
        assertEquals(server, serverThread.getServer());
        assertFalse(serverThread.isIfClose());
    }

    @Test
    public void actionHelp() {
        int port = 2002;
        Server server = new Server(port);
        ServerThread serverThread = new ServerThread(Server.serverLogger, server);

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
    public void actionShowUsers() throws IOException {
        String inDeck = ", in deck: ";
        int port = 2003;
        Server server = new Server(port);

        RawConnectionTest  rawConnectionTest = new RawConnectionTest(port);
        ServerSocket serverSocket = new ServerSocket(port);
        rawConnectionTest.start();

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
                newUserThread.action(new Server.Split("\\showusers")));

        Socket socket1 = serverSocket.accept();

        String name1 = "wojtek";
        UserThread newUser1 = new UserThread(socket1, server, Server.serverLogger);
        OutputStream output1 = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        Socket socket2 = serverSocket.accept();

        String name2 = "ola";
        UserThread newUser2 = new UserThread(socket2, server, Server.serverLogger);
        OutputStream output2 = socket.getOutputStream();
        newUser.setWriter(new PrintWriter(output2, true));
        server.addUserThread(newUser2);
        server.addUser(newUser2);
        server.addUserName(name2, newUser2);

        String user =  name + inDeck + "" + ", " + newUser;
        String user1 = name1 + inDeck + "" + ", " + newUser1;
        String user2 = name2 + inDeck + "" + ", " + newUser2;

        assertTrue(newUserThread.action(new Server.Split("\\showusers")).contains(user));
        assertTrue(newUserThread.action(new Server.Split("\\showusers")).contains(user1));
        assertTrue(newUserThread.action(new Server.Split("\\showusers")).contains(user2));


        server.removeUser("michal", newUser);
        server.removeUser("wojtek", newUser1);
        server.removeUser("ola", newUser2);

        socket.close();
        socket1.close();
        socket2.close();
    }

    @Test
    public void showDecks() {

    }

    @Test
    public void msgAll() throws IOException {
        int port = 2004;
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

        String name1 = "ola";
        UserThread newUser1 = new UserThread(socket, server, Server.serverLogger);
        OutputStream output1 = socket.getOutputStream();
        newUser1.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall czesc")));
        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall")));

        server.removeUser("michal", newUser);
        server.removeUser("ola", newUser1);

        socket.close();
    }

    @Test
    public void defaultAction() throws IOException {
        int port = 2005;
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

        assertEquals("unknown command!", serverThread.action(new Server.Split("\\dasda")));
        assertEquals("[SERVER]: siema", serverThread.action(new Server.Split("\\michal siema")));

        server.removeUser("michal", newUser);
        socket.close();
    }
}
