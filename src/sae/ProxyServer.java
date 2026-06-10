package sae;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class ProxyServer {

    private ServiceRestaurant service;

    private String velosInfoUrl;
    private String velosStatusUrl;
    private String incidentsUrl;

    public ProxyServer() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("proxy.properties"));

        String rmiHost = props.getProperty("rmi.host", "localhost");
        int rmiPort = Integer.parseInt(props.getProperty("rmi.port", "1099"));
        String rmiName = props.getProperty("rmi.name", "ServiceRestaurant");

        velosInfoUrl = props.getProperty("velos.info.url");
        velosStatusUrl = props.getProperty("velos.status.url");
        incidentsUrl = props.getProperty("incidents.url");

        Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
        service = (ServiceRestaurant) registry.lookup(rmiName);
    }

    public void start(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/health", this::health);
        server.createContext("/api/restaurants", this::restaurants);
        server.createContext("/api/tables", this::tables);
        server.createContext("/api/reservations", this::reservations);
        server.createContext("/api/velos", this::velos);
        server.createContext("/api/incidents", this::incidents);

        server.start();
        System.out.println("Proxy lance sur http://localhost:" + port);
    }

    private void health(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;
        envoyer(e, 200, "{\"success\":true,\"message\":\"Proxy OK\"}");
    }

    private void restaurants(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;
        try {
            envoyer(e, 200, service.getRestaurants());
        } catch (Exception ex) {
            envoyer(e, 500, "{\"success\":false,\"message\":\"Erreur RMI restaurants\"}");
        }
    }

    private void tables(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;
        try {
            envoyer(e, 200, service.getTables());
        } catch (Exception ex) {
            envoyer(e, 500, "{\"success\":false,\"message\":\"Erreur RMI tables\"}");
        }
    }

    private void reservations(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;

        try {
            if ("GET".equals(e.getRequestMethod())) {
                envoyer(e, 200, service.getReservations());
                return;
            }

            if ("POST".equals(e.getRequestMethod())) {
                String body = lireCorps(e);
                JSONObject obj = new JSONObject(body);

                int restaurantId = obj.getInt("restaurantId");
                int numTable = obj.getInt("numTable");
                String nom = obj.getString("nom");
                String prenom = obj.getString("prenom");
                int nbConvives = obj.getInt("nbConvives");
                String telephone = obj.getString("telephone");

                String resultat = service.reserver(restaurantId, numTable, nom, prenom, nbConvives, telephone);
                envoyer(e, 200, resultat);
                return;
            }

            envoyer(e, 405, "{\"success\":false,\"message\":\"Methode non autorisee\"}");
        } catch (Exception ex) {
            envoyer(e, 500, "{\"success\":false,\"message\":\"Erreur reservation : " + json(ex.getMessage()) + "\"}");
        }
    }

    private void velos(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;
        envoyer(e, 200, chargerVelos());
    }

    private void incidents(HttpExchange e) throws java.io.IOException {
        if (cors(e)) return;
        envoyer(e, 200, chargerIncidents());
    }

    private String lireCorps(HttpExchange e) throws java.io.IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(e.getRequestBody(), "UTF-8"));
        String ligne;
        String texte = "";

        while ((ligne = br.readLine()) != null) {
            texte += ligne;
        }

        return texte;
    }

    private String lireURL(String adresse) throws Exception {
        URL url = new URL(adresse);
        InputStream input = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));

        String ligne;
        String texte = "";

        while ((ligne = br.readLine()) != null) {
            texte += ligne;
        }

        br.close();
        return texte;
    }

    private String chargerVelos() {
        try {
            JSONObject info = new JSONObject(lireURL(velosInfoUrl));
            JSONObject status = new JSONObject(lireURL(velosStatusUrl));

            JSONArray stationsInfo = info.getJSONObject("data").getJSONArray("stations");
            JSONArray stationsStatus = status.getJSONObject("data").getJSONArray("stations");

            JSONArray resultat = new JSONArray();

            for (int i = 0; i < stationsInfo.length(); i++) {
                JSONObject station = stationsInfo.getJSONObject(i);
                String stationId = station.getString("station_id");

                for (int j = 0; j < stationsStatus.length(); j++) {
                    JSONObject etat = stationsStatus.getJSONObject(j);

                    if (stationId.equals(etat.getString("station_id"))) {
                        JSONObject simple = new JSONObject();
                        simple.put("name", station.optString("name"));
                        simple.put("adresse", station.optString("address"));
                        simple.put("lat", station.getDouble("lat"));
                        simple.put("lon", station.getDouble("lon"));
                        simple.put("numBikesAvailable", etat.optInt("num_bikes_available"));
                        simple.put("numDocksAvailable", etat.optInt("num_docks_available"));
                        resultat.put(simple);
                    }
                }
            }

            return resultat.toString();
        } catch (Exception e) {
            System.err.println("Erreur velos : " + e.getMessage());
            return "[]";
        }
    }

    private String chargerIncidents() {
        try {
            JSONObject json = new JSONObject(lireURL(incidentsUrl));
            JSONArray incidents = json.getJSONArray("incidents");
            JSONArray resultat = new JSONArray();

            for (int i = 0; i < incidents.length(); i++) {
                JSONObject incident = incidents.getJSONObject(i);
                JSONObject lieu = incident.getJSONObject("location");

                String polyline = lieu.getString("polyline");
                String[] coord = polyline.split(" ");

                JSONObject simple = new JSONObject();
                simple.put("adresse", lieu.optString("location_description"));
                simple.put("cause", incident.optString("short_description"));
                simple.put("description", incident.optString("description"));
                simple.put("start", incident.optString("starttime"));
                simple.put("end", incident.optString("endtime"));
                simple.put("lat", Double.parseDouble(coord[0]));
                simple.put("lon", Double.parseDouble(coord[1]));

                resultat.put(simple);
            }

            return resultat.toString();
        } catch (Exception e) {
            System.err.println("Erreur incidents : " + e.getMessage());
            return "[]";
        }
    }

    private boolean cors(HttpExchange e) throws java.io.IOException {
        Headers h = e.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(e.getRequestMethod())) {
            e.sendResponseHeaders(204, -1);
            return true;
        }

        return false;
    }

    private void envoyer(HttpExchange e, int code, String json) throws java.io.IOException {
        cors(e);
        byte[] bytes = json.getBytes("UTF-8");
        e.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        e.sendResponseHeaders(code, bytes.length);

        OutputStream os = e.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String json(String texte) {
        if (texte == null) {
            return "";
        }
        return texte.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    public static void main(String[] args) {
        try {
            int port = 8000;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            ProxyServer proxy = new ProxyServer();
            proxy.start(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
