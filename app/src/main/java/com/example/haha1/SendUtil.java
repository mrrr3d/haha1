package com.example.haha1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SendUtil {
    public static void sendMsgTcp (String msg, String ip, int port) throws IOException {
        Socket socket = new Socket();
        SocketAddress socAddress = new InetSocketAddress(ip, port);
        socket.connect(socAddress, 5000);
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(msg);
        pw.flush();
        socket.shutdownOutput();
        socket.close();
    }

    public static void sendMsgUdp (String msg, String ip, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(ip);
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, port);
        socket.send(packet);
    }
}
