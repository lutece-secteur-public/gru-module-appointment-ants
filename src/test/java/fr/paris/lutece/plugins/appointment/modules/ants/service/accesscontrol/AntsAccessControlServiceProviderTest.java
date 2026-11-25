/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.ants.service.accesscontrol;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.accesscontrol.business.AccessControl;
import fr.paris.lutece.plugins.accesscontrol.business.AccessControlHome;
import fr.paris.lutece.plugins.accesscontrol.business.AccessController;
import fr.paris.lutece.plugins.accesscontrol.business.AccessControllerHome;
import fr.paris.lutece.plugins.accesscontrol.business.AccessControlResourceHome;
import fr.paris.lutece.plugins.accesscontrol.service.AccessControlServiceProvider;
import fr.paris.lutece.plugins.appointment.modules.ants.business.accesscontrol.config.SlotsNumberAccessControllerConfig;
import fr.paris.lutece.plugins.appointment.modules.ants.business.accesscontrol.config.SlotsNumberAccessControllerConfigDAO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import freemarker.core.HTMLOutputFormat;
import jakarta.enterprise.inject.spi.CDI;

import java.util.Locale;

/**
 * The ANTS access control service provider finds the controllers of the access control it is given, whose id differs from
 * the ids of its controllers.
 */
public class AntsAccessControlServiceProviderTest extends LuteceTestCase
{
    private static final String SESSION_ATTRIBUTE_CODES = "APPOINTMENT_CODE_PREDEMANDE";
    private static final String SESSION_ATTRIBUTE_MESSAGE = "LUTECE_PORTAL_MESSAGE";
    private static final String ERROR_MESSAGE = "Wrong number of slots";
    private static final String RESOURCE_TYPE = "APPOINTMENT_FORM";
    private static final int ID_FORM_ANTS = 990001;
    private static final int ID_FORM_FREE = 990002;

    private final SlotsNumberAccessControllerConfigDAO _dao = new SlotsNumberAccessControllerConfigDAO( );
    private AccessControl _otherAccessControl;
    private AccessControl _accessControl;
    private AccessController _controller;

    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        // another access control with controllers, so that the id of the access control tested is not the id of its controller
        _otherAccessControl = AccessControlHome.create( accessControl( "other" ) );
        _accessControl = AccessControlHome.create( accessControl( "ants" ) );
        AccessController otherController = AccessControllerHome.create( controller( _otherAccessControl.getId( ) ) );
        if ( otherController.getId( ) + 1 == _accessControl.getId( ) )
        {
            AccessControllerHome.create( controller( _otherAccessControl.getId( ) ) );
        }
        _controller = AccessControllerHome.create( controller( _accessControl.getId( ) ) );

