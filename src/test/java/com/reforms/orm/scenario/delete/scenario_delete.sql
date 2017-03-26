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
INSERT INTO client (id, name, act_time, version) VALUES (2, 'Остапов Никалай Сергеевич', {ts '2017-01-01 19:12:01.69'}, 2);
INSERT INTO client (id, name, act_time, version) VALUES (3, 'Васин Никалай Сергеевич', {ts '2017-01-02 19:12:01.69'}, 1);
INSERT INTO client (id, name, act_time, version) VALUES (4, 'Бор Евгений Сергеевич', {ts '2017-01-03 19:12:01.69'}, 2);
INSERT INTO client (id, name, act_time, version) VALUES (5, 'Валуев Никалай Бусурманович', {ts '2017-01-04 19:12:01.69'}, 1);
INSERT INTO client (id, name, act_time, version) VALUES (6, 'Седов Дмитрий Сергеевич', {ts '2017-01-05 19:12:01.69'}, 17);
