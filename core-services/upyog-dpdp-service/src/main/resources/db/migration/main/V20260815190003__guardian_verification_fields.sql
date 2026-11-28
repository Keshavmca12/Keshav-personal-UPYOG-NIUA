ALTER TABLE ug_dpdp_guardian
    ADD COLUMN IF NOT EXISTS verification_method VARCHAR(64),
    ADD COLUMN IF NOT EXISTS authority_order_ref VARCHAR(256);
