package com.virtusa;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] arg) {

        String YOUR_COMMAND = "GET /";
        String YOUR_USER = "phil";
        String YOUR_PASS = "xxxxxxxxxxx";
        String YOUR_HOST = "monolith";
        try {
            System.out.println(telnetConnection(YOUR_COMMAND, YOUR_USER, YOUR_PASS, YOUR_HOST));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String telnetConnection(String command, String user, String password, String host) throws JSchException, Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        // It must not be recommended, but if you want to skip host-key check,
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect(3000);
        //session.connect(30000);   // making a connection with timeout.

        Channel channel = session.openChannel("shell");

        channel.connect(3000);

        DataInputStream dataIn = new DataInputStream(channel.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataIn));
        DataOutputStream dataOut = new DataOutputStream(channel.getOutputStream());

        System.out.println("Starting telnet connection...");
        dataOut.writeBytes("telnet localhost 80\r\n");
//      dataOut.writeBytes("enable\r\n");
        dataOut.writeBytes(command + "\r\n");
        System.out.println("DEBUG1");
        dataOut.writeBytes("exit\r\n"); //exit from telnet
        System.out.println("DEBUG2");
        dataOut.writeBytes("exit\r\n"); //exit from shell
        System.out.println("DEBUG3");
        dataOut.flush();

        String line = reader.readLine();
        String result = line + "\n";

        while (!(line = reader.readLine()).equals("Connection closed by foreign host.")) {
            System.out.println("I READ: " + line);
            result += line + "\n";
        }

        /* better would be
        while (!(line = reader.readLine()).startsWith("Connection closed by foreign host")) {
            System.out.println("I READ: " + line);
            result += line + "\n";
        } */

        dataIn.close();
        dataOut.close();
        channel.disconnect();
        session.disconnect();

        return result;

    }

}