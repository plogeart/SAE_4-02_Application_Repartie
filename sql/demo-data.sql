INSERT INTO restaurant VALUES (1, 'La Maison dans le Parc', '3 Rue Sainte-Catherine, 54000 Nancy', 48.692083, 6.183925);
INSERT INTO restaurant VALUES (2, 'Brasserie Excelsior', '50 Rue Henri Poincare, 54000 Nancy', 48.690644, 6.175585);
INSERT INTO restaurant VALUES (3, 'Le Bouche a Oreille', '42 Rue des Carmes, 54000 Nancy', 48.691420, 6.181130);

INSERT INTO table_resto VALUES (1, 1, 2, 'Libre');
INSERT INTO table_resto VALUES (2, 1, 4, 'Libre');
INSERT INTO table_resto VALUES (3, 2, 4, 'Libre');
INSERT INTO table_resto VALUES (4, 2, 6, 'Libre');
INSERT INTO table_resto VALUES (5, 3, 2, 'Libre');
INSERT INTO table_resto VALUES (6, 3, 4, 'Libre');

COMMIT;
