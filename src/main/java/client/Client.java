package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] arg) {
        int port = 1337;
        try {
            // Ouvrir une connexion avec le serveur
            Socket clientSocket = new Socket("127.0.0.1", port);
            System.out.println("Connexion établie avec le serveur");

            // envoyer une requête "charger" au serveur
            OutputStreamWriter outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter writer = new BufferedWriter(outputStream);
            writer.write("charger\n");
            writer.flush();

            // Récupérer la liste des cours et les affiche
            InputStream inputStream = clientSocket.getInputStream(); //copier de chatGPT
            BufferedReader inStr = new BufferedReader(new InputStreamReader(inputStream)); //copier de chatGPT
            String cours;
            while ((cours = inStr.readLine()) != null){
                System.out.println(cours);
            }
            // Fermer la connexion avec le server
            clientSocket.close();
            System.out.println("Connexion fermée avec le serveur.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
