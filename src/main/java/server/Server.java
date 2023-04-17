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

/**
 * Il s'agit de la classe Server qui permet de créer un Serveur
 * et de lui permettre de communiquer et de répondre aux besoins du client
 */
public class Server {

    /**
     * La commande pour l'inscription.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * La commande pour charger les cours.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Ce constructeur permet de créer une nouvelle instance de la classe Server pour écouter les nouvelles connexions sur le port choisi.
     *
     * @param port Le paramètre port est le numéro de port sur lequel se connecte le serveur pour écouter.
     * @throws IOException Une IOException est lancée lorsque la créaction du socket du serveur ne fonctionne pas
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * La méthode permet l'ajout d'un objet de type EventHandler à la liste handlers
     *
     * @param h Le paramètre h est l'objet de type EventHandler qui est ajouté
     */
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

    /**
     * La méthode permet de se connecter au client, si la demande est faite.
     * Elle permet aussi de d'écouter ses demandes sur le socket du serveur et de les traiter de façon infini et de se déconnecter de celui-ci.
     *
     * @throws Exception S'il y a une erreur qui se produit lors du traitements des demandes du client, il y a impression de l'exception dans la console.
     */
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

    /**
     * La méthode permet d'écouter les demandes du client, tant qu'il y en a.
     * Chaque demande permet de faire déclencher des évenements.
     *
     * @throws IOException Si une erreur est produite lors de la lecture de la demande du client.
     * @throws ClassNotFoundException Si la classe de l'object venant du flux entrant n'est pas trouvée.
     */
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

    /**
     * La méthode permet de séparer une ligne de commande en deux
     *
     * @param line La ligne de commande qui est envoyé par le client
     * @return un object pair qui contient la commande et l'arugment
     */
    public Pair<String, String> processCommandLine(String line) {
        // permet de traiter une ligne de commande sous forme de chaîne de caractères
        // et de la convertir en un objet 'Pair' qui contient cmd et arg séparés
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * La méthode permet de déconnecter le serveur et ses flux d'entrée et de sortie
     *
     * @throws IOException Si une erreur se produit lors de la fermeture des flux
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * La méthode permet d'appeler les méthodes correspondantes, soit handleLoadCourses ou handleRegistration,
     * lorsque les commandes "INSCRIRE " ou "CHARGER " sont passées.
     *
     * @param cmd La commande qui sera effectuée par le serveur
     * @param arg La session pour laquelle le client veut récupérer la liste de cours
     */
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

            // lit le fichier et en fait une liste
            while (scan.hasNext()) {
                String code = scan.next();
                String name = scan.next();
                String session = scan.next();

                Course cours = new Course(name, code, session);
                listeCours.add(cours);
            }
            // fait une liste qui contient seulement les cours de la session donnée en arg
            for (Course coursChoisi : listeCours) {
                if (coursChoisi.getSession().equals(arg)) {
                    listeCoursSession.add(coursChoisi);
                }
            }
            // envoit la liste de cours de la session choisie en arg au client
            objectOutputStream.writeObject(listeCoursSession);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("erreur à l'ouverture du fichier");
        } catch (IOException e) {
            System.out.println("Erreur à l'écriture");
            throw new RuntimeException(e);
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
            System.out.println(registrationForm.getCourse());
            for (Course coursInscrit : listeCours){
                if (coursInscrit.getCode().equals(registrationForm.getCourse().getCode())){
                    sessionInscrit = coursInscrit.getSession();
                }
            }
            // Enregistrer l'objet dans un fichier text
            String fichierInscription = "inscription.txt";
            boolean append = true;
            BufferedWriter writer = new BufferedWriter(new FileWriter(fichierInscription, append));
            writer.append(sessionInscrit + "\t");
            writer.append(registrationForm.getCourse().getCode().toString()+ "\t");
            writer.append(registrationForm.getPrenom().toString()+ "\t");
            writer.append(registrationForm.getNom().toString()+ "\t");
            writer.append(registrationForm.getEmail().toString() + "\t");
            writer.newLine();
            writer.close();

            // Envoyer un message de confirmation au client
            String message = "Félicitations! Inscription réussie de " + registrationForm.getPrenom() +" au cours " + registrationForm.getCourse().getCode() + ".";
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

