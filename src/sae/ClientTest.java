package sae;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientTest {
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
