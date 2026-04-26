
CREATE DATABASE IF NOT EXISTS gvtp_db;
USE gvtp_db;


CREATE TABLE USUARIO (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);


CREATE TABLE PARADA (
    id_parada INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);


CREATE TABLE TRAMO (
    id_tramo INT AUTO_INCREMENT PRIMARY KEY,
    id_parada_origen INT NOT NULL,
    id_parada_destino INT NOT NULL,
    distancia DECIMAL(6,2) NOT NULL,
    tiempo TIME NOT NULL,
    FOREIGN KEY (id_parada_origen) REFERENCES PARADA(id_parada),
    FOREIGN KEY (id_parada_destino) REFERENCES PARADA(id_parada)
);


CREATE TABLE VIAJE (
    id_viaje INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_transporte ENUM('autobus', 'tren', 'urbano') NOT NULL
);


CREATE TABLE VIAJE_TRAMO (
    id_viaje INT NOT NULL,
    id_tramo INT NOT NULL,
    orden INT NOT NULL,
    PRIMARY KEY (id_viaje, id_tramo),
    FOREIGN KEY (id_viaje) REFERENCES VIAJE(id_viaje),
    FOREIGN KEY (id_tramo) REFERENCES TRAMO(id_tramo)
);


CREATE TABLE BILLETE (
    id_billete INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_viaje INT NOT NULL,
    id_parada_origen INT NOT NULL,
    id_parada_destino INT NOT NULL,
    precio_base DECIMAL(8,2) NOT NULL,
    descuento DECIMAL(5,2) DEFAULT 0,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE DEFAULT NULL,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_viaje) REFERENCES VIAJE(id_viaje),
    FOREIGN KEY (id_parada_origen) REFERENCES PARADA(id_parada),
    FOREIGN KEY (id_parada_destino) REFERENCES PARADA(id_parada)
);