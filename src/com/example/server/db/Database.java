package com.example.server.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrique de connexions JDBC.
 *
 * La configuration est lue dans db.properties. Le fichier peut être placé à la
 * racine du projet lors de l'exécution, ou dans le classpath. Cela permet de ne
 * pas coder d'URL, d'utilisateur ou de mot de passe directement dans le code.
 */
public final class Database {
    private static final Properties PROPS = new Properties();
    private static boolean loaded = false;

    private Database() {
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }

        try (InputStream file = new FileInputStream("db.properties")) {
            PROPS.load(file);
        } catch (IOException fileError) {
            try (InputStream res = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (res != null) {
                    PROPS.load(res);
                }
            } catch (IOException ignored) {
                // L'erreur sera plus explicite lors de getConnection().
            }
        }

        String driver = PROPS.getProperty("db.driver", "").trim();
        if (!driver.isEmpty()) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Driver JDBC introuvable : " + driver, e);
            }
        }

        loaded = true;
    }

    /**
     * Ouvre une connexion JDBC configurée dans db.properties.
     *
     * @return connexion JDBC ouverte
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        load();
        String url = PROPS.getProperty("db.url");
        String user = PROPS.getProperty("db.user", "");
        String password = PROPS.getProperty("db.password", "");

        if (url == null || url.trim().isEmpty()) {
            throw new SQLException("db.url absent dans db.properties");
        }

        if (user.isEmpty()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
