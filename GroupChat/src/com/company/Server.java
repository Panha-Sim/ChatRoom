package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ServerSocket server;
    private ExecutorService pool;
    private ArrayList<ClientHandler> Connections;

    @Override
    public void run() {


        try {
            Connections = new ArrayList<ClientHandler>();
            server = new ServerSocket(9992);
            pool = Executors.newCachedThreadPool();

            while (true) {

                Socket client = server.accept();
                ClientHandler CH = new ClientHandler(client);
                Connections.add(CH);
                pool.execute(CH);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void broadcast(String msg) {
        for (ClientHandler CH : Connections) {
            CH.sendMessage(msg);
        }
    }

    public class ClientHandler implements Runnable {

        private PrintWriter out;
        private BufferedReader in;
        private Socket client;
        private String name;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {

                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                out.println("welcome, Please Enter your name:");
                name = in.readLine();
                System.out.println(name + " Connected");
                broadcast(name + " joined the chat");
                String InMsg;

                //keep Receiving Input from client
                while ((InMsg = in.readLine()) != null) {

                    if (InMsg.equals("/quit")) {
                        broadcast(name + " left the chat");
                        System.out.println(name + " left the chat");
                        client.close();
                    } else {
                        //work on broadcast
                        broadcast(name + ": " + InMsg);
                    }

                }

            } catch (IOException e) {
                shutDown();
            }
        }

        public void shutDown() {
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String msg) {
            out.println(msg);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();

    }
}
