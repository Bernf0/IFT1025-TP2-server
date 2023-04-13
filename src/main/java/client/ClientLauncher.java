package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;

public class ClientLauncher {
    public static void main(String[] arg) {
        int port = 1337;
        try {
            // Ouvrir une connexion avec le serveur
            Socket clientSocket = new Socket("127.0.0.1", port);
            Client client = new Client(clientSocket);
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
            printCourse(client);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Que souhaitez-vous faire?");
            System.out.println("1.Consulter les cours offerts pour une autre session \n2.Inscription à un cours");
            System.out.println("> Choix:");

            int choix = scanner.nextInt();

            while(choix != 1 & choix != 2){
                System.out.println("Option non-valide!");
                System.out.println("> Choix:");
                choix = scanner.nextInt();
            }

            if(choix == 1){
                printCourse(client);
            }
            if(choix == 2) {
                client.inscription();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void printCourse(Client client){
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

        // Imprime la liste de cours pour la session choisie
        if (session.equals("Ete")){
            session = "Été";
        }
        System.out.println("Les cours offerts pendant la session d'" + session.toLowerCase() + " sont:");
        for(int i = 0; i < courseList.size(); i++){
            System.out.println(i + ". " + courseList.get(i).getCode() + "\t" + courseList.get(i).getName());
        }

    }
}