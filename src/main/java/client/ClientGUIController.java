package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ClientGUIController {
    ClientGUI client;

    public ClientGUIController(ClientGUI client){
        this.client = client;
    }
    public ArrayList<Course> getCours(String session){
        return client.getCours(session);
    }

    public void inscription(String session, String prenom, String nom, String email, String matricule, Course cours){// À implémenter
        client.inscription(session, prenom, nom, email, matricule, cours);
}
}
