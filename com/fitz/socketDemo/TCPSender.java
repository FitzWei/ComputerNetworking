package com.fitz.socketDemo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSender {
    private byte[] ReceiveWin; // the receive windows
    private byte[] fileBuffer;  // into which read from the file stream
    Socket clientSocket;    // connect to server
    DataInputStream inFromServer;    // the stream from server
    DataOutputStream outToServer;  // the stream to server

    public TCPSender(InetAddress IPAddress, int port) throws IOException {
        clientSocket = new Socket(IPAddress,port);
        System.out.println("Connection to server succeed! The server's address is: " + IPAddress + ":" + port);
        ReceiveWin = new byte[8192];
        fileBuffer = new byte[8192];
    }

    public void sendFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        String fileName = file.getName();
        long fileLength = fileInputStream.available();
        System.out.println("fileLength:" + fileLength);

        // 1. Create input stream output stream attached to socket
        inFromServer = new DataInputStream(clientSocket.getInputStream());
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        // 2. Send the information of the file to Receiver
        byte[] initMsg = (fileName + "::" + fileLength).getBytes();
        outToServer.write(initMsg);
        outToServer.flush();

        // 3. wait for a confirmation from receiver
        int replyLength = 0;
        while(replyLength == 0)
        {
            replyLength = inFromServer.read(ReceiveWin);
            if(replyLength > 0)
            {
                break;
            }
        }
        if((new String(ReceiveWin, 0, replyLength)).equals("OK"))
        {
            // 4. Send the content of file to the receiver
            long cursor = 0;
            int length; // the length of bytes read from the file every time
            while( cursor < fileLength )
            {
                System.out.println(cursor);
                // read the file into fileBuffer
                length = fileInputStream.read(fileBuffer);
                // send the bytes stream to receiver
                outToServer.write(fileBuffer);
                cursor += length;
            }
        }
        else
        {
            System.out.println("======The receiver did not confirm this transmission. Cancelled");
        }

    }

    public static void main(String[] args) throws IOException {
        TCPSender tcpSender = new TCPSender(InetAddress.getByName("localhost"),51200);
        tcpSender.sendFile("src/com/fitz/socket/vedio1.mkv");
    }
}
