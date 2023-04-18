package client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe permet de créer une application client qui permet de s'inscrire à des cours de l'Université de Montréal.
 * Elle se connecter à un serveur pour acquérir la liste de cours selon la session voulue et permettre à l'utilisateur de s'inscrire à un cours.
 */
public class ClientGUI extends Application {
    /**
     * Le port sur lequel le client se connecte au serveur.
     */
    public int port = 1337;
    /**
     * Le socket utilisé pour se connecter au serveur.
     */
    public Socket socket;
    /**
     * Le flux de sortie d'objets qui permet d'envoyer des données au serveur.
     */
    private ObjectOutputStream oos;
    /**
     * Le flux d'entrée d'objets qui permet de recevoir des données du serveur.
     */
    private ObjectInputStream ois;
    /**
     * Le bouton permettant d'afficher la listes des cours pour une session.
     */
    Button boutonCours = new Button("Charger");
    /**
     * Le bouton permettant d'envoyer le formulaire d'inscription.
     */
    Button envoyer = new Button("envoyer");
    /**
     * Le controleur permet de gérer les actions de l'utilisateur.
     */
    ClientGUIController controller = new ClientGUIController(this);

    /**
     * La méthode main permet de faire rouler l'application client.
     *
     * @param args l'argument du terminal
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * La méthode permet de se déconnecter du serveur.
     * @throws IOException S'il y a une erreur de déconnection avec le serveur.
     */
    public void disconnect() throws IOException {
    }

    /**
     * La méthode permet de connecter le client avec le serveur selon le socket choisi.
     *
     * @param socket Le socket utilisé pour la connection avec le serveur
     * @throws IOException S'il y a une erreur de connection
     */
    public void connectServer(Socket socket){
        try {
            this.socket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * La méthode permet la création de l'interface graphique et configure les différents boutons qui peuvent être utilisés par l'utilisateur.
     *
     * @param primaryStage la fenêtre principale de l'application
     * @throws IOException S'il y a une erreur d'entrée ou de sortie des données lors de la connection avec le serveur.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 400);
        SplitPane splitPane1 = new SplitPane();

        // Section gauche de la fenêtre: affichage des cours offerts dans un TableView (table).
        VBox boxListeCours = new VBox();
        Text titreCours = new Text("Liste des cours:");
        boxListeCours.getChildren().add(titreCours);
        boxListeCours.getChildren().add(new Separator());
        VBox codeCours = new VBox();

        TableView<Course> table = new TableView<>();

        //Création des colonnes de table pour qu'elles affichent le code et le nom des cours.
        TableColumn codeColonne = new TableColumn("Code");
        codeColonne.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn coursColonne = new TableColumn("Cours");
        coursColonne.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codeColonne, coursColonne);
        codeCours.getChildren().add(table);
        boxListeCours.getChildren().add(codeCours);

