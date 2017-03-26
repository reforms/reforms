-- Тестовый сценарий работы ORM мэппинга по UPDATE выражению: простой сценарий

-- Таблица клиентов
CREATE TABLE client(
  id bigint NOT NULL,
  name character varying(127) NOT NULL,
  act_time timestamp NOT NULL,
  version int NOT NULL,
  CONSTRAINT pk_goods PRIMARY KEY (id)
);

-- Добавляем клиентов
INSERT INTO client (id, name, act_time, version) VALUES (1, 'Пупкин Иван Иванович', {ts '2017-01-01 19:12:01.69'}, 1);