package sae;

import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
public class ServeurRestaurant extends UnicastRemoteObject implements ServiceRestaurant {

    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public ServeurRestaurant() throws RemoteException {
        super();
        chargerConfigurationBase();
    }

    private void chargerConfigurationBase() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));

            dbDriver = props.getProperty("db.driver");
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");

            Class.forName(dbDriver);
        } catch (Exception e) {
            System.err.println("Erreur configuration JDBC : " + e.getMessage());
        }
    }

    private Connection getConnexion() throws Exception {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private String json(String texte) {
        if (texte == null) {
            return "";
        }
        return texte.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public String getRestaurants() throws RemoteException {
        Connection cnx = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            cnx = getConnexion();
            st = cnx.createStatement();
            rs = st.executeQuery("SELECT id_restaurant, nom, adresse, latitude, longitude FROM restaurant ORDER BY id_restaurant");

            String resultat = "[";
            boolean premier = true;

            while (rs.next()) {
                if (!premier) {
                    resultat += ",";
                }
                premier = false;

                resultat += "{";
                resultat += "\"id\":" + rs.getInt("id_restaurant") + ",";
                resultat += "\"nom\":\"" + json(rs.getString("nom")) + "\",";
                resultat += "\"adresse\":\"" + json(rs.getString("adresse")) + "\",";
                resultat += "\"latitude\":" + rs.getDouble("latitude") + ",";
                resultat += "\"longitude\":" + rs.getDouble("longitude");
                resultat += "}";
            }

            resultat += "]";
            return resultat;

        } catch (Exception e) {
            return "[{\"success\":false,\"message\":\"Erreur restaurants : " + json(e.getMessage()) + "\"}]";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
            try { if (cnx != null) cnx.close(); } catch (Exception e) {}
        }
    }

    public String getTables() throws RemoteException {
        Connection cnx = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            cnx = getConnexion();
            st = cnx.createStatement();

            String sql = "SELECT t.num_table, t.id_restaurant, r.nom AS restaurant, t.nb_places, t.statut " +
                    "FROM table_resto t JOIN restaurant r ON r.id_restaurant = t.id_restaurant " +
                    "ORDER BY t.num_table";

            rs = st.executeQuery(sql);

            String resultat = "[";
            boolean premier = true;

            while (rs.next()) {
                if (!premier) {
                    resultat += ",";
                }
                premier = false;

                resultat += "{";
                resultat += "\"numTable\":" + rs.getInt("num_table") + ",";
                resultat += "\"restaurantId\":" + rs.getInt("id_restaurant") + ",";
                resultat += "\"restaurant\":\"" + json(rs.getString("restaurant")) + "\",";
                resultat += "\"nbPlaces\":" + rs.getInt("nb_places") + ",";
                resultat += "\"statut\":\"" + json(rs.getString("statut")) + "\"";
                resultat += "}";
            }

            resultat += "]";
            return resultat;

        } catch (Exception e) {
            return "[{\"success\":false,\"message\":\"Erreur tables : " + json(e.getMessage()) + "\"}]";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
            try { if (cnx != null) cnx.close(); } catch (Exception e) {}
        }
    }

    public String getReservations() throws RemoteException {
        Connection cnx = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            cnx = getConnexion();
            st = cnx.createStatement();

            String sql = "SELECT res.id_reservation, res.id_restaurant, r.nom AS restaurant, " +
                    "res.num_table, res.nom, res.prenom, res.nb_convives, res.telephone " +
                    "FROM reservation res JOIN restaurant r ON r.id_restaurant = res.id_restaurant " +
                    "ORDER BY res.id_reservation DESC";

            rs = st.executeQuery(sql);

            String resultat = "[";
            boolean premier = true;

            while (rs.next()) {
                if (!premier) {
                    resultat += ",";
                }
                premier = false;

                resultat += "{";
                resultat += "\"idReservation\":" + rs.getInt("id_reservation") + ",";
                resultat += "\"restaurantId\":" + rs.getInt("id_restaurant") + ",";
                resultat += "\"restaurant\":\"" + json(rs.getString("restaurant")) + "\",";
                resultat += "\"numTable\":" + rs.getInt("num_table") + ",";
                resultat += "\"nom\":\"" + json(rs.getString("nom")) + "\",";
                resultat += "\"prenom\":\"" + json(rs.getString("prenom")) + "\",";
                resultat += "\"nbConvives\":" + rs.getInt("nb_convives") + ",";
                resultat += "\"telephone\":\"" + json(rs.getString("telephone")) + "\"";
                resultat += "}";
            }

            resultat += "]";
            return resultat;

        } catch (Exception e) {
            return "[{\"success\":false,\"message\":\"Erreur reservations : " + json(e.getMessage()) + "\"}]";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
            try { if (cnx != null) cnx.close(); } catch (Exception e) {}
        }
    }

    //Transaction pour reserver une table : on verifie que la table est libre et qu elle a assez de places, puis on enregistre la reservation et on marque la table comme reservee
    public String reserver(int restaurantId, int numTable, String nom, String prenom,
        int nbConvives, String telephone) throws RemoteException {
        Connection cnx = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cnx = getConnexion();

            cnx.setAutoCommit(false);

            String sqlTable = "SELECT statut, nb_places, id_restaurant FROM table_resto WHERE num_table = ? FOR UPDATE";
            ps = cnx.prepareStatement(sqlTable);
            ps.setInt(1, numTable);
            rs = ps.executeQuery();

            if (!rs.next()) {
                cnx.rollback();
                return "{\"success\":false,\"message\":\"Table introuvable\"}";
            }

            String statut = rs.getString("statut");
            int nbPlaces = rs.getInt("nb_places");
            int vraiRestaurantId = rs.getInt("id_restaurant");

            rs.close();
            ps.close();

            if (vraiRestaurantId != restaurantId) {
                cnx.rollback();
                return "{\"success\":false,\"message\":\"Cette table n appartient pas a ce restaurant\"}";
            }

            if (!"Libre".equals(statut)) {
                cnx.rollback();
                return "{\"success\":false,\"message\":\"Table deja reservee ou occupee\"}";
            }

            if (nbConvives > nbPlaces) {
                cnx.rollback();
                return "{\"success\":false,\"message\":\"Pas assez de places pour cette table\"}";
            }

            String sqlInsert = "INSERT INTO reservation " +
                    "(id_reservation, id_restaurant, num_table, nom, prenom, nb_convives, telephone) " +
                    "VALUES (seq_reservation.NEXTVAL, ?, ?, ?, ?, ?, ?)";

            ps = cnx.prepareStatement(sqlInsert);
            ps.setInt(1, restaurantId);
            ps.setInt(2, numTable);
            ps.setString(3, nom);
            ps.setString(4, prenom);
            ps.setInt(5, nbConvives);
            ps.setString(6, telephone);
            ps.executeUpdate();
            ps.close();

            String sqlUpdate = "UPDATE table_resto SET statut = 'Reservee' WHERE num_table = ?";
            ps = cnx.prepareStatement(sqlUpdate);
            ps.setInt(1, numTable);
            ps.executeUpdate();

            // Fin de la transaction
            cnx.commit();

            return "{\"success\":true,\"message\":\"Reservation enregistree\"}";

        } catch (Exception e) {
            try {
                if (cnx != null) {
                    cnx.rollback();
                }
            } catch (Exception ex) {}

            return "{\"success\":false,\"message\":\"Erreur reservation : " + json(e.getMessage()) + "\"}";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try {
                if (cnx != null) {
                    cnx.setAutoCommit(true);
                    cnx.close();
                }
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        try {
            String host = "localhost";
            int port = 1099;
            String nomService = "ServiceRestaurant";

            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            if (args.length >= 2) {
                nomService = args[1];
            }

            try {
                LocateRegistry.createRegistry(port);
                System.out.println("Registre RMI cree sur le port " + port);
            } catch (Exception e) {
                System.out.println("Registre RMI deja lance sur le port " + port);
            }

            ServiceRestaurant service = new ServeurRestaurant();
            Registry registry = LocateRegistry.getRegistry(host, port);
            registry.rebind(nomService, service);

            System.out.println("Serveur RMI lance : " + nomService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
