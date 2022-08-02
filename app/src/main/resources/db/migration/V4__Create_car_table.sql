CREATE TABLE public.car
(
    id bigserial NOT NULL,
    registration_number character varying(20) NOT NULL,
    is_used_for_personal_purpose boolean NOT NULL DEFAULT false,
    PRIMARY KEY (id)
);