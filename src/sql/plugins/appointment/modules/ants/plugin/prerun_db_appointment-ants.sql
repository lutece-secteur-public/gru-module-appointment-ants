-- liquibase formatted sql
-- changeset appointment-ants:prerun_db_appointment-ants.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM core_datastore WHERE entity_key = 'core.plugins.status.appointment-ants.version'
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'accesscontrol_controller_slots_number_config'
-- comment Creates the controller table on a site where the module is installed without it
CREATE TABLE accesscontrol_controller_slots_number_config (
	id_access_controller int,
	param_name_ants_application_number varchar(255),
	param_name_slots_to_take_number varchar(255),
	comment long varchar,
	error_message varchar(100),
	PRIMARY KEY( id_access_controller )
);