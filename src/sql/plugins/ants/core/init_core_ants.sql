-- liquibase formatted sql
-- changeset appointment-ants:init_core_ants.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Site property : add editable single-line text field for ANTS TOKEN
--
INSERT INTO core_datastore VALUES ('module.appointment.ants.site_property.token', '');
