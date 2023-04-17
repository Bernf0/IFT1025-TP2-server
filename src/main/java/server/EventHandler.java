package server;

/**
 * Il s'agit d'une interface fonctionnelle qui permet de définir une méthode handle() pour définir tous les évenements possibles
 */
@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg);
}
