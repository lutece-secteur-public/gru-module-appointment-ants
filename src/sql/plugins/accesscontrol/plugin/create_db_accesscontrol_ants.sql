-- liquibase formatted sql
-- changeset appointment-ants:create_db_accesscontrol_ants.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table accesscontrol_controller_slots_number_config
--
-- This table extends the accesscontrol schema with the controller brought by
-- module-appointment-ants. It must NOT be declared in create_db_accesscontrol.sql :
-- that file name already belongs to plugin-accesscontrol, and shipping the same
-- webapp path from two artifacts makes one silently overwrite the other when the
-- webapp is exploded.
--
DROP TABLE IF EXISTS accesscontrol_controller_slots_number_config;
CREATE TABLE accesscontrol_controller_slots_number_config (
	id_access_controller int,
	param_name_ants_application_number varchar(255),
	param_name_slots_to_take_number varchar(255),
	comment long varchar,
	error_message varchar(100),
	PRIMARY KEY( id_access_controller )
);