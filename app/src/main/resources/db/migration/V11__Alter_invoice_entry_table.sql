ALTER TABLE public.invoice_entry
    ADD CONSTRAINT vat_rate_fk FOREIGN KEY (vat_rate)
        REFERENCES public.vat (id);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT car_fk FOREIGN KEY (expense_related_to_car)
        REFERENCES public.car (id);

ALTER TABLE public.invoice_entry
    RENAME COLUMN expense_related_to_car to car_expense_is_related_to;