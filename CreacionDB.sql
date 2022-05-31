CREATE TABLE IF NOT EXISTS users(
    USERNAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    LAST_NAME VARCHAR(255) NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    PHONE NUMERIC NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    PRIMARY KEY (USERNAME)
);

CREATE TABLE IF NOT EXISTS vehiculos(
    USERNAME varchar(255) NOT NULL,
    placa_carro varchar(6) NOT NULL,
    marca_carro varchar(255) NOT NULL,
    vin_carro varchar(255) NOT NULL,
    año_carro NUMERIC NOT NULL,
    modelo_carro varchar(255) NOT NULL,
    PRIMARY KEY (placa_carro)
);

CREATE TABLE IF NOT EXISTS viajes(
    IDviajes int NOT NULL,
    dir_destino varchar(255) NOT NULL,
    dir_inicio varchar(255) NOT NULL,
    placa_carro varchar(6) NOT NULL,
    marca_carro varchar(255) NOT NULL,
    conductor varchar(255) NOT NULL,
    num_pasajeros int NOT NULL,
    precio MONEY NOT NULL,
    PRIMARY KEY (IDviajes)
);

CREATE TABLE IF NOT EXISTS reviews(
    IDviajes int NOT NULL,
    conductor varchar(255) NOT NULL,
    puntaje int,
    PRIMARY KEY (IDviajes)
);

