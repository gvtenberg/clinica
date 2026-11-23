-- data.sql é lido pelo Spring. Para rodar depois do Hibernate: spring.sql.init.mode=always e spring.jpa.defer-datasource-initialization=true
-- import.sql é lido pelo Hibernate. Como os imports são multilinha, é necessário usar spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor=multi-line

INSERT INTO perfil (id, nome) VALUES (1,'ADMIN'),(2,'SECRETARIO'),(3,'MEDICO'),(4,'PACIENTE');

-- id do usuário, nome de usuario, hash da senha, ativo
INSERT INTO usuario (id, usuario, senha, ativo) VALUES
    (1, 'admin', '$2a$10$GZYZJQo4vZnSx.jBCmbjgubogV4upVP5mEMpHUa2POHig/voCBj76', true),
    (2, 'secretario', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (3, 'ana', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (4, 'carlos', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (5, 'mariana', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (6, 'joao', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (7, 'fernanda', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (8, 'lucas', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (9, 'roberto', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (10, 'juliana', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (11, 'marcos', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true),
    (12, 'patricia', '$2a$10$tcOsRRGfdTbnuWhhF8oCsOItNazQ60k5zvurMPXaxZnPvzpW3IHTS', true);

-- id do usuário, id do perfil
INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES
    (1,1),
    (2,2),
    (3,4),
    (4,4),
    (5,4),
    (6,4),
    (7,4),
    (8,4),
    (9,3),
    (10,3),
    (11,3),
    (12,3);

-- id, nome, cpf, email, telefone
INSERT INTO paciente (id, nome, cpf, email, telefone, id_usuario) VALUES
    (1, 'Ana Beatriz Souza', '111.222.333-96', 'ana.beatriz@email.com', '(63) 99912-3456', 3),
    (2, 'Carlos Henrique Lima', '222.333.444-05', 'carlos.lima@email.com', '(63) 99876-5432', 4),
    (3, 'Mariana Oliveira Costa', '333.444.555-08', 'mariana.costa@email.com', '(63) 99123-4567', 5),
    (4, 'João Pedro Martins', '444.555.666-19', 'joao.martins@email.com', '(63) 99234-5678', 6),
    (5, 'Fernanda Alves Rocha', '555.666.777-20', 'fernanda.rocha@email.com', '(63) 99345-6789', 7),
    (6, 'Lucas Gabriel Santos', '666.777.888-30', 'lucas.santos@email.com', '(63) 99456-7890', 8);

-- id, nome, cpf, crm, email, telefone
INSERT INTO medico (id, nome, cpf, crm, email, telefone, id_usuario) VALUES
    (7, 'Roberto Almeida', '777.888.999-41', 'CRM-TO 12345', 'roberto.almeida@clinica.com', '(63) 98888-1111', 9),
    (8, 'Juliana Mendes', '888.999.000-78', 'CRM-TO 23456', 'juliana.mendes@clinica.com', '(63) 98888-2222', 10),
    (9, 'Marcos Vinicius Ferreira', '999.000.111-12', 'CRM-TO 34567', 'marcos.ferreira@clinica.com', '(63) 98888-3333', 11),
    (10, 'Patrícia Ribeiro', '000.111.222-85', 'CRM-TO 45678', 'patricia.ribeiro@clinica.com', '(63) 98888-4444', 12);

-- id, data, valor, observacao, id_paciente, id_medico
INSERT INTO consulta (id, data, valor, observacao, id_paciente, id_medico) VALUES
    (1, '2026-08-25 08:00:00', 250.00, 'Consulta de rotina', 1, 7),
    (2, '2026-08-25 09:30:00', 300.00, 'Acompanhamento clínico', 2, 8),
    (3, '2026-08-25 11:00:00', 280.00, 'Avaliação inicial', 3, 9),
    (4, '2026-08-26 08:30:00', 250.00, 'Retorno médico', 4, 7),
    (5, '2026-08-26 10:00:00', 350.00, 'Consulta especializada', 5, 10),
    (6, '2026-08-26 14:00:00', 220.00, 'Consulta de rotina', 6, 8),
    (7, '2026-08-27 09:00:00', 300.00, 'Acompanhamento clínico', 1, 9),
    (8, '2026-08-27 10:30:00', 250.00, 'Retorno médico', 2, 10),
    (9, '2026-08-28 08:00:00', 280.00, 'Avaliação de exames', 3, 7),
    (10, '2026-08-28 15:00:00', 320.00, 'Consulta especializada', 5, 9);

ALTER SEQUENCE pessoa_seq RESTART WITH 11;
ALTER TABLE consulta ALTER COLUMN id RESTART WITH 11;

ALTER TABLE perfil ALTER COLUMN id RESTART WITH 5;
ALTER TABLE usuario ALTER COLUMN id RESTART WITH 13;
