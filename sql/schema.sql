    EXECUTE IMMEDIATE 'DROP TABLE RESERVATION CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE TABLE_RESTO CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE RESTAURANT CASCADE CONSTRAINTS';

CREATE TABLE RESTAURANT (
    id_restaurant NUMBER(10) PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL,
    adresse VARCHAR2(255) NOT NULL,
    latitude NUMBER(10,6) NOT NULL,
    longitude NUMBER(10,6) NOT NULL
);

CREATE TABLE TABLE_RESTO (
    num_table NUMBER(10) PRIMARY KEY,
    id_restaurant NUMBER(10) NOT NULL,
    nb_places NUMBER(10) NOT NULL,
    statut VARCHAR2(20) NOT NULL,

    CONSTRAINT ck_table_statut
        CHECK (statut IN ('Libre', 'Reservee', 'Occupee')),

    CONSTRAINT fk_table_restaurant
        FOREIGN KEY (id_restaurant)
        REFERENCES RESTAURANT(id_restaurant)
);

CREATE TABLE RESERVATION (
    id_reservation NUMBER(10) PRIMARY KEY,
    id_restaurant NUMBER(10) NOT NULL,
    num_table NUMBER(10) NOT NULL,
    nom VARCHAR2(100) NOT NULL,
    prenom VARCHAR2(100) NOT NULL,
    nb_convives NUMBER(3) NOT NULL,
    telephone VARCHAR2(30) NOT NULL,

    CONSTRAINT fk_reservation_restaurant
        FOREIGN KEY (id_restaurant)
        REFERENCES RESTAURANT(id_restaurant),

    CONSTRAINT fk_reservation_table
        FOREIGN KEY (num_table)
        REFERENCES TABLE_RESTO(num_table)
);

COMMIT;