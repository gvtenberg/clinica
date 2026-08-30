-- data.sql é lido pelo Spring. Para rodar depois do Hibernate: spring.sql.init.mode=always e spring.jpa.defer-datasource-initialization=true
-- import.sql é lido pelo Hibernate. Como os imports são multilinha, é necessário usar spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor=multi-line

-- DROP TABLE IF EXISTS consulta;
-- DROP TABLE IF EXISTS paciente;
-- DROP TABLE IF EXISTS medico;
--
-- CREATE TABLE IF NOT EXISTS paciente (
--     id          BIGINT AUTO_INCREMENT PRIMARY KEY,
--     nome        VARCHAR(255) NOT NULL,
--     telefone    VARCHAR(255) NOT NULL
-- );
--
-- CREATE TABLE IF NOT EXISTS medico (
--     id      BIGINT AUTO_INCREMENT PRIMARY KEY,
--     nome    VARCHAR(255) NOT NULL,
--     crm     VARCHAR(255) NOT NULL
-- );
--
-- CREATE TABLE IF NOT EXISTS consulta (
--     id              BIGINT AUTO_INCREMENT PRIMARY KEY,
--     data            TIMESTAMP NOT NULL,
--     valor           DECIMAL(10,2) NOT NULL,
--     observacao      VARCHAR(255),
--     id_paciente     BIGINT NOT NULL REFERENCES paciente(id),
--     id_medico       BIGINT NOT NULL REFERENCES medico(id)
-- );

INSERT INTO paciente (id, nome, telefone) VALUES
    (1, 'Ana Beatriz Souza', '(63) 99912-3456'),
    (2, 'Carlos Henrique Lima', '(63) 99876-5432'),
    (3, 'Mariana Oliveira Costa', '(63) 99123-4567'),
    (4, 'João Pedro Martins', '(63) 99234-5678'),
    (5, 'Fernanda Alves Rocha', '(63) 99345-6789'),
    (6, 'Lucas Gabriel Santos', '(63) 99456-7890');

INSERT INTO medico (id, nome, crm) VALUES
    (1, 'Roberto Almeida', 'CRM-TO 12345'),
    (2, 'Juliana Mendes', 'CRM-TO 23456'),
    (3, 'Marcos Vinicius Ferreira', 'CRM-TO 34567'),
    (4, 'Patrícia Ribeiro', 'CRM-TO 45678');

INSERT INTO consulta (id, data, valor, observacao, id_paciente, id_medico) VALUES
    (1, '2026-08-25 08:00:00', 250.00, 'Consulta de rotina',     1, 1),
    (2, '2026-08-25 09:30:00', 300.00, 'Acompanhamento clínico', 2, 2),
    (3, '2026-08-25 11:00:00', 280.00, 'Avaliação inicial',      3, 3),
    (4, '2026-08-26 08:30:00', 250.00, 'Retorno médico',         4, 1),
    (5, '2026-08-26 10:00:00', 350.00, 'Consulta especializada', 5, 4),
    (6, '2026-08-26 14:00:00', 220.00, 'Consulta de rotina',     6, 2),
    (7, '2026-08-27 09:00:00', 300.00, 'Acompanhamento clínico', 1, 3),
    (8, '2026-08-27 10:30:00', 250.00, 'Retorno médico',         2, 4),
    (9, '2026-08-28 08:00:00', 280.00, 'Avaliação de exames',    3, 1),
    (10, '2026-08-28 15:00:00', 320.00, 'Consulta especializada', 5, 3);

ALTER TABLE paciente ALTER COLUMN id RESTART WITH 7;
ALTER TABLE medico ALTER COLUMN id RESTART WITH 5;
ALTER TABLE consulta ALTER COLUMN id RESTART WITH 11;
