package server;

/**
 * La classe permet de faire rouler le serveur
 */
public class ServerLauncher {
    /**
     * Le port sur lequel le serveur se connecte
     */
    public final static int PORT = 1337;

    /**
     * La méthode qui permet de créer et de lancer le serveur pour qu'il interagisse avec le client
     *
     * @param args l'argument du terminal
     * @throws Exception S'il y a une erreur qui se produit lors du traitements des demandes du client, il y a impression de l'exception dans la console.
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}