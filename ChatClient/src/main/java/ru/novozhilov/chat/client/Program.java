package ru.novozhilov.chat.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in); Socket socket = new Socket("localhost", 1400)){
        System.out.println("Введите свое имя: ");
        String name = scanner.nextLine();
        Client client = new Client(name, socket);
        client.listenMessage();
        client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
