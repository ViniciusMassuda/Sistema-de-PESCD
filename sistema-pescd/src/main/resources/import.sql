INSERT INTO Usuario (username, password, nome, role) VALUES ('admin', '123456', 'Administrador', 'ADMIN');
INSERT INTO Usuario (username, password, nome, role) VALUES ('sec', '123456', 'Secretario', 'SECRETARIO');
INSERT INTO Usuario (username, password, nome, role) VALUES ('prof', '123456', 'Prof Valter', 'PROFESSOR');
INSERT INTO Usuario (username, password, nome, role) VALUES ('juliano', '123456', 'Juliano', 'ALUNO');

INSERT INTO Oferta (nome, semestre, data_inicio, data_fim, professor_responsavel_id) VALUES ('PESCD I', '2026/1', '2026-03-01', '2026-07-15', 3);
INSERT INTO Oferta (nome, semestre, data_inicio, data_fim, professor_responsavel_id) VALUES ('PESCD II', '2025/2', '2025-08-01', '2025-12-15', 3);

INSERT INTO Inscricao (aluno_id, oferta_id) VALUES (4, 1);
