-- PARA SPRING DATA REACTIVE DEBES CREAR LA TABLAS
CREATE TABLE IF NOT EXISTS REPRESENTANTES
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid   UUID NOT NULL,
    nombre TEXT NOT NULL,
    email TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Datos de prueba
-- REPRESENTANTES
INSERT INTO REPRESENTANTES (uuid, nombre, email) VALUES ('b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf', 'Pepe Perez', 'pepe@perez.com');
INSERT INTO REPRESENTANTES (uuid, nombre, email) VALUES ('c53062e4-31ea-4f5e-a99d-36c228ed01a3', 'Juan Lopez', 'juan@lopez.com');
INSERT INTO REPRESENTANTES (uuid, nombre, email) VALUES ('a33cd6a6-e767-48c3-b07b-ab7e015a73cd', 'Maria Garcia', 'maria@garcia.com');
