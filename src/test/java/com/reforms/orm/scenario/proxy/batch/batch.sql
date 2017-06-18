-- Тестовый сценарий работы ORM мэппинга по SELECT выражению: вложенные данные

-- Таблица клиентов
CREATE TABLE client (
  id bigint NOT NULL,
  name character varying(127) NOT NULL
);

INSERT INTO client (id, name) VALUES (1, '1');
INSERT INTO client (id, name) VALUES (2, '2');
INSERT INTO client (id, name) VALUES (3, '3');
INSERT INTO client (id, name) VALUES (4, '4');
INSERT INTO client (id, name) VALUES (5, '5');
INSERT INTO client (id, name) VALUES (6, '6');
INSERT INTO client (id, name) VALUES (7, '7');



CREATE TABLE client2 (
  id bigint NOT NULL,
  name character varying(127) NULL
);

INSERT INTO client2 (id, name) VALUES (1, '1');
INSERT INTO client2 (id, name) VALUES (2, '2');
INSERT INTO client2 (id, name) VALUES (3, '3');
