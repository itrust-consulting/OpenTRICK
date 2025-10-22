package lu.itrust.business.ts.usermanagement.helper;

import java.security.Principal;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CustomRequestCache
 *
 * Prevents Spring Security from saving API or background requests (like JSON or AJAX)
 * as the target URL for post-login redirection.
 * this avoids redirecting to JSON endpoints (e.g., /Task/In-progress) after login,
 * while still allowing normal page navigation to be remembmbered
 */
public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        boolean wantsHtml = accept != null && accept.contains("text/html");

        // Only save full-page HTML navigations (ignore API/AJAX/JSON calls)
        if (!"GET".equalsIgnoreCase(request.getMethod()) || !wantsHtml) {
            return;
        }

        if (uri.startsWith("/Api/") || uri.startsWith("/Messaging") || uri.startsWith("/Task/")) {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
       
                return;
            }
        }

        super.saveRequest(request, response);
    }
}
