-- V2: Identity domain foundation.
-- Companies, application users, company memberships, and a seeded role reference table.
-- Primary keys are application-generated random UUIDs (no DB-side generation).

CREATE TABLE company (
    id         uuid         NOT NULL,
    name       varchar(150) NOT NULL,
    created_at timestamptz  NOT NULL,
    updated_at timestamptz  NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

CREATE TABLE role (
    code        varchar(40)  NOT NULL,
    description varchar(200) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (code)
);

INSERT INTO role (code, description) VALUES
    ('OWNER',   'Company owner with full administrative control'),
    ('ADMIN',   'Administrator with management privileges'),
    ('MANAGER', 'Manager of projects and teams'),
    ('WORKER',  'Standard field worker');

CREATE TABLE app_user (
    id            uuid         NOT NULL,
    email         varchar(254) NOT NULL,
    password_hash varchar(100) NOT NULL,
    full_name     varchar(150),
    status        varchar(20)  NOT NULL,
    created_at    timestamptz  NOT NULL,
    updated_at    timestamptz  NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

-- Case-insensitive global email uniqueness enforced at the database level,
-- independent of any application-side normalization.
CREATE UNIQUE INDEX uq_app_user_email_lower ON app_user (lower(email));

CREATE TABLE company_membership (
    id         uuid        NOT NULL,
    company_id uuid        NOT NULL,
    user_id    uuid        NOT NULL,
    role_code  varchar(40) NOT NULL,
    status     varchar(20) NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT pk_company_membership PRIMARY KEY (id),
    CONSTRAINT fk_membership_company FOREIGN KEY (company_id) REFERENCES company (id),
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_membership_role FOREIGN KEY (role_code) REFERENCES role (code),
    CONSTRAINT uq_membership_company_user UNIQUE (company_id, user_id)
);

-- company_id lookups are covered by the leftmost column of uq_membership_company_user;
-- user_id needs its own index for membership-by-user queries.
CREATE INDEX ix_membership_user ON company_membership (user_id);
