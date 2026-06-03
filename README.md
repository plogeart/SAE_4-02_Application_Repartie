# SAÉ — Application répartie Nancy

Projet Java/Web pour la SAÉ « Application répartie » : site Leaflet sur Webetu, proxy HTTP Java, service RMI et accès à une base **Oracle** via JDBC.

## Fonctionnalités

- Carte Leaflet centrée sur Nancy.
- Affichage des stations VélOstan'lib.
- Affichage des incidents/travaux de Nancy.
- Proxy Java.
- Service RMI `RestaurantService`.
- JDBC Oracle :
  - `CLIENT`
  - `TABLE_RESTO`
  - `PLAT`
  - `COMMANDE`
  - `DETAIL_PLAT`
- Création d'une commande et mise à jour du statut de table.
- Refus d'une commande si la table demandée est déjà `Occupee` ou `Reservee`.

## Structure

```text
```

## Configuration JDBC Oracle

Configuration pour Oracle sur Charlemagne :

```properties
db.driver=oracle.jdbc.OracleDriver
db.url=jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb
db.user=LOGIN
db.password=MDP
```

À modifier avant de lancer le serveur RMI :

```properties
db.user=login_oracle
db.password=mot_de_passe_oracle
```

## Initialiser la base

Ouvrir  Oracle puis exécuter dans l'ordre :

```text
sql/schema.sql      # suppression/recréation des tables Oracle
sql/demo-data.sql   # exemple
```

## Compilation

```bash
ant clean compile
```

## Lancement
Lance le service RMI dans un terminal avec :
```bash
./scripts/start-rmi.sh
```

Lance le service proxy dans un terminal avec :
```bash
./scripts/start-proxy.sh
```

### Tests

```bash
curl http://localhost:8000/api/health
curl http://localhost:8000/api/tables
curl http://localhost:8000/api/plats
curl http://localhost:8000/api/incidents
```

Retourne des tableau non vide.