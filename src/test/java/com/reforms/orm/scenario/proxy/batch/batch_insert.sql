-- Тестовый сценарий работы ORM мэппинга по SELECT выражению: вложенные данные

-- Таблица клиентов
CREATE TABLE client (
  id bigint NOT NULL,
  name character varying(127) NULL,
  state int NULL
);