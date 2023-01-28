-- PARA SPRING DATA REACTIVE DEBES CREAR LA TABLAS

-- REPRESENTANTES
CREATE TABLE IF NOT EXISTS REPRESENTANTES
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid       UUID      NOT NULL UNIQUE,
    nombre     TEXT      NOT NULL,
    email      TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN   NOT NULL DEFAULT FALSE
);

-- RAQUETAS
CREATE TABLE IF NOT EXISTS RAQUETAS
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             UUID      NOT NULL UNIQUE,
    marca            TEXT      NOT NULL,
    precio           DOUBLE    NOT NULL,
    representante_id UUID      NOT NULL REFERENCES REPRESENTANTES (uuid),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          BOOLEAN   NOT NULL DEFAULT FALSE
);

-- DATOS DE PRUEBA

-- REPRESENTANTES
INSERT INTO REPRESENTANTES (uuid, nombre, email)
VALUES ('b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf', 'Pepe Perez', 'pepe@perez.com');
INSERT INTO REPRESENTANTES (uuid, nombre, email)
VALUES ('c53062e4-31ea-4f5e-a99d-36c228ed01a3', 'Juan Lopez', 'juan@lopez.com');
INSERT INTO REPRESENTANTES (uuid, nombre, email)
VALUES ('a33cd6a6-e767-48c3-b07b-ab7e015a73cd', 'Maria Garcia', 'maria@garcia.com');

-- RAQUETAS
INSERT INTO RAQUETAS (uuid, marca, precio, representante_id)
VALUES ('86084458-4733-4d71-a3db-34b50cd8d68f', 'Babolat', 200.0, 'b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf');
INSERT INTO RAQUETAS (uuid, marca, precio, representante_id)
VALUES ('b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e', 'Wilson', 250.0, 'c53062e4-31ea-4f5e-a99d-36c228ed01a3');
INSERT INTO RAQUETAS (uuid, marca, precio, representante_id)
VALUES ('e4a7b78e-f9ca-43df-b186-3811554eeeb2', 'Head', 225.0, 'a33cd6a6-e767-48c3-b07b-ab7e015a73cd');
