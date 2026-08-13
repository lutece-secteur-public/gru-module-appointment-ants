-- liquibase formatted sql
-- changeset appointment-ants:init_core_ants.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Site property : add editable single-line text field for ANTS TOKEN
--
-- ON DUPLICATE KEY UPDATE is a deliberate no-op : it keeps the script idempotent on a
-- database where the key already exists, without overwriting a token already configured.
-- A plain INSERT aborts the whole Liquibase run with a duplicate key error, and since
-- liquibase.failOnError defaults to true, it stops the web application from starting.
--
INSERT INTO core_datastore ( entity_key, entity_value )
VALUES ( 'module.appointment.ants.site_property.token', '' )
ON DUPLICATE KEY UPDATE entity_key = entity_key;
