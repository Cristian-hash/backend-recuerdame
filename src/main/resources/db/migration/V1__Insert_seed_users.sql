-- Contraseña para todos los usuarios semilla es "123456"
-- El hash (bcrypt) corresponde a: $2a$10$mI84.V0N6sC.aLdF.Ea21eB4es1r5E5s2u9yqN.C2G6vjX.Q/z.aG

INSERT INTO users (full_name, email, password_hash, role, is_active) VALUES
('Sra. Mirna', 'mirna@upgrade.com', '$2a$10$mI84.V0N6sC.aLdF.Ea21eB4es1r5E5s2u9yqN.C2G6vjX.Q/z.aG', 'GERENCIA', true),
('Karlangela', 'karlangela@upgrade.com', '$2a$10$mI84.V0N6sC.aLdF.Ea21eB4es1r5E5s2u9yqN.C2G6vjX.Q/z.aG', 'FINANZAS', true),
('Ronald', 'ronald@upgrade.com', '$2a$10$mI84.V0N6sC.aLdF.Ea21eB4es1r5E5s2u9yqN.C2G6vjX.Q/z.aG', 'LOGISTICA', true);
