-- liquibase formatted sql
-- changeset lutece-global-pom:update_db_core-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Site property : add editable single-line text field for ANTS TOKEN
--
INSERT INTO core_datastore VALUES ('module.appointment.ants.site_property.token', '');
