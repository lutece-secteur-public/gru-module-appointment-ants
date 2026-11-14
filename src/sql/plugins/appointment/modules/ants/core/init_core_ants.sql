-- liquibase formatted sql
-- changeset lutece-global-pom:init_core_ants.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_datastore WHERE entity_key = 'module.appointment.ants.site_property.token'

--
-- Site property : add editable single-line text field for ANTS TOKEN
--
-- The precondition keeps the script idempotent on a database where the key already
-- exists, without overwriting a token already configured : Liquibase marks the changeset
-- as run instead of aborting on a duplicate key. The plain INSERT stays portable to the
-- databases the SQL scripts are converted for (ON DUPLICATE KEY UPDATE is MySQL only).
--
INSERT INTO core_datastore ( entity_key, entity_value )
VALUES ( 'module.appointment.ants.site_property.token', '' );
