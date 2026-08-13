-- liquibase formatted sql
-- changeset appointment-ants:update_db_core-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Site property : add editable single-line text field for ANTS TOKEN
--
-- Same statement as init_core_ants.sql : a site upgrading from 1.0.1 gets the key here,
-- a fresh install gets it there. ON DUPLICATE KEY UPDATE is a deliberate no-op so that
-- running both leaves an already configured token untouched instead of aborting the
-- Liquibase run on a duplicate key.
--
INSERT INTO core_datastore ( entity_key, entity_value )
VALUES ( 'module.appointment.ants.site_property.token', '' )
ON DUPLICATE KEY UPDATE entity_key = entity_key;
