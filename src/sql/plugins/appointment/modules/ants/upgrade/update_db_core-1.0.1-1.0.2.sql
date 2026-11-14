-- liquibase formatted sql
-- changeset lutece-global-pom:update_db_core-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_datastore WHERE entity_key = 'module.appointment.ants.site_property.token'

--
-- Site property : add editable single-line text field for ANTS TOKEN
--
-- Same statement as init_core_ants.sql : a site upgrading from 1.0.1 gets the key here,
-- a fresh install gets it there. The precondition skips the INSERT when the key already
-- exists, so running both leaves an already configured token untouched instead of
-- aborting the Liquibase run on a duplicate key.
--
INSERT INTO core_datastore ( entity_key, entity_value )
VALUES ( 'module.appointment.ants.site_property.token', '' );
