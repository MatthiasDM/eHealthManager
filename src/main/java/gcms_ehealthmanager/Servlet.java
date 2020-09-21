/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

/**
 *
 * @author Matthias
 */
import be.ehealth.technicalconnector.exception.ConnectorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import gcms_ehealthmanager.database.DatabaseActions;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.http.auth.AuthenticationException;

/**
 *
 * @author matmey
 */
@WebServlet(name = "Servlet", urlPatterns = {"/api/*"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 20, // 20MB
        maxFileSize = 1024 * 1024 * 20, // 20MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class Servlet extends HttpServlet {

    private ServletContext context;
    eHealthSessionManager sessionManager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        this.context = config.getServletContext();
        sessionManager = new eHealthSessionManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder sb = new StringBuilder();
            Response actionResponse = null;
            ActionManager aM;
            String tempURL = context.getRealPath("/temp");
            aM = new ActionManager(request, tempURL, sessionManager);
            actionResponse = aM.startAction();
            sb.append(actionResponse.sb);

            if (sb.toString().length() > 0) {
                response.setContentType("text/xml");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(sb.toString());
                response.setStatus(actionResponse.responseStatus);
            } else {
                response.setStatus(actionResponse.responseStatus);
            }
        } catch (ClassNotFoundException | ConnectorException | JsonProcessingException | AuthenticationException | NoSuchFieldException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JsonProcessingException {
        try {
            StringBuilder sb = new StringBuilder();
            Response actionResponse = null;
            ActionManager aM;
            String tempURL = context.getRealPath("/temp");
            aM = new ActionManager(request, tempURL, sessionManager);
            actionResponse = aM.startAction();
            sb.append(actionResponse.sb);

            if (sb.toString().length() > 0) {
                response.setContentType("text/xml");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(sb.toString());
                response.setStatus(actionResponse.responseStatus);
            } else {
                response.setStatus(actionResponse.responseStatus);
            }
        } catch (ClassNotFoundException | ConnectorException | JsonProcessingException | AuthenticationException | NoSuchFieldException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder sb = new StringBuilder();
            Response actionResponse = null;
            ActionManager aM;
            String tempURL = context.getRealPath("/temp");
            aM = new ActionManager(request, tempURL, sessionManager);
            actionResponse = aM.startAction();
            sb.append(actionResponse.sb);

            if (sb.toString().length() > 0) {
                response.setContentType("text/xml");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(sb.toString());
                response.setStatus(actionResponse.responseStatus);
            } else {
                response.setStatus(actionResponse.responseStatus);
            }
        } catch (ClassNotFoundException | ConnectorException | JsonProcessingException | AuthenticationException | NoSuchFieldException ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Servlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class Response {

        int responseStatus;
        StringBuilder sb;

        public Response() {
        }

        public Response(int responseStatus, StringBuilder sb) {
            this.responseStatus = responseStatus;
            this.sb = sb;
        }

        public StringBuilder getSb() {
            return sb;
        }

        public void setSb(StringBuilder sb) {
            this.sb = sb;
        }

    }

    public static String getClientPCName(String ipAddr) {
        String host = "";
        try {
            InetAddress addr = InetAddress.getByName(ipAddr);
            host = addr.getHostName();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Servlet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return host;
    }

    class ActionManager {

        String cookie, URI, baseURL, tempURL;
        Map<String, String[]> requestParameters = new HashMap<String, String[]>();
        Collection<Part> parts;
        int responseStatus = HttpServletResponse.SC_ACCEPTED;
        int urlStartIndex = 2;
        String method;
        List<String> pathVariableMap;
        eHealthSessionManager sessionManager;

        public ActionManager(HttpServletRequest request, String tempURL, eHealthSessionManager sessionManager) throws ClassNotFoundException {
            this.requestParameters = request.getParameterMap();
            this.URI = request.getRequestURL().toString();
            this.baseURL = Core.getBaseUrl(request);
            this.tempURL = tempURL;
            this.method = request.getMethod();
            this.pathVariableMap = Core.getPathParamtersFromUrl(URI);
            this.sessionManager = sessionManager;
            if (requestParameters.get("LCMS_session") != null) {
                cookie = requestParameters.get("LCMS_session")[0];
            }

        }

        public String getCookie() {
            return cookie;
        }

        public String nextVariable() {
            String variable = null;
            if (urlStartIndex <= pathVariableMap.size()) {
                variable = pathVariableMap.get(urlStartIndex);
                urlStartIndex++;
            }
            return variable;
        }

        public Response startAction() throws ClassNotFoundException, IOException, NoSuchFieldException, ConnectorException, JsonProcessingException, AuthenticationException, Exception {
            StringBuilder sb = new StringBuilder();
            String pathVariable = null;
            if (Core.getProp(requestParameters.get("key")[0]).equals("1")) {
                pathVariable = nextVariable();
                if (pathVariable.equals("messages")) {
                    if (method.equals("GET")) {
                        int page = requestParameters.get("page") != null ? Integer.parseInt(requestParameters.get("page")[0]) : 1;
                        int rows = requestParameters.get("rows") != null ? Integer.parseInt(requestParameters.get("rows")[0]) : 50;
                        BasicDBObject filterObject = DatabaseActions.createFilterObject(requestParameters.get("filters"));
                        sb.append(eHealthboxManager.getMessages(page, rows, filterObject));
                    } else {
                        if (method.equals("PUT")) {

                            if (sessionManager.start_session()) {
                                sb.append(eHealthboxManager.checkMessages());
                                //sb.append("Testomgeving checking messages success");
                            } else {
                                sb.append("Unable to create valid session");
                            }
                        }
                    }
                }
                if (pathVariable.equals("file")) {
                    if (method.equals("GET")) {
                        String singleton = nextVariable();
                        sb.append(eHealthboxManager.getFile(singleton, baseURL, tempURL));
                    }
                }

                if (pathVariable.equals("dump")) {
                    if (method.equals("POST")) {
                        int period = Integer.parseInt(Core.getProp("dump-period"));
                        String regex = Core.getProp("dump-regex");
                        String dir = Core.getProp("dump-dir");
                        if (requestParameters.get("dump-period") != null) {
                            period = Integer.parseInt(requestParameters.get("dump-period")[0]);
                        }
                        if (requestParameters.get("dump-regex") != null) {
                            regex = (requestParameters.get("dump-regex")[0]);
                        }
                        if (requestParameters.get("dump-dir") != null) {
                            dir = (requestParameters.get("dump-dir")[0]);
                        }
                        sb.append(eHealthboxManager.dumpFiles(regex, dir, period));
                    }
                }

                if (pathVariable.equals("rr")) {
                    if (method.equals("GET")) {
                        if (sessionManager.start_session()) {
                            String ssin = nextVariable();
                            String response = ConsultRRManager.SearchPersonBySsin(ssin);
                            sb.append(response);
                        }
                    }
                }
                if (pathVariable.equals("nihii")) {
                    if (method.equals("GET")) {
                        if (sessionManager.start_session()) {
                            String nihii = nextVariable();
                            String response = AddressbookManager.getProfessionalContactInfo(nihii);
                            sb.append(response);
                        }
                    }
                }

            }

            //sb.append("succes");
            return new Response(responseStatus, sb);

        }

    }

}
