# Module appointment ants
This module facilitates the implementation of the user journey in the front-office. It includes a form for entering pre-application numbers, restricts access to the appointment booking form in the absence of a pre-application number, and automatically pre-fills the "pre-application number" field.

### Prerequisites

For being able to run the module appointment ants on you local machine, you need to have a tomcat server configured, eclipse, java 8, maven (at least 3.3.9), ant, and a mysql local instance (with mysqlWorkBench).

Configure `appointment-ants.properties` : 

Replace `<Insert valid ANTS API Token>` with your actual ANTS API token and session attiribute name `APPOINTMENT_CODE_PREDEMANDE` inside the .properties file


### Installing

You need to create a site (a new project on Eclipse) with a pom.xml that includes : lutece-core, module-appointment-ants, plugin-appointment, module-workflow-appointmentants, module-appointment-rest, plugin-workflow, module-workflow-appointment, module-appointment-solr, module-appointment-solrsearchapp, plugin-solr,

Once you have done this, you will have to get all the maven dependencies and create the site by running these two command lines on the workspace directory of the site : `mvn lutece:clean lutece:site:assembly`

Once the build is success, you will have a site-rendezvous-integration-XXX.war created on the target directory and a directory named site-rendezvous-integration-XXX.

You will have to go to this directory/WEB-INF/conf and edit the db.properties file to put the right login/password to access your local mysql instance.

To create the lutece schema, you will have to run the ant script in site-rendezvous-integration-XXX/WEB-INF/sql by running in a prompt shell the command:mvn antrun:run

To run the application, just put the site-rendezvous-integration-XXX.war to the webapps directory of your tomcat and launch : catalina jpda start

You will have access to the application at the urls :

http://localhost:8080/site-rendezvous-integration-XXX/jsp/admin/AdminLogin.jsp for the Back Office pages

http://localhost:8080/site-rendezvous-integration-XXX/jsp/site/Portal.jsp?page=appointmentants&view=predemandeForm for the front office pages

If you have development to make on the module appointment ants, you have to get from github the plugin directory and on eclipse import this project.
Once the development is done on the module, you have to build it with :
mvn clean install lutece:exploded -Dmaven.test.skip=true
and put the module-appointment-ants-XXX.jar created on the target directory site-rendezvous-integration-XXX\WEB-INF\lib of the webapp of your tomcat, to replace the old jar. It will avoid you to create again the site with the jar you've just built.

### Break down into code

From Eclipse, you have the possibility of running a debug mode.
For that, you have to start the tomcat instance like that : catalina jpda start
On Eclipse, in the debug configurations menu, create a new Remote Java Application (project : plugin appointment, connection properties : host : localhost, port : 8000)
Click on debug.
Put breakpoint where you want and debug !

## Site Configuration 
1 : Configure a workflow : create a new workflow with 3 states (Available => Created => Deleted) add 2 actions between them 'Create' with the task Add an appointment to the ANTS database' and 'Delete' with the task 'Delete an appointment from the ANTS database' 

2 : Configure an appointment form : create a new appointment form then assosiate it the workflow and the category (titres) 

3 : Add generic attribute type session : the title will be `predemande codes` and the session attribute `APPOINTMENT_CODE_PREDEMANDE`

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Rafik Yahiaoui** - *Initial work* - [ryahiaoui](https://github.com/ryahiaoui)

## License

Copyright (c) 2002-2023, Mairie de Paris
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

   1. Redistributions of source code must retain the above copyright notice
      and the following disclaimer.
 
   2. Redistributions in binary form must reproduce the above copyright notice
      and the following disclaimer in the documentation and/or other materials
      provided with the distribution.
 
   3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
      contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.
 
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
 
  License 1.0