        // Création du comboBox permettant à l'utilisateur de sélectionner la session désirée.
        boxListeCours.getChildren().add(new Separator());
        HBox boxChargementCours = new HBox();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "Automne",
                "Hiver",
                "Ete"
        );
        boxChargementCours.getChildren().add(comboBox);
        boxChargementCours.getChildren().add(this.boutonCours);
        boxListeCours.getChildren().add(boxChargementCours);

        // Section droite de la fenêtre: inscription au cours sélectionné par l'utilisateur.
        VBox boxInscription = new VBox();
        Text textInscription = new Text("Formulaire d'inscription");
        boxInscription.getChildren().add(textInscription);
        boxInscription.getChildren().add(new Separator());

        // Création des TextFields permettant à l'utilisateur d'entrer ses informations concernant l'inscription (prénom, nom, email et matricule0
        HBox boxPrenom = new HBox();
        Text prenom = new Text("Prénom:");
        TextField fieldPrenom = new TextField();
        boxPrenom.getChildren().addAll(prenom, fieldPrenom);

        HBox boxNom = new HBox();
        Text nom = new Text("Nom:");
        TextField fieldNom = new TextField();
        boxNom.getChildren().addAll(nom, fieldNom);

        HBox boxEmail = new HBox();
        Text email = new Text("Email:");
        TextField fieldEmail = new TextField();
        boxNom.getChildren().addAll(email, fieldEmail);

        HBox boxMatricule = new HBox();
        Text matricule = new Text("Matricule:");
        TextField fieldMatricule = new TextField();
        boxNom.getChildren().addAll(matricule, fieldMatricule);

        VBox champsInscription = new VBox();
        champsInscription.getChildren().addAll(boxPrenom, boxNom, boxEmail, boxMatricule);
        boxInscription.getChildren().add(champsInscription);
        boxInscription.getChildren().add(new Separator());
        boxInscription.getChildren().add(envoyer);


        splitPane1.getItems().addAll(boxListeCours, boxInscription);

        root.getChildren().add(splitPane1);

        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);

        primaryStage.setTitle("Inscription Udem");
        primaryStage.setScene(scene);
        primaryStage.show();

        Socket sockett = new Socket("127.0.0.1", port);
        connectServer(sockett);


        // Ajout d'un eventHandler qui permet de charger les cours dans la table lorsque l'utilisateur appuie sur le bouton charger.
        EventHandler<MouseEvent> chargerHandler =
               e -> {
            chargerCours(table, comboBox.getValue());
                };
        boutonCours.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, chargerHandler);

        // Ajout d'un eventHandler qui permet de gérer l'inscription lorsque l'utilisateur appuie sur le bouton envoyer.
        EventHandler<MouseEvent> envoyerHandler =
                e -> {
                    controller.inscription(comboBox.getValue(), fieldPrenom.getText(), fieldNom.getText(), fieldEmail.getText(), fieldMatricule.getText(), table.getSelectionModel().getSelectedItem());
                };
        envoyer.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, envoyerHandler);

    }

    /**
     * La méthode permet d'afficher la liste de cours pour la session voulue et de l'afficher dans la table voulue.
     *
     * @param table La table sur laquelle on affiche les cours de la session choisie.
     * @param session La session pour laquelle on veut charger la liste de cours.
     */
    public void chargerCours(TableView<Course> table, String session){
        ArrayList<Course> listeCours = controller.getCours(session);
        table.getItems().clear();
        for(Course cours: listeCours){
            table.getItems().add(cours);
        }
    }

    /**
     * La méthode permet de récupérer la liste des cours pour une session spécifique.
     *
     * @param session La session pour laquelle on veut récupérer la liste de cours.
     * @return la liste de cours de la session choisie.
     * @throws IOException S'il y a une erreur d'entrée ou de sortie des données lors de la connection avec le serveur.
     * @throws ClassNotFoundException Si la classe de l'object venant du flux n'est pas trouvée.
     */
    public ArrayList<Course> getCours(String session){
        try {
            oos.writeObject("CHARGER " + session);
            return (ArrayList<Course>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * La méthode permet d'inscrire un étudiant pour un cours choisi et affiche un message de confirmation.
     * @param session La session pour laquelle l'inscription est effectuée
     * @param prenom Le prénom de l'étudiant
     * @param nom Le nom de l'étudiant
     * @param email Le courriel de l'étudiant
     * @param matricule Le matricule de l'étudiant
     * @param cours Le numéro de cours pour laquelle l'inscription est effectuée
     * @throws IOException S'il y a une erreur dans la communication des données de l'inscription.
     */
    public void inscription(String session, String prenom, String nom, String email, String matricule, Course cours){
        String troisPremieresLettres = cours.getCode().substring(0,3);
        String lettresMajuscules = troisPremieresLettres.toUpperCase();
        String code = lettresMajuscules + cours.getCode().substring(3);

        RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, cours);

        VBox root = new VBox();
        Stage stage = new Stage();
        Scene scene = new Scene(root, 700, 300);
        Text confirmation = new Text("Félicitations! Inscription réussie de " + prenom +" au cours " + cours.getName() + ".");
        root.getChildren().add(confirmation);

        stage.setScene(scene);
        stage.setTitle("Confirmation d'inscription");
        stage.show();


        try {
            this.connectServer(new Socket("127.0.0.1", port));
            oos.writeObject("INSCRIRE ");
            oos.writeObject(registrationForm);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    }

