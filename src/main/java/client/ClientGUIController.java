package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * La classe est le contrôleur de l'interface graphiqe client. Elle permet
 */
public class ClientGUIController {
    /**
     * L'interface graphique client qui est associé au contrôleur
     */
    ClientGUI client;

    /**
     * La méthode constructeur qui permet de prendre en paramètre l'interface graphique client.
     *
     * @param client L'interface graphique associé au contrôleur
     */
    public ClientGUIController(ClientGUI client){
        this.client = client;
    }

    /**
     * La méthode permet de récupérer la liste de cours pour la session choisie.
     *
     * @param session La session pour laquelle on veut récupérer la liste de cours.
     * @return une liste des cours pour la session choisie.
     */
    public ArrayList<Course> getCours(String session){
        return client.getCours(session);
    }

    /**
     * La méthode permet d'envoyer le formulaire d'inscription au serveur.
     *
     * @param session La session pour laquelle l'inscription est effectuée
     * @param prenom Le prénom de l'étudiant
     * @param nom Le nom de l'étudiant
     * @param email Le courriel de l'étudiant
     * @param matricule Le matricule de l'étudiant
     * @param cours Le numéro de cours pour laquelle l'inscription est effectuée
     */
    public void inscription(String session, String prenom, String nom, String email, String matricule, Course cours){
        client.inscription(session, prenom, nom, email, matricule, cours);
}
}
