package com.miragesql.miragesql.filter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.miragesql.miragesql.session.Session;
import com.miragesql.miragesql.session.SessionFactory;
import com.miragesql.miragesql.util.ExceptionUtil;

/**
 * This filter implements the  <em>Open Session in View</em> pattern in Mirage-SQL.
 * <p>
 * This filter has to be used with {@link Session} and manages transaction automatically through methods of that.
 *
 * @author Naoki Takezoe
 */
public class OpenSessionInViewFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(OpenSessionInViewFilter.class);

//	@Override
    /**{@inheritDoc}*/
    public void destroy() {
    }

//	@Override
    /**{@inheritDoc}*/
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        Session session = SessionFactory.getSession();

        // begin
        try {
            session.begin();

        } catch(Exception ex){
            logger.error("Failed to begin Session.");
            logger.error(ExceptionUtil.toString(ex));
            throw new RuntimeException(ex);
        }

        try {
            chain.doFilter(request, response);

            // commit
            if(!session.isRollbackOnly()){
                try {
                    session.commit();

                } catch(Exception ex){
                    logger.error("Failed to commit Session.");
                    logger.error(ExceptionUtil.toString(ex));

                    if(ex instanceof RuntimeException){
                        throw (RuntimeException) ex;
                    }

                    throw new RuntimeException(ex);
                }
            }
        } catch(Exception ex){
            session.setRollbackOnly();

        } finally {
            // rollback
            if(session.isRollbackOnly()){
                try {
                    session.rollback();

                } catch (Exception e) {
                    logger.error("Failed to rollback Session.");
                    logger.error(ExceptionUtil.toString(e));

                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }

                    throw new RuntimeException(e);
                }
            }

            // release
            try {
                session.release();

            } catch(Exception ex){
                logger.error("Failed to release Session.");
                logger.error(ExceptionUtil.toString(ex));

                if(ex instanceof RuntimeException){
                    throw (RuntimeException) ex;
                }

                throw new RuntimeException(ex);
            }
        }
    }

//	@Override
    /**{@inheritDoc}*/
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}
