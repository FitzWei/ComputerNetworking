package com.fitz.socketDemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;

public class UDPSender {
    private DatagramSocket senderSocket;
    private DatagramPacket dataPacket;
    private File file;
    private FileInputStream fileInputStream;
    private byte[] msg;
    private int portNum;
    private InetAddress IPAddress;


    public void sendFile( String path ) throws IOException {
        // Init stuff
        file = new File(path);
        fileInputStream = new FileInputStream(file);

        String fileName = file.getName();
        long fileLength = fileInputStream.available();
        System.out.println("The length of this file is " + fileLength + " bytes");


        // 1. send the file name and the length to receiver
        byte[] sendData = (fileName + "::" + fileLength).getBytes();
        // Create a datagram with data-to-send, length, Ip addr, port
        dataPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNum);
        // Send datagram to receiver
        senderSocket.send(dataPacket);

        // 2. receive the OK packet
        byte[] replyData = new byte[8092];
        DatagramPacket replyPacket = new DatagramPacket(replyData,replyData.length);
        senderSocket.receive(replyPacket);

        // Check the reply content
        if( new String(replyPacket.getData(), 0, replyPacket.getLength()).equals("OK"))
        {
            // 3. send the data to the receiver
            long cursor = 0;
            int bytesRead = 0;
            while(cursor < fileLength)
            {
                System.out.println(cursor);
                bytesRead = fileInputStream.read(msg);
                dataPacket = new DatagramPacket(msg,msg.length,IPAddress,portNum);
                senderSocket.send(dataPacket);
                cursor += bytesRead;
            }
        }
        else
        {
            System.out.println("The receiver did not confirm this transmission");
        }
    }

    public static void main(String[] args) throws IOException {

        UDPSender udpSender = new UDPSender();
        udpSender.senderSocket = new DatagramSocket();
        udpSender.msg = new byte[8192];
        // Set the IP addr and port of destination
        udpSender.IPAddress = InetAddress.getByName("localhost");
        udpSender.portNum = 51200;
        udpSender.senderSocket.connect(udpSender.IPAddress, udpSender.portNum);

        String filePath = "src/com/fitz/socket/vedio1.mkv";
        long startTime = System.currentTimeMillis();
        udpSender.sendFile(filePath);
        long endTime = System.currentTimeMillis();
        System.out.println("This transmission which is based on UDP cost " + (endTime-startTime) + " mills");
    }



}
