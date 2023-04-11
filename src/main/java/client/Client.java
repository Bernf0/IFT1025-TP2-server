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
        Request request = new Request("CHARGER", session);
        try {
            oos.writeObject(request);
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
        ArrayList<Course> allCourseList = this.getCourse("all");
        Course cours = null;
        for (Course course : allCourseList) {
            if (course.getCode.equals(code)) {
                cours = course;
            }
        }
        if(cours == null){
            System.out.println("Le cours choisi n'existe pas!");
            return;
        }
        RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, cours);
        Request request = new Request("INSCRIRE", registrationForm);
         // à tester
        try {
            String message = (String) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}