package client;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGUI extends Application {

    public static void main(String[] args) {
        launch();
    }

    private Socket socket;

    public ClientGUI(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public void connectServer(){
        try {
            this.socket = new Socket("127.0.0.1", 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, 320, 250);
        SplitPane splitPane1 = new SplitPane();

        // SECTION GAUCHE: AFFICHER COURS
        VBox boxListeCours = new VBox();
        Text titreCours = new Text("Liste des cours:");
        boxListeCours.getChildren().add(titreCours);
        boxListeCours.getChildren().add(new Separator());
        VBox codeCours = new VBox();

        TableView<String> table = new TableView<>();
        TableColumn codeCol = new TableColumn("Code");
        TableColumn coursCol = new TableColumn("Cours");

        table.getColumns().addAll(codeCol, coursCol);

        codeCours.getChildren().add(table);

        boxListeCours.getChildren().add(codeCours);

        boxListeCours.getChildren().add(new Separator());
        HBox boxChargementCours = new HBox();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "Automne",
                "Hiver",
                "Été"
        );
        boxChargementCours.getChildren().add(comboBox);
        Button boutonCours = new Button("Charger");
        boxChargementCours.getChildren().add(boutonCours);

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
        Button envoyer = new Button("envoyer");
        boxInscription.getChildren().add(envoyer);


        splitPane1.getItems().addAll(boxListeCours, boxInscription);

        root.getChildren().add(splitPane1);

        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);

        primaryStage.setTitle("Inscription Udem");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void inscription(String session){ // À implémenter

    }

    public ArrayList<Course> getCourse(String session){ // À implémenter

        return null;
    }

}