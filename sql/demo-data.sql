INSERT INTO RESTAURANT(id_restaurant, nom, adresse, latitude, longitude)
VALUES (1, 'Restaurant Stanislas', 'Place Stanislas, 54000 Nancy', 48.693722, 6.183409);

INSERT INTO RESTAURANT(id_restaurant, nom, adresse, latitude, longitude)
VALUES (2, 'La Table de Bacchus', '15 rue des Marechaux, 54000 Nancy', 48.692200, 6.182900);

INSERT INTO RESTAURANT(id_restaurant, nom, adresse, latitude, longitude)
VALUES (3, 'Brasserie de la Pepiniere', 'Parc de la Pepiniere, 54000 Nancy', 48.697000, 6.184500);

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (1, 1, 2, 'Libre');

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (2, 1, 4, 'Libre');

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (3, 1, 6, 'Libre');

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (4, 2, 4, 'Libre');

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (5, 2, 6, 'Libre');

INSERT INTO TABLE_RESTO(num_table, id_restaurant, nb_places, statut)
VALUES (6, 3, 4, 'Libre');

COMMIT;