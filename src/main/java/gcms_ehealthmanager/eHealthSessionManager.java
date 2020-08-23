/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import be.ehealth.businessconnector.ehbox.api.domain.Addressee;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.ConfigValidator;
import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.sts.SAMLTokenFactory;
import be.ehealth.technicalconnector.service.sts.security.SAMLToken;
import be.ehealth.technicalconnector.service.sts.security.impl.KeyStoreCredential;
import be.ehealth.technicalconnector.service.sts.utils.SAMLConverter;
import be.ehealth.technicalconnector.session.Session;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.session.SessionManager;
import be.ehealth.technicalconnector.utils.CertificateParser;
import be.ehealth.technicalconnector.utils.IdentifierType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;

/**
 *
 * @author Matthias
 */
public class eHealthSessionManager {

    private static String type, id, app;
    String configLocation = "";

    public eHealthSessionManager() {
        configLocation = Core.getBasePath() + "/" + Core.getProp("ehealth.properties");
        try {
            ConfigFactory.setConfigLocation(configLocation);
            System.out.println(ConfigFactory.getConfigLocation());
        } catch (TechnicalConnectorException ex) {
            Logger.getLogger(eHealthSessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Addressee getSender() throws TechnicalConnectorException {
        CertificateParser certParser = new CertificateParser(Session.getInstance().getSession().getSAMLToken().getCertificate());
        Addressee addressee = new Addressee(IdentifierType.valueOf(Core.getProp("ehealth.identifiertype")));
        addressee.setId(id);
        addressee.setQuality(Core.getProp("ehealth.quality"));
        addressee.setApplicationId(app);
        return addressee;
    }

    public boolean start_session() {
        boolean successfull = init_session();
        if (successfull) {
            SessionManager sessionmgmt = Session.getInstance();
            try {
                CertificateParser certParser = new CertificateParser(Session.getInstance().getSession().getSAMLToken().getCertificate());
                String holderofKey = Session.getInstance().getSession().getHolderOfKeyCredential().getCertificate().getSubjectDN().getName();
                type = certParser.getType();
                app = certParser.getApplication();
                id = certParser.getValue();
            } catch (TechnicalConnectorException ex) {
                Logger.getLogger(eHealthSessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return successfull;
    }

    public boolean init_session() {
        boolean valid_session = false;
        try {
            SessionManager sessionmgmt = Session.getInstance();

            Map<String, String> keystores = new HashMap<String, String>(3);
            //  valid_session = sessionmgmt.hasValidSession();
            if (!valid_session) {
                sessionmgmt.unloadSession();
                sessionmgmt.createFallbackSession(Core.getProp("ehealth.hokpassword"), Core.getProp("ehealth.hokpassword"), Core.getProp("ehealth.hokpassword"));
            } else {
                //if (DynamicSwing.infoBox("A Session already exists, do you want to close the old session and create a new one?", "Session already exists") == 0) {
                //   sessionmgmt.unloadSession();
                //  sessionmgmt.createFallbackSession(hokPassword, hokPassword, hokPassword);
                //}
            }
            valid_session = sessionmgmt.hasValidSession();
        } catch (SessionManagementException ex) {
            Logger.getLogger(eHealthSessionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TechnicalConnectorException ex) {
            Logger.getLogger(eHealthSessionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(eHealthSessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valid_session;
    }

    private static void storeAndReload(String indentPwd, String hokPwd, String encPwd, SessionManager sessionmgmt) throws IOException, TechnicalConnectorException {
        SessionItem item = sessionmgmt.getSession();
        Element originalAssertion = item.getSAMLToken().getAssertion();
        String serializedToken = SAMLConverter.toXMLString(originalAssertion);
        File temp = new File("tempToken");
        temp.deleteOnExit();
        IOUtils.write(serializedToken.getBytes(), new FileOutputStream(temp));
        sessionmgmt.unloadSession();
        Element savedAssertion = SAMLConverter.toElement(IOUtils.toString(new FileReader(temp)));
        ConfigValidator config = ConfigFactory.getConfigValidator();
        String hokKeystore = config.getProperty("sessionmanager.holderofkey.keystore");
        String hokAlias = config.getProperty("sessionmanager.holderofkey.alias", "authentication");
        SAMLToken token = SAMLTokenFactory.getInstance().createSamlToken(savedAssertion, new KeyStoreCredential(hokKeystore, hokAlias, hokPwd));

        sessionmgmt.loadSession(token, hokPwd, encPwd);
    }

    public void close_session() {
        SessionManager sessionmgmt = Session.getInstance();
        sessionmgmt.unloadSession();
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        eHealthSessionManager.type = type;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        eHealthSessionManager.id = id;
    }

    public static String getApp() {
        return app;
    }

    public static void setApp(String app) {
        eHealthSessionManager.app = app;
    }

}
