CREATE TABLE public.invoice_entry
(
    id                     bigserial      NOT NULL,
    description            character varying(50),
    quantity               numeric(10, 2) NOT NULL DEFAULT 1,
    net_price              numeric(10, 2) NOT NULL,
    vat_value              numeric(10, 2) NOT NULL,
    vat_rate               bigint         NOT NULL,
    expense_related_to_car bigint,
    PRIMARY KEY (id)
);
