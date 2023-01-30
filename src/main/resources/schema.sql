-- PARA SPRING DATA REACTIVE DEBES CREAR LA TABLAS

-- BORRAR TABLAS
DROP TABLE IF EXISTS TENISTAS;
DROP TABLE IF EXISTS RAQUETAS;
DROP TABLE IF EXISTS REPRESENTANTES;

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

-- TENISTAS
CREATE TABLE IF NOT EXISTS TENISTAS
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             UUID      NOT NULL UNIQUE,
    nombre           TEXT      NOT NULL,
    ranking          INTEGER   NOT NULL UNIQUE,
    fecha_nacimiento DATE      NOT NULL,
    año_profesional  INTEGER   NOT NULL,
    altura           INTEGER   NOT NULL,
    peso             INTEGER   NOT NULL,
    mano_dominante   TEXT      NOT NULL,
    tipo_reves       TEXT      NOT NULL,
    puntos           INTEGER   NOT NULL,
    pais             TEXT      NOT NULL,
    raqueta_id       UUID REFERENCES RAQUETAS (uuid),
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

-- TENISTAS
INSERT INTO TENISTAS (uuid, nombre, ranking, fecha_nacimiento, año_profesional, altura, peso, mano_dominante,
                      tipo_reves, puntos, pais, raqueta_id)
VALUES ('ea2962c6-2142-41b8-8dfb-0ecfe67e27df', 'Rafael Nadal', 2, '1985-06-04', 2005, 185, 81, 'IZQUIERDA',
        'DOS_MANOS', 6789, 'España', '86084458-4733-4d71-a3db-34b50cd8d68f');
INSERT INTO TENISTAS (uuid, nombre, ranking, fecha_nacimiento, año_profesional, altura, peso, mano_dominante,
                      tipo_reves, puntos, pais, raqueta_id)
VALUES ('f629e649-c6b7-4514-94a8-36bbcd4e7e1b', 'Roger Federer', 3, '1981-01-01', 2000, 188, 83, 'DERECHA',
        'UNA_MANO', 3789, 'Suiza', 'b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e');
INSERT INTO TENISTAS (uuid, nombre, ranking, fecha_nacimiento, año_profesional, altura, peso, mano_dominante,
                      tipo_reves, puntos, pais, raqueta_id)
VALUES ('24242ae7-1c81-434f-9b33-849a640d68a0', 'Novak Djokovic', 4, '1986-05-05', 2004, 189, 81, 'DERECHA',
        'DOS_MANOS', 1970, 'Serbia', 'e4a7b78e-f9ca-43df-b186-3811554eeeb2');
INSERT INTO TENISTAS (uuid, nombre, ranking, fecha_nacimiento, año_profesional, altura, peso, mano_dominante,
                      tipo_reves, puntos, pais, raqueta_id)
VALUES ('af04e495-bacc-4bde-8d61-d52f78b52a86', 'Dominic Thiem', 5, '1995-06-04', 2015, 188, 82, 'DERECHA',
        'UNA_MANO', 1234, 'Austria', '86084458-4733-4d71-a3db-34b50cd8d68f');
INSERT INTO TENISTAS (uuid, nombre, ranking, fecha_nacimiento, año_profesional, altura, peso, mano_dominante,
                      tipo_reves, puntos, pais, raqueta_id)
VALUES ('a711040a-fb0d-4fe4-b726-75883ca8d907', 'Carlos Alcaraz', 1, '2003-05-05', 2019, 185, 80, 'DERECHA',
        'DOS_MANOS', 6880, 'España', '86084458-4733-4d71-a3db-34b50cd8d68f');
