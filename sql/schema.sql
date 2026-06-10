DROP TABLE reservation CASCADE CONSTRAINTS PURGE;
DROP TABLE table_resto CASCADE CONSTRAINTS PURGE;
DROP TABLE restaurant CASCADE CONSTRAINTS PURGE;
DROP SEQUENCE seq_reservation;

CREATE TABLE restaurant (
    id_restaurant NUMBER PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL,
    adresse VARCHAR2(255) NOT NULL,
    latitude NUMBER NOT NULL,
    longitude NUMBER NOT NULL
);

CREATE TABLE table_resto (
    num_table NUMBER PRIMARY KEY,
    id_restaurant NUMBER NOT NULL,
    nb_places NUMBER NOT NULL,
    statut VARCHAR2(20) NOT NULL,

    CONSTRAINT fk_table_restaurant
        FOREIGN KEY (id_restaurant)
        REFERENCES restaurant(id_restaurant),

    CONSTRAINT ck_table_statut
        CHECK (statut IN ('Libre', 'Reservee', 'Occupee'))
);

CREATE TABLE reservation (
    id_reservation NUMBER PRIMARY KEY,
    id_restaurant NUMBER NOT NULL,
    num_table NUMBER NOT NULL,
    nom VARCHAR2(100) NOT NULL,
    prenom VARCHAR2(100) NOT NULL,
    nb_convives NUMBER NOT NULL,
    telephone VARCHAR2(30) NOT NULL,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservation_restaurant
        FOREIGN KEY (id_restaurant)
        REFERENCES restaurant(id_restaurant),

    CONSTRAINT fk_reservation_table
        FOREIGN KEY (num_table)
        REFERENCES table_resto(num_table)
);

CREATE SEQUENCE seq_reservation START WITH 1 INCREMENT BY 1;
