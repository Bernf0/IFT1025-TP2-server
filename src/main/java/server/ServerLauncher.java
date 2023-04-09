package server;

public class ServerLauncher {
    public final static int PORT = 1337;

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
            // écriture des classes anonymes
            // server.addEventHandler((cmd, arg) -> {
            // if (cmd.equals("echo")) { System.out.println(arg); }
            // });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}