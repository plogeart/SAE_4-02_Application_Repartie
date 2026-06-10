package sae;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceRestaurant extends Remote {

    String getRestaurants() throws RemoteException;

    String getTables() throws RemoteException;

    String getReservations() throws RemoteException;

    String reserver(int restaurantId, int numTable, String nom, String prenom,
                    int nbConvives, String telephone) throws RemoteException;
}
