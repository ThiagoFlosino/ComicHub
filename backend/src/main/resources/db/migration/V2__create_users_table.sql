-- Task #004 – Tabela de utilizadores (auth via Cognito JWT)
CREATE TABLE users
(
    id            UUID         PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    auth_provider VARCHAR(50)  NOT NULL,

    CONSTRAINT users_email_check CHECK (email ~* '^[^@]+@[^@]+\.[^@]+$')
);

COMMENT ON TABLE  users              IS 'Utilizadores autenticados via Cognito';
COMMENT ON COLUMN users.id          IS 'Cognito sub (UUID)';
COMMENT ON COLUMN users.auth_provider IS 'Ex: COGNITO, GOOGLE, APPLE';
