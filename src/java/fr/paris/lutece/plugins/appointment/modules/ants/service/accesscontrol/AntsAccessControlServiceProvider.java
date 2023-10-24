package fr.paris.lutece.plugins.appointment.modules.ants.service.accesscontrol;

import fr.paris.lutece.plugins.accesscontrol.business.AccessController;
import fr.paris.lutece.plugins.accesscontrol.business.AccessControllerHome;
import fr.paris.lutece.plugins.accesscontrol.service.AccessControlServiceProvider;
import fr.paris.lutece.plugins.accesscontrol.service.IAccessControllerType;
import fr.paris.lutece.portal.service.message.CustomSiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AntsAccessControlServiceProvider extends AccessControlServiceProvider {

    public static final String BEAN_NAME = "accesscontrol.accessControlServiceProvider";

    private static final String URL_PORTAL = "Portal.jsp";

    private static final String MARKER_NB_PLACES_TO_TAKE = "nbPlacesToTake";
    public static final String SITE_MESSAGE_JSP = "jsp/site/SiteMessage.jsp";

    private AccessController _currentController;

    @Override
    public XPage redirectToAccessControlXPage(HttpServletRequest request, int idResource, String resourceType, int idAccessControl ) {

        // Get the controller
        IAccessControllerType controllerType = getAntsAccessController( idAccessControl );

        // If the form uses a standard AccessController
        if( !isAntsAccessController( controllerType ) )
        {
            // Redirect to the standard implementation of the AccessControl XPage
            return super.redirectToAccessControlXPage( request, idResource, resourceType, idAccessControl );
        }
        // If the form uses the specific ANTS access controller, we override the main execution
        else
        {
            return validateController( request, ( SlotsNumberAccessControllerType ) controllerType, idResource, resourceType );
        }
    }

    private IAccessControllerType getAntsAccessController( int idAccessControl )
    {
        _currentController = AccessControllerHome.findByPrimaryKey( idAccessControl );
        return SpringContextService.getBean( _currentController.getType( ) );
    }

    private boolean isAntsAccessController( IAccessControllerType controllerType )
    {
        return ( controllerType instanceof SlotsNumberAccessControllerType );
    }

    public XPage validateController( HttpServletRequest request, SlotsNumberAccessControllerType controllerType, int idResource, String resourceType )
    {
        String validationResult = controllerType.validate( request, _currentController );

        if ( validationResult == null )
        {
            return null;
        }

        return getErrorPage( request, idResource, resourceType, validationResult );
    }

    private XPage getErrorPage( HttpServletRequest request, int idResource, String resourceType, String validationResult )
    {
        MVCUtils.getLogger( ).error( "Validation error: {}", validationResult );
        HttpServletResponse response = LocalVariables.getResponse( );
        SiteMessage message = new CustomSiteMessage( "Erreur de validation : ", validationResult, null, SiteMessage.TYPE_ERROR, SiteMessage.TYPE_BUTTON_BACK, null );
        HttpSession session = request.getSession( true );
        session.setAttribute( "LUTECE_PORTAL_MESSAGE", message );
        String strTarget = AppPathService.getBaseUrl( request ) + SITE_MESSAGE_JSP;
        try {
            response.sendRedirect( strTarget );
        } catch ( IOException e ) {
            MVCUtils.getLogger( ).error( "Unable to redirect : {} : {}", strTarget, e.getMessage( ), e );
        }

        return new XPage();
    }
}
