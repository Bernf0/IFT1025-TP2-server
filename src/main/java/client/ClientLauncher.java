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

        // Si l'utilisateur entre quelque chose qui n'est pas dans les choix, on lui redemande.
        while(choix != 1 & choix != 2 & choix != 3){
            System.out.println("Option non-valide!");
            System.out.println("> Choix:");
            choix = scanner.nextInt();
        }

        // Sélectionne les bons cours selon la session choisie
        if(choix == 1){
            ArrayList<Course> CourseList = client.getCourse("Automne");
        }
        if (choix == 2) {
            ArrayList<Course> CourseList = client.getCourse("Hiver");
        }
        if(choix == 3){
            ArrayList<Course> CourseList = client.getCourse("Été");
        }

// Imprime la liste de cours pour la session choisie
        for(int i = 0; i < CourseList.size(); i++){
            System.out.println(i + ". " + CourseList.get(i).getCode() + "\t" + CourseList.get(i).getName());
        }

    }
}