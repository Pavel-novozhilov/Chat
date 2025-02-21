package ru.novozhilov.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String userName;
    private final Socket socket;
    private final Client client;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Client(String userName, Socket socket) {
        this.socket = socket;
        this.userName = userName;
        client = new Client(userName, socket);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeStream(socket, bufferedReader, bufferedWriter);
        }
        sendMessage();
    }

    public void listenMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        closeStream(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                bufferedWriter.write(userName + " :" + scanner.nextLine());
                bufferedWriter.newLine();
                bufferedWriter.flush();
                }
            } catch (IOException e) {
            closeStream(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeStream(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
