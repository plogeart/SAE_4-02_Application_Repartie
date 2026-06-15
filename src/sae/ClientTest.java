package sae;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 * Classe de test pour le client RMI qui interagit avec le service de gestion des restaurants.
 */
public class ClientTest {
    /**
     * Méthode Main pour le test.
     * @param args les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ServiceRestaurant service = (ServiceRestaurant) registry.lookup("ServiceRestaurant");

            System.out.println("Restaurants :");
            System.out.println(service.getRestaurants());

            System.out.println("Tables :");
            System.out.println(service.getTables());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