        SlotsNumberAccessControllerConfig config = new SlotsNumberAccessControllerConfig( );
        config.setIdAccessController( _controller.getId( ) );
        config.setErrorMessage( ERROR_MESSAGE );
        _dao.insert( config );
    }

    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        AccessControlResourceHome.removeByResource( ID_FORM_ANTS, RESOURCE_TYPE );
        _dao.delete( _controller.getId( ) );
        for ( AccessControl accessControl : new AccessControl [ ] {
                _accessControl, _otherAccessControl
        } )
        {
            for ( AccessController controller : AccessControllerHome.getAccessControllersListByAccessControlId( accessControl.getId( ) ) )
            {
                AccessControllerHome.remove( controller.getId( ) );
            }
            AccessControlHome.remove( accessControl.getId( ) );
        }
        super.tearDown( );
    }

    /**
     * The number of people matches the pre-demand codes kept in session: the user goes on, without a redirection.
     */
    @Test
    public void testValidSlotsNumberGoesOn( )
    {
        assertNotEquals( _accessControl.getId( ), _controller.getId( ) );
        MockHttpServletRequest request = request( "2" );
        request.getSession( true ).setAttribute( SESSION_ATTRIBUTE_CODES, "ABCDE12345,FGHIJ67890" );

        XPage page = provider( ).redirectToAccessControlXPage( request, 1, "appointment", _accessControl.getId( ) );

        assertNull( page );
        assertNull( request.getSession( ).getAttribute( SESSION_ATTRIBUTE_MESSAGE ) );
    }

    /**
     * The number of people differs from the pre-demand codes kept in session: the error message of the controller is shown.
     */
    @Test
    public void testWrongSlotsNumberShowsTheErrorMessage( )
    {
        MockHttpServletRequest request = request( "3" );
        request.getSession( true ).setAttribute( SESSION_ATTRIBUTE_CODES, "ABCDE12345,FGHIJ67890" );

        XPage page = provider( ).redirectToAccessControlXPage( request, 1, "appointment", _accessControl.getId( ) );

        assertNotNull( page );
        assertNotNull( request.getSession( ).getAttribute( SESSION_ATTRIBUTE_MESSAGE ) );
    }

    /**
     * The error message of the controller is written as text in the site message.
     */
    @Test
    public void testErrorMessageIsWrittenAsText( ) throws Exception
    {
        SlotsNumberAccessControllerConfig config = _dao.load( _controller.getId( ) );
        config.setErrorMessage( "Nombre <b>incorrect</b> & \"autre\"" );
        _dao.store( config );
        MockHttpServletRequest request = request( "3" );
        request.getSession( true ).setAttribute( SESSION_ATTRIBUTE_CODES, "ABCDE12345,FGHIJ67890" );

        provider( ).redirectToAccessControlXPage( request, 1, "appointment", _accessControl.getId( ) );

        SiteMessage message = (SiteMessage) request.getSession( ).getAttribute( SESSION_ATTRIBUTE_MESSAGE );
        assertNotNull( message );
        String strMarkup = HTMLOutputFormat.INSTANCE.getMarkupString( message.getMarkup( Locale.FRENCH ) );
        assertEquals( "Nombre &lt;b&gt;incorrect&lt;/b&gt; &amp; &quot;autre&quot;", strMarkup );
        assertEquals( SiteMessage.TYPE_ERROR, message.getType( ) );
    }

    /**
     * The booking of a form protected by the ANTS controller ends (plugin-appointment cleans the access control data of the
     * form): the pre-demand codes are consumed, a new booking needs a new pre-demand check.
     */
    @Test
    public void testCodesAreConsumedWhenTheBookingEnds( )
    {
        provider( ).createOrUpdateAccessControlResource( ID_FORM_ANTS, RESOURCE_TYPE, _accessControl.getId( ) );
        MockHttpServletRequest request = request( "1" );
        request.getSession( true ).setAttribute( SESSION_ATTRIBUTE_CODES, "ABCDE12345" );
        assertNull( provider( ).redirectToAccessControlXPage( request, ID_FORM_ANTS, RESOURCE_TYPE, _accessControl.getId( ) ) );

        provider( ).deleteSessionDataForResource( request, ID_FORM_ANTS, RESOURCE_TYPE );

        assertNull( request.getSession( ).getAttribute( SESSION_ATTRIBUTE_CODES ) );
        assertNotNull( provider( ).redirectToAccessControlXPage( request, ID_FORM_ANTS, RESOURCE_TYPE, _accessControl.getId( ) ) );
    }

    /**
     * The booking of a form that is not protected by the ANTS controller leaves the pre-demand codes in session.
     */
    @Test
    public void testCodesAreKeptWhenAnotherFormIsBooked( )
    {
        MockHttpServletRequest request = request( "1" );
        request.getSession( true ).setAttribute( SESSION_ATTRIBUTE_CODES, "ABCDE12345" );

        provider( ).deleteSessionDataForResource( request, ID_FORM_FREE, RESOURCE_TYPE );

        assertEquals( "ABCDE12345", request.getSession( ).getAttribute( SESSION_ATTRIBUTE_CODES ) );
    }

    /**
     * The reference given to reschedule an appointment opens the protected form only for an appointment of that form,
     * not cancelled.
     */
    @Test
    public void testRescheduleReferenceMustBeAnAppointmentOfTheForm( )
    {
        AppointmentDTO appointment = new AppointmentDTO( );
        appointment.setIdForm( 5 );

        assertTrue( SlotsNumberAccessControllerType.isAppointmentOfForm( appointment, "5" ) );
        assertFalse( SlotsNumberAccessControllerType.isAppointmentOfForm( appointment, "7" ), "appointment of another form" );
        assertFalse( SlotsNumberAccessControllerType.isAppointmentOfForm( appointment, null ), "no form in the request" );
        assertFalse( SlotsNumberAccessControllerType.isAppointmentOfForm( null, "5" ), "unknown reference" );
        appointment.setIsCancelled( true );
        assertFalse( SlotsNumberAccessControllerType.isAppointmentOfForm( appointment, "5" ), "cancelled appointment" );
    }

    private static AccessControlServiceProvider provider( )
    {
        return CDI.current( ).select( AccessControlServiceProvider.class ).get( );
    }

    private static MockHttpServletRequest request( String strNbPlacesToTake )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "nbPlacesToTake", strNbPlacesToTake );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        return request;
    }

    private static AccessControl accessControl( String strName )
    {
        AccessControl accessControl = new AccessControl( );
        accessControl.setName( strName );
        accessControl.setDescription( strName );
        accessControl.setEnabled( true );
        accessControl.setWorkgroup( "all" );
        accessControl.setCreationDate( new java.sql.Date( System.currentTimeMillis( ) ) );

        return accessControl;
    }

    private static AccessController controller( int nIdAccessControl )
    {
        AccessController controller = new AccessController( );
        controller.setType( SlotsNumberAccessControllerType.BEAN_NAME );
        controller.setIdAccesscontrol( nIdAccessControl );
        controller.setOrder( 1 );
        controller.setBoolCond( "AND" );

        return controller;
    }
}
