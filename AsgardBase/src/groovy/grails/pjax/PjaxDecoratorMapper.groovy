package grails.pjax

import com.opensymphony.module.sitemesh.Decorator
import com.opensymphony.module.sitemesh.Page
import org.codehaus.groovy.grails.web.sitemesh.GrailsLayoutDecoratorMapper

import javax.servlet.http.HttpServletRequest

class PjaxDecoratorMapper extends GrailsLayoutDecoratorMapper {
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator decorator = super.getDecorator(request, page)
        if (request.getHeader("X-Pjax")) {
            log.debug("PJAX enabled")
            decorator = getNamedDecorator(request, "pjax")
        }

        return decorator
    }
}