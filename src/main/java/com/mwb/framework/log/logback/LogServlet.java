package com.mwb.framework.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

public class LogServlet extends HttpServlet {
    private static final long serialVersionUID = -4421563970243853612L;

    private static final String SKIPPED_LOGGERS = "SKIPPED_LOGGERS";
    private static final String EXISTING_PARAM_PREFIX = "existing-";
    private static final String NEW_LOGGER_NAME_PARAM = "newLoggerName";
    private static final String NEW_LOGGER_LEVEL_PARAM = "";

    private Set<String> skippedLoggerSet;

    private org.slf4j.Logger logger = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        logger = LoggerFactory.getLogger(LogServlet.class);

        skippedLoggerSet = new HashSet<String>();
        String skippedLoggers = config.getInitParameter(SKIPPED_LOGGERS);

        if (StringUtils.isNotBlank(skippedLoggers)) {
            String[] loggerNames = skippedLoggers.split(",");

            for (String loggerName : loggerNames) {
                loggerName = loggerName.trim();
                if (StringUtils.isNotBlank(loggerName)) {
                    skippedLoggerSet.add(loggerName);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            Map<?, ?> parameterMap = request.getParameterMap();

            if (logger instanceof Logger) {
            	
            	if (parameterMap == null || parameterMap.isEmpty()) {
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));

                    response.setContentType("text/html");

                    writer.println("<html>");
                    writer.println("<body>");
                    writer.println("<form method=\"post\"><table align=\"center\">");

                    List<Logger> configuredLoggers = new ArrayList<Logger>();
                    List<String> configuredLoggerNames = new ArrayList<String>();
                    
                    List<Logger> loggers = ((Logger)logger).getLoggerContext().getLoggerList();
                	
                    Logger rootLogger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                    
                    for (Logger currentLogger: loggers) {
                    	String currentLoggerName = currentLogger.getName();

                        if (currentLogger.getLevel() != null && !configuredLoggerNames.contains(currentLoggerName)
                                && !currentLogger.equals(rootLogger)) {
                            configuredLoggers.add(currentLogger);
                            configuredLoggerNames.add(currentLoggerName);
                        }
                    }
                    
                    Collections.sort(configuredLoggers, new Comparator<Logger>() {
                        @Override
                        public int compare(Logger l1, Logger l2) {
                            return l1.getName().compareTo(l2.getName());
                        }

                    });

                    if (StringUtils.isNotBlank(rootLogger.getName())) {
                        configuredLoggers.add(0, rootLogger);
                    }

                    writer.println("<tr><td colspan=\"3\" align=\"center\"><b>Available Loggers</b></td><td>");
                    for (Logger configuredLogger : configuredLoggers) {
                        String loggerName = configuredLogger.getName();

                        if (skippedLoggerSet.contains(loggerName)) {
                            continue;
                        }

                        writer.println("<tr><td colspan=\"2\"><b>" + loggerName + "</b></td><td>");
                        writer.println("<select name=\"" + EXISTING_PARAM_PREFIX + loggerName + "\">");

                        String loggerLevel = configuredLogger.getLevel().toString();
                        if ("OFF".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"OFF\" selected>OFF</option>");
                        } else {
                            writer.println("<option value=\"OFF\">OFF</option>");
                        }

                        if ("FATAL".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"FATAL\" selected>FATAL</option>");
                        } else {
                            writer.println("<option value=\"FATAL\">FATAL</option>");
                        }

                        if ("ERROR".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"ERROR\" selected>ERROR</option>");
                        } else {
                            writer.println("<option value=\"ERROR\">ERROR</option>");
                        }

                        if ("WARN".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"WARN\" selected>WARN</option>");
                        } else {
                            writer.println("<option value=\"WARN\">WARN</option>");
                        }

                        if ("INFO".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"INFO\" selected>INFO</option>");
                        } else {
                            writer.println("<option value=\"INFO\">INFO</option>");
                        }

                        if ("DEBUG".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"DEBUG\" selected>DEBUG</option>");
                        } else {
                            writer.println("<option value=\"DEBUG\">DEBUG</option>");
                        }

                        if ("TRACE".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"TRACE\" selected>TRACE</option>");
                        } else {
                            writer.println("<option value=\"TRACE\">TRACE</option>");
                        }

                        if ("ALL".equalsIgnoreCase(loggerLevel)) {
                            writer.println("<option value=\"ALL\" selected>ALL</option>");
                        } else {
                            writer.println("<option value=\"ALL\">ALL</option>");
                        }

                        writer.println("</td></tr>");
                    }

                    writer.println("<tr><td colspan=\"3\">&nbsp;</td></tr>");
                    writer.println("<tr><td colspan=\"3\" align=\"left\">Enable logging for any package or class (e.g. \"com.foo\" or \"com.foo.Bar\"):</td></tr>");
                    writer.println("<tr><td colspan=\"2\"><input type=\"text\" size=\"60\" name=\"" + NEW_LOGGER_NAME_PARAM
                            + "\"></td>");
                    writer.println("<td><select name=\"" + NEW_LOGGER_LEVEL_PARAM + "\">");
                    writer.println("<option value=\"OFF\" selected>OFF</option>");
                    writer.println("<option value=\"FATAL\">FATAL</option>");
                    writer.println("<option value=\"ERROR\">ERROR</option>");
                    writer.println("<option value=\"WARN\">WARN</option>");
                    writer.println("<option value=\"INFO\">INFO</option>");
                    writer.println("<option value=\"DEBUG\">DEBUG</option>");
                    writer.println("<option value=\"TRACE\">TRACE</option>");
                    writer.println("<option value=\"ALL\">ALL</option>");
                    writer.println("</td></tr>");

                    writer.println("<tr><td colspan=\"3\" align=\"center\"><input type=\"submit\" value=\"Submit\"></td></tr>");
                    writer.println("</table></form>");
                    writer.println("</body>");
                    writer.println("</html>");

                    writer.flush();
                } else {
                    Iterator<?> iterator = parameterMap.keySet().iterator();

                    Logger logger = null;
                    String loggerName = null;
                    String newLogLevel = null;
                    while (iterator.hasNext()) {
                        String parameter = (String) iterator.next();

                        if (parameter.startsWith(EXISTING_PARAM_PREFIX)) {
                            loggerName = parameter.substring(EXISTING_PARAM_PREFIX.length());

                            if (skippedLoggerSet.contains(loggerName)) {
                                continue;
                            }

                            if (org.slf4j.Logger.ROOT_LOGGER_NAME.equalsIgnoreCase(loggerName)) {
                                logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                            } else {
                                logger = (Logger)LoggerFactory.getLogger(loggerName);
                            }

                            newLogLevel = request.getParameter(parameter);
                            if (StringUtils.isNotBlank(newLogLevel)
                                    && !newLogLevel.equalsIgnoreCase(logger.getLevel().toString())) {
                                logger.setLevel(Level.toLevel(newLogLevel));
                            }
                        }
                    }

                    loggerName = request.getParameter(NEW_LOGGER_NAME_PARAM);
                    newLogLevel = request.getParameter(NEW_LOGGER_LEVEL_PARAM);
                    if (StringUtils.isNotBlank(loggerName) && !skippedLoggerSet.contains(loggerName)) {
                        logger = (Logger)LoggerFactory.getLogger(loggerName);
                        if (logger.getLevel() == null) {
                            logger.setLevel(Level.toLevel(newLogLevel));
                        }
                    }

                    response.sendRedirect(request.getRequestURI());
                }
            	
            }
                        
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

}
