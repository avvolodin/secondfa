# --- !Ups
CREATE TABLE IF NOT EXISTS public.realm
(
    id text COLLATE pg_catalog."default" NOT NULL,
    key text COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    CONSTRAINT realm_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.identity
(
    id text COLLATE pg_catalog."default" NOT NULL,
    "realmId" text COLLATE pg_catalog."default" NOT NULL,
    token text COLLATE pg_catalog."default",
    login text COLLATE pg_catalog."default",
    CONSTRAINT identity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_identity_realm FOREIGN KEY ("realmId")
    REFERENCES public.realm (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID,
    CONSTRAINT u_identity_login UNIQUE ("realmId", login)
    );

INSERT INTO public.realm (id, key, description) VALUES ('adc41beb-8b09-4d58-82c7-f06ed3608d64', 'adc41beb-8b09-4d58-82c7-f06ed3608d64', 'Test');