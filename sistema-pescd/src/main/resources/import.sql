-- Usuários
INSERT INTO Usuario (username, password, nome, role) VALUES ('admin', '123456', 'Administrador', 'ADMIN');
INSERT INTO Usuario (username, password, nome, role) VALUES ('sec', '123456', 'Secretario', 'SECRETARIO');
INSERT INTO Usuario (username, password, nome, role) VALUES ('prof', '123456', 'Professor Responsavel', 'PROFESSOR');
INSERT INTO Usuario (username, password, nome, role) VALUES ('aluno', '123456', 'Aluno 1', 'ALUNO');

-- Ofertas (com todos os campos obrigatórios)
INSERT INTO Oferta (nome, semestre, data_inicio, data_fim, professor_responsavel_id, concluida_professor, encerrada_secretario)
VALUES ('PESCD I - Estágio Docente', '2026/1', '2026-03-01', '2026-07-15', 3, false, false);

INSERT INTO Oferta (nome, semestre, data_inicio, data_fim, professor_responsavel_id, concluida_professor, encerrada_secretario)
VALUES ('PESCD II - Prática de Ensino', '2026/1', '2026-03-01', '2026-07-15', 3, false, false);

-- Inscrição do aluno na primeira oferta (com status)
INSERT INTO Inscricao (aluno_id, oferta_id, status)
VALUES (4, 1, 'NAO_ENVIADO');