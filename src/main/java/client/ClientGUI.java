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

public class ClientGUI extends Application {

    int port = 1337;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Button boutonCours = new Button("Charger");
    Button envoyer = new Button("envoyer");
    ClientGUIController controller = new ClientGUIController(this);

    public static void main(String[] args) {
        launch();
    }

    public void disconnect() throws IOException {
    }

    public void connectServer(Socket socket){
        try {
            this.socket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 400);
        SplitPane splitPane1 = new SplitPane();

        // SECTION GAUCHE: AFFICHER COURS
        VBox boxListeCours = new VBox();
        Text titreCours = new Text("Liste des cours:");
        boxListeCours.getChildren().add(titreCours);
        boxListeCours.getChildren().add(new Separator());
        VBox codeCours = new VBox();

        TableView<Course> table = new TableView<>();


        TableColumn codeColonne = new TableColumn("Code");
        codeColonne.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn coursColonne = new TableColumn("Cours");
        coursColonne.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codeColonne, coursColonne);

        codeCours.getChildren().add(table);

        boxListeCours.getChildren().add(codeCours);

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

        // SECTION DROITE: INSCRIPTION
        VBox boxInscription = new VBox();
        Text textInscription = new Text("Formulaire d'inscription");
        boxInscription.getChildren().add(textInscription);
        boxInscription.getChildren().add(new Separator());

        // ENTRER LES DIFFÉRENTES INFOS
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


        //boutonCours.setOnAction(actionEvent ->
                //chargerCours(table, "Automne"));

        EventHandler<MouseEvent> chargerHandler =
                e -> {
                chargerCours(table, comboBox.getValue());
                };
        boutonCours.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, chargerHandler);

        EventHandler<MouseEvent> envoyerHandler =
                e -> {
                    controller.inscription(comboBox.getValue(), fieldPrenom.getText(), fieldNom.getText(), fieldEmail.getText(), fieldMatricule.getText(), table.getSelectionModel().getSelectedItem());
                };
        envoyer.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, envoyerHandler);

    }


    public void chargerCours(TableView<Course> table, String session){
        ArrayList<Course> listeCours = controller.getCours(session);
        table.getItems().clear();
        for(Course cours: listeCours){
            table.getItems().add(cours);
        }
    }

    public ArrayList<Course> getCours(String session){
        try {
            oos.writeObject("CHARGER " + session); //important de mettre un espace ici
            return (ArrayList<Course>) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void inscription(String session, String prenom, String nom, String email, String matricule, Course cours){// À implémenter
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
            oos.writeObject("INSCRIRE ");//important de mettre un espace ici
            //oos.flush();
            //oos.writeObject(registrationForm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    }

