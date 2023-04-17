package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;

public class ClientLauncher {
    public static void main(String[] arg) {
        int port = 1337;

        try {
            boolean disconnect = false;
            // Ouvrir une connexion avec le serveur
            Socket clientSocket = new Socket("127.0.0.1", port);
            Client client = new Client(clientSocket);
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            String sessionEnCours = printCourse(client);
            int choix = 0;


            while(disconnect == false){
                Scanner scanner = new Scanner(System.in);
                System.out.println("Que souhaitez-vous faire?");
                System.out.println("1.Consulter les cours offerts pour une session \n2.Inscription à un cours");
                System.out.println("> Choix:");
                choix = scanner.nextInt();
                while(choix != 1 & choix != 2){
                    System.out.println("Option non-valide!");
                    System.out.println("> Choix:");
                    choix = scanner.nextInt();
                }
                if(choix == 1){
                    client.connectServer();
                    sessionEnCours = printCourse(client);
                }
                if(choix == 2) {
                    client.connectServer();
                    client.inscription(sessionEnCours);
                }

                }

                if(choix == 3){
                    disconnect = true;
                    client.disconnect();
                }
            } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


    }



    public static String printCourse(Client client){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours:");
        System.out.println("1.Automne \n2.Hiver \n3.Été");
        System.out.println("> Choix: ");

        int choix = scanner.nextInt();
        String session = null;

        // Si l'utilisateur entre quelque chose qui n'est pas dans les choix, on lui redemande.
        while(choix != 1 & choix != 2 & choix != 3){
            System.out.println("Option non-valide!");
            System.out.println("> Choix:");
            choix = scanner.nextInt();
        }
        ArrayList<Course> courseList = null;
        String[] sessionsPossibles = {"Automne", "Hiver", "Ete"}; // ne doit pas mettre d'accent sinon ne reconnait pas le cours dans le fichier cours.txt
        // Sélectionne les bons cours selon la session choisie
        if(choix == 1){
            session = sessionsPossibles[0];
            courseList = client.getCourse(session);
        }
        if (choix == 2) {
            session = sessionsPossibles[1];
            courseList = client.getCourse(session);
        }
        if(choix == 3){
            session = sessionsPossibles[2];
            courseList = client.getCourse(session);
        }
        System.out.println("Les cours offerts pendant la session d'" + session.toLowerCase() + " sont:");
        for (int i = 0; i < courseList.size(); i++){
            System.out.println(i + ". " + courseList.get(i).getCode() + "\t" + courseList.get(i).getName());
        }
        return session;
    }
}