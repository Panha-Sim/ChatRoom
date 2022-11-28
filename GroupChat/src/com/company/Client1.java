package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 implements Runnable {
    private boolean online;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public Client1() {
        online = true;
    }

    @Override
    public void run() {
        try {

            client = new Socket("localhost", 9992);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            InputHandler IH = new InputHandler();
            Thread t = new Thread(IH);
            t.start();

            //Listen for Message
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        online = false;

        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class InputHandler implements Runnable {
        @Override
        public void run() {
            try {

                //Output message
                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                while (online) {
                    String message = r.readLine();

                    if (!message.equals("/quit")) {
                        out.println(message);
                    } else {
                        out.println(message);
                        r.close();
                        shutdown();
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        Client c = new Client();
        c.run();

    }
}
