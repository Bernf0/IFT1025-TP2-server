package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;

public class Client {

    Socket socket;
    ObjectOutput oos;
    ObjectInputStream ois;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public void disconnect() throws IOException {
        oos.close();
        ois.close();
        socket.close();
    }

    public ArrayList<Course> getCourse(String session) {
        try {
            oos.writeObject("CHARGER " + session);
            return (ArrayList<Course>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectServer(){
        try {
            this.socket = new Socket("127.0.0.1", 1337);
            oos = new ObjectOutputStream(this.socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void inscription(String session) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez saisir votre prénom:");
        String prenom = scanner.nextLine();
        System.out.println("Veuillez saisir votre nom:");
        String nom = scanner.nextLine();
        System.out.println("Veuillez saisir votre email:");
        String email = scanner.nextLine();
        System.out.println("Veuillez saisir votre matricule:");
        String matricule = scanner.nextLine();
        System.out.println("Veuillez saisir le code du cours:");
        String code = scanner.nextLine();
        // Permet de s'assurer de pouvoir s'inscrire même si le code est inscrit en lettres minuscules
        String troisPremieresLettres = code.substring(0,3);
        String lettresMajuscules = troisPremieresLettres.toUpperCase();
        code = lettresMajuscules + code.substring(3);

        ArrayList<Course> listeCoursSession = this.getCourse(session);

        // ajouter une erreur si le choix de cours d'est pas dans la liste de cours de la session donnée
        Course cours = null;
        for (Course course : listeCoursSession) {
            if (course.getCode().equals(code)) {
                cours = course;
            }
        }
        if(cours == null){
            System.out.println("Le cours choisi n'existe pas!");
            return;
        }

        RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, cours);
        this.connectServer();
        oos.writeObject("INSCRIRE ");
        oos.writeObject(registrationForm);
        oos.flush();

        try {
            String message = (String) ois.readObject();
            System.out.println(message);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

}
}