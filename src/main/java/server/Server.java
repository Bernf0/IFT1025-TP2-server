package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream; //lire des objets du flux entrant de données depuis le client.
    private ObjectOutputStream objectOutputStream; //écrire des objets dans le flux sortant de données vers le client.
    private final ArrayList<EventHandler> handlers; //stocker les objets qui gèrent les événements liés aux commandes envoyées par les clients.

    public Server(int port) throws IOException { // constructeur
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) { // ajout d'un objet EventHandler à la liste de handlers
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        // envoie à chaque h de la liste handlers que le message (cmd, arg) est arrivée
        // et c'est à l'event de choisir comment réagir
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        // écoute les entrées du flux données objectInputStream
        // et déclenche des évenements pour chaque commande reçue avec le alertHandlers
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg); //
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        // permet de traiter une ligne de commande sous forme de chaîne de caractères
        // et de la convertir en un objet 'Pair' qui contient cmd et arg séparés
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        try {
            Scanner scan = new Scanner(new File("src/main/java/server/data/cours.txt"));
            ArrayList<Course> listeCours = new ArrayList<Course>();
            ArrayList<Course> listeCoursSession = new ArrayList<Course>();


            while (scan.hasNext()) { // lit le fichier et en fait une liste
                String code = scan.next();
                String name = scan.next();
                String session = scan.next();

                Course cours = new Course(name, code, session);
                listeCours.add(cours);

            }
            for (Course coursChoisi : listeCours) { // fait une liste qui contient seulement les cours de la session donnée en arg
                if (coursChoisi.getSession().equals(arg)) {
                    listeCoursSession.add(coursChoisi);
                }
            }
            // envoit la liste de cours de la session choisie en arg au client
            objectOutputStream.writeObject(listeCoursSession);
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("erreur à l'ouverture du fichier");
        } catch (IOException e) {
            System.out.println("Erreur à l'écriture");
            throw new RuntimeException(e); // c'est intellij qui a rajouté ça
        }
    }
    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */

    public void handleRegistration() {
        try {
            // Récupérer l'objet 'RegistrationForm' envoyé par le client
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            // Récupération de la session du cours
            Scanner scan = new Scanner(new File("src/main/java/server/data/cours.txt"));
            ArrayList<Course> listeCours = new ArrayList<Course>();
            String sessionInscrit = null;
            while (scan.hasNext()) { // lit le fichier et en fait une liste
                String code = scan.next();
                String name = scan.next();
                String session = scan.next();

                Course cours = new Course(name, code, session);
                listeCours.add(cours);

            }
            for (Course coursInscrit : listeCours){
                if (coursInscrit.getCode().equals(registrationForm.getCourse())){
                    sessionInscrit = coursInscrit.getSession();
                }
            }
            // Enregistrer l'objet dans un fichier text
            String fichierInscription = "inscription.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fichierInscription));
            writer.write(sessionInscrit + "\n");
            writer.write(registrationForm.getCourse().toString()+ "\n");
            writer.write(registrationForm.getPrenom().toString()+ "\n");
            writer.write(registrationForm.getNom().toString()+ "\n");
            writer.write(registrationForm.getEmail().toString() + "\n");
            writer.close();

            // Envoyer un message de confirmation au client
            String message = "Félicitations! Inscription réussie de " + registrationForm.getPrenom() +" au cours " + registrationForm.getCourse() + ".";
            System.out.println(message);
            objectOutputStream.writeObject(message);
            objectOutputStream.close();

        } catch (IOException e) { //vérifier les exceptions, il en manque
            System.out.println("Erreur lors de l'écriture du fichier");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

