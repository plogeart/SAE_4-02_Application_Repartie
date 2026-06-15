package sae;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface représentant le service de gestion des restaurants.
 */
public interface ServiceRestaurant extends Remote {

    /**
     * Récupère la liste des restaurants disponibles.
     * @return une chaîne de caractères représentant la liste des restaurants.
     * @throws RemoteException
     */
    String getRestaurants() throws RemoteException;

    /**
     * Récupère la liste des tables disponibles.
     * @return une chaîne de caractères représentant la liste des tables.
     * @throws RemoteException
     */
    String getTables() throws RemoteException;

    /**
     * Récupère la liste des réservations effectuées.
     * @return une chaîne de caractères représentant la liste des réservations.
     * @throws RemoteException
     */
    String getReservations() throws RemoteException;

    /**
     * Effectue une réservation pour un restaurant donné.
     * @param restaurantId l'identifiant du restaurant.
     * @param numTable le numéro de la table à réserver.
     * @param nom le nom du client.
     * @param prenom le prénom du client.
     * @param nbConvives le nombre de convives.
     * @param telephone le numéro de téléphone du client.
     * @return une chaîne de caractères indiquant le succès ou l'échec de la réservation.
     * @throws RemoteException
     */
    String reserver(int restaurantId, int numTable, String nom, String prenom, int nbConvives, String telephone) throws RemoteException;
}
