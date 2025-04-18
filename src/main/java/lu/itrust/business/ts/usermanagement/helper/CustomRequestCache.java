package lu.itrust.business.ts.usermanagement.helper;

import java.security.Principal;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().startsWith("/Messaging")) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                // Reject WebSock request if the user is not authenticated
                return;
            }
        }
        super.saveRequest(request, response);
    }
}
