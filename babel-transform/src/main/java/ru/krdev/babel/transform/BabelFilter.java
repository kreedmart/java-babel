package ru.krdev.babel.transform;

import java.io.IOException;
import java.io.PrintWriter;
import javax.script.ScriptException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sergekos
 */
public class BabelFilter implements Filter {
    private final BabelTransformer babel = new BabelTransformer();

    public BabelFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        ResponseCaptureWrapper captureResp = new ResponseCaptureWrapper(
                (HttpServletResponse) response);

        chain.doFilter(request, captureResp);
        
        try (PrintWriter pw = response.getWriter()) {
            String capture = captureResp.getCaptureAsString();
//            System.out.println("Capture: " + capture);            
            
            String transform = babel.transform(capture);
//            System.out.println("Transform: " + transform);

            response.setContentLength(transform.getBytes().length);
            
            pw.write(transform);

        } catch (ScriptException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
