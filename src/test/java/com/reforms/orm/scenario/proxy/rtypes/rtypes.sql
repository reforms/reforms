-- Тестовый сценарий работы ORM мэппинга по SELECT выражению: вложенные данные

-- Таблица клиентов
CREATE TABLE client(
  id bigint NOT NULL,
  name character varying(127) NOT NULL
);

INSERT INTO client (id, name) VALUES (1, '1');
INSERT INTO client (id, name) VALUES (2, '2');
INSERT INTO client (id, name) VALUES (3, '3');
