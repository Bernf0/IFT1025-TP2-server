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
            oos.writeObject("CHARGER " + session); //important de mettre un espace ici
            return (ArrayList<Course>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void inscription() throws IOException {
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

        //test
        System.out.println("print " + code);
        //Course cours = new Course("foutuProg", code, "Ayt");

        //ArrayList<Course> listeCoursSession = this.getCourse(session); // ne fonctionne pas doit être la liste de cours de seulement la session voulue

        // ajouter une erreur si le choix de cours d'est pas dasn la liste de cours de la session donnée

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
        System.out.println("se rend à inscrire");
        oos.writeObject("INSCRIRE ");//important de mettre un espace ici
        oos.writeObject(registrationForm); // comme vu sur piazza, mettre séparé

        try {
            String message = (String) ois.readObject();
            System.out.println(message);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}