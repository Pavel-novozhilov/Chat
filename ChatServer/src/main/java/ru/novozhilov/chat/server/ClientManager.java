package ru.novozhilov.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    public final static ArrayList<ClientManager> clients = new ArrayList<>();
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату!");
        } catch (IOException e) {
            closeEveryThinks(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                String nameClient = getNameOrNull(messageFromClient);
                if (nameClient == null) {
                    broadcastMessageAll(messageFromClient);
                }
                broadcastMessageByName(nameClient, messageFromClient);
            } catch (IOException e) {
                closeEveryThinks(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private String getNameOrNull(String message) {
        if (message.startsWith("@")) {
            String[] words = message.split("\\s+"); // Разделяем текст на слова
            return words[0].substring(1);
        }
        return null;
    }

    private void broadcastMessageByName(String nameClient, String message) {
        for (ClientManager clientManager: clients) {
            try {
                if (clientManager.name.equals(nameClient)) {
                    clientManager.bufferedWriter.write(message);
                    clientManager.bufferedWriter.newLine();
                    clientManager.bufferedWriter.flush();
                    break;
                }
            } catch (IOException e) {
                closeEveryThinks(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void broadcastMessageAll(String message) {
        for (ClientManager clientManager: clients) {
            try {
                if (!clientManager.name.equals(name)) {
                    clientManager.bufferedWriter.write(message);
                    clientManager.bufferedWriter.newLine();
                    clientManager.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEveryThinks(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void closeEveryThinks(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат!");
    }
}
