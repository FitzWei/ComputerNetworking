package com.fitz.socketDemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class TCPReceiver {
    byte[] receiveWin;  // The receive window
    ServerSocket receiveSocket;
    DataInputStream inFromClient;
    DataOutputStream outToClient;

    public TCPReceiver(int port) throws IOException {
        receiveSocket = new ServerSocket(port);
        receiveWin = new byte[8092];
    }

    public void receiveFile() throws IOException {
        while(true) {
            Socket connectionSocket = receiveSocket.accept();
            System.out.println("TCP connection established, the client ip is " + connectionSocket.getLocalSocketAddress());
            // 1. Create input stream and output stream attached to the socket
            inFromClient = new DataInputStream(connectionSocket.getInputStream());
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // 2. receive the init Msg from the sender
            int length = 0;    // the length of the Msg;
            length = inFromClient.read(receiveWin);
            String initMsg = "Rece(TCP)-" + new String(receiveWin, 0, length);
            StringTokenizer stringTokenizer = new StringTokenizer(initMsg, "::");

            String fileName = stringTokenizer.nextToken();
            long bytesToRead = Long.parseLong((stringTokenizer.nextToken()));
            System.out.println("====The file will be saved as '" + fileName + "'====");
            System.out.println("====Expecting to receive " + bytesToRead + " bytes");

            // 3. Send an reply containing 'OK' to sender
            outToClient.write(("OK").getBytes());

            // 4. Receive the contents of the file and save
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            int bytesReceived = 0;
            int packetLength;
            while (bytesReceived < bytesToRead) {
                packetLength = inFromClient.read(receiveWin);
                fileOutputStream.write(receiveWin, 0, packetLength);
                bytesReceived += packetLength;
            }
            System.out.println("======The transmission completed!======");
        }
    }

    public static void main(String[] args) throws IOException {
        TCPReceiver tcpReceiver = new TCPReceiver(51200);
        System.out.println("The receiver is online~");
        long startTime = System.currentTimeMillis();
        tcpReceiver.receiveFile();
        long endTime = System.currentTimeMillis();
        System.out.println("This transmission is based on TCP and it costs " + (endTime-startTime) + " ms");
    }
}
