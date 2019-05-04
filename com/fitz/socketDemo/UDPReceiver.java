package com.fitz.socketDemo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.StringTokenizer;

public class UDPReceiver {
    private DatagramSocket serverSocket;
    private String initString, filename;
    private DatagramPacket initPacket, receivedPacket;
    private FileOutputStream fileOutputStream;
    private byte[] receivedData;
    private long bytesReveived, bytesToReceive;

    public UDPReceiver(int portNum ) throws IOException {
        // Create datagram socket at port the given portNum
        serverSocket = new DatagramSocket(portNum);
        System.out.println("====Ready to receive file at port 51200====");

        receivedData = new byte[8192];
    }
    public void ReceiveFile() throws IOException {

        // 1. wait for the sender to transmit the fileName and the length of file
        initPacket = new DatagramPacket(receivedData,receivedData.length);
        serverSocket.receive(initPacket);

        initString = "Rece(UDP)-" + new String(initPacket.getData(), 0, initPacket.getLength());
        StringTokenizer stringTokenizer = new StringTokenizer(initString,"::");

        filename = stringTokenizer.nextToken();
        bytesToReceive = Long.parseLong(((stringTokenizer.nextToken())));
        System.out.println("==The file will be saved as " + filename + "==");
        System.out.println("==The length of this file is"+bytesToReceive+"==");

        // 2. send 'OK' to sender
        byte[] ok = ("OK").getBytes();
        DatagramPacket OKPacket = new DatagramPacket(ok, ok.length, initPacket.getAddress(), initPacket.getPort());
        serverSocket.send(OKPacket);

        // 3. receive the data of the file
        fileOutputStream = new FileOutputStream(filename);
        while( bytesReveived < bytesToReceive )
        {
            receivedPacket = new DatagramPacket(receivedData, receivedData.length);
            serverSocket.receive(receivedPacket);
            fileOutputStream.write(receivedPacket.getData(), 0, receivedPacket.getLength());
            bytesReveived += receivedPacket.getLength();
        }

        System.out.println("====File transmission completed!====");

    }

    public static void main(String[] args) throws IOException {
        UDPReceiver udpReceiver = new UDPReceiver(51200);
        long startTime = System.currentTimeMillis();
        udpReceiver.ReceiveFile();
        long endTime = System.currentTimeMillis();
        System.out.println("This transmission is based on UDP and it costs " + (endTime-startTime) + " ms");
    }
}
