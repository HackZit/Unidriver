CREATE TABLE IF NOT EXISTS users(
    USERNAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    LAST_NAME VARCHAR(255) NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    PHONE NUMERIC NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    PRIMARY KEY (USERNAME)
);

DROP TABLE reviews;

CREATE TABLE IF NOT EXISTS viajes(
    IDviajes serial primary key,
    dir_destino varchar(255) NOT NULL,
    dir_inicio varchar(255) NOT NULL,
    usermain varchar(255) NOT NULL,
    pasajeros varchar(255) NOT NULL,
    num_pasajeros int NOT NULL,
    numactual_pasajeros int NOT NULL,
    hora_destino varchar(255) NOT NULL,
    activo boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews(
    IDviajes int NOT NULL,
    USERNAME varchar(255) NOT NULL,
    puntaje int,
    PRIMARY KEY (IDviajes, USERNAME)
);

CREATE TABLE IF NOT EXISTS direcciones(
    IDdir serial primary key,
    USERNAME varchar(255) NOT NULL,
    dir_destino varchar(255) NOT NULL
);