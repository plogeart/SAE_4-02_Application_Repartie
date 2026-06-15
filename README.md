# SAÉ Application Répartie — version simple

Projet Java/Web très simple pour la SAÉ :

- site web Leaflet dans `site/` ;
- proxy HTTP Java dans `ProxyServer.java` ;
- service RMI Java dans `ServeurRestaurant.java` ;
- accès Oracle avec JDBC ;
- aucune couche DAO dans cette version.

## Structure simple

```text
src/sae/ServiceRestaurant.java      Interface RMI
src/sae/ServeurRestaurant.java   Serveur RMI + requêtes JDBC
src/sae/ProxyServer.java         Proxy HTTP + données ouvertes
src/sae/ClientTest.java    Client de test RMI
site/                                           Site web Leaflet
sql/schema.sql                                  Création des tables Oracle
sql/demo-data.sql                               Données de démonstration
scripts/start-rmi.sh                            Lancement du serveur RMI
scripts/start-proxy.sh                          Lancement du proxy HTTP
```

## Configuration Oracle

Le fichier `db.properties` doit contenir vos identifiants Oracle :

```properties
db.driver=oracle.jdbc.OracleDriver
db.url=jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb
db.user=VOTRE_LOGIN
db.password=VOTRE_MOT_DE_PASSE
```

Le driver Oracle `ojdbc.jar` doit être présent dans `lib/`.

## Initialiser la base

Dans SQL*Plus ou SQL Developer, exécuter dans cet ordre :

```sql
@sql/schema.sql
@sql/demo-data.sql
```

Les tables créées sont :

- `RESTAURANT`
- `TABLE_RESTO`
- `RESERVATION`

## Compilation

```bash
ant clean compile
```

## Lancement local

Terminal 1 :

```bash
./scripts/start-rmi.sh
```

Terminal 2 :

```bash
./scripts/start-proxy.sh
```


## Tests API

```bash
curl http://localhost:8000/api/health
curl http://localhost:8000/api/restaurants
curl http://localhost:8000/api/tables
curl http://localhost:8000/api/reservations
curl http://localhost:8000/api/velos
curl http://localhost:8000/api/incidents
```

## Fonctionnement de la réservation

Quand un utilisateur réserve depuis la carte :

1. le site appelle `POST /api/reservations` ;
2. le proxy appelle le service RMI ;
3. le serveur RMI ouvre une transaction JDBC ;
4. la table est verrouillée avec `SELECT ... FOR UPDATE` ;
5. si elle est libre et assez grande, une réservation est insérée ;
6. la table passe au statut `Reservee` ;
7. `COMMIT` valide l'opération.

En cas d'erreur, le serveur fait un `ROLLBACK`.

## WEBETU

Pour webetu modifier le /js/config.js et mettre l'ip de la machine qui lance le proxy.

