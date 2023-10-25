--
-- Structure for table accesscontrol_controller_age_config
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