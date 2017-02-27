-- Тестовый сценарий работы ORM мэппинга по SELECT выражению: вложенные данные

-- Таблица клиентов
CREATE TABLE client(
  id bigint NOT NULL,
  type int NOT NULL,
  state int NOT NULL,
  mode character varying(10) NOT NULL,
  CONSTRAINT pk_goods PRIMARY KEY (id)
);

-- Добавляем клиентов
INSERT INTO client (id, type, state, mode) VALUES (1, 0, 1, 'online');
INSERT INTO client (id, type, state, mode) VALUES (2, 1, 1, 'online');
INSERT INTO client (id, type, state, mode) VALUES (3, 1, 2, 'offline');
