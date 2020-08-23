/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import gcms_ehealthmanager.database.MessageObjectWithAnnexes;
import be.ehealth.businessconnector.ehbox.api.domain.Document;
import be.ehealth.businessconnector.ehbox.api.domain.DocumentMessage;
import be.ehealth.businessconnector.ehbox.api.domain.exception.EhboxBusinessConnectorException;
import be.ehealth.businessconnector.ehbox.v3.builders.ConsultationMessageBuilder;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.exception.UnsealConnectorException;
import be.ehealth.technicalconnector.utils.MarshallerHelper;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetFullMessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gcms_ehealthmanager.database.DatabaseActions;
import gcms_ehealthmanager.database.FileObject;
import gcms_ehealthmanager.database.MessageObject;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author Matthias
 */
public class Core {

    static String dirName = getProp("app.root");
    static String baseURL = getProp("base.url");

    public static String getBasePath() {
        Class<?> clazz = Core.class;
        Package p = clazz.getPackage();
        return System.getProperties().getProperty("user.home") + "/" + p.getName();
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }

    private static FileObject createFileObject(String _id, String _filename, String _type, String _contenttype) {
        long now = Instant.now().toEpochMilli();
        FileObject fileObject = new FileObject();
        fileObject.setFileid(_id);
        fileObject.setType(_type);
        fileObject.setName(_filename);
        fileObject.setUpload_date(now);
        fileObject.setContent_type(_contenttype);
        return fileObject;
    }

    public static String saveMessageObjectWithAnnexes(MessageObjectWithAnnexes messageObjectWithAnnexes) throws FileNotFoundException, IOException, FileNotFoundException, JsonProcessingException, ClassNotFoundException {
        String fileName = "";
        String name = messageObjectWithAnnexes.getMessageObject().getMessageId();
        String messageData = messageObjectWithAnnexes.getMessageObject().getMessage();
        HashMap<String, byte[]> files = messageObjectWithAnnexes.getAnnexes();
        uploadFileToDatabase(messageData.getBytes(), messageData, name, name, ".ehealth", "txt/html");
        for (Map.Entry pair : files.entrySet()) {
            fileName = (String) pair.getKey();
            byte[] data = (byte[]) pair.getValue();
            uploadFileToDatabase(data, new String(data), name + "-" + fileName, fileName, ".ehealth", "txt/html");
        }
        ObjectMapper mapper = new ObjectMapper();
        org.bson.Document document = org.bson.Document.parse(mapper.writeValueAsString(messageObjectWithAnnexes.getMessageObject()));
        DatabaseActions.insertObject("ehealthbox", "gcms_ehealthmanager.database.MessageObject", document);
        return fileName;
    }

    public static void uploadFileToDatabase(byte[] byteData, String data, String id, String fileName, String type, String contenttype) throws JsonProcessingException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        FileObject fileobject = createFileObject(id, fileName, type, contenttype);
        DatabaseActions.insertFile(new ByteArrayInputStream(byteData), id, fileobject);
        org.bson.Document document = org.bson.Document.parse(mapper.writeValueAsString(fileobject));
        DatabaseActions.insertObject("fileobjects", FileObject.class.getCanonicalName(), document);
    }

    public static MessageObject getMessageFromDatabase(String _messageID) throws ClassNotFoundException, JsonProcessingException {
        MessageObject messageObject = new MessageObject();
        ObjectMapper mapper = new ObjectMapper();
        String fileName = _messageID;
        org.bson.Document messageDocument = null;
        try {
            messageDocument = DatabaseActions.getObject("ehealthbox", "gcms_ehealthmanager.database.MessageObject", "messageId", fileName);
            if (messageDocument != null) {
                messageObject = mapper.convertValue(messageDocument, MessageObject.class);
                return messageObject;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(messageDocument.toString());
            return null;
        }

    }

//    public static void 
    public static String getProp(String name) {

        try {
            Class<?> clazz = Core.class;
            Package p = clazz.getPackage();
            Properties prop = new Properties();
            String basePath = System.getProperties().getProperty("user.home") + "/" + p.getName();
            checkDir(basePath);
            String propFileName = p.getName() + ".properties";
            System.out.println("getProp() basepath: " + basePath + "(" + basePath + "/" + propFileName + ")");
            File f = new File(basePath + "/" + propFileName);
            prop.load(new StringReader(readAllLines(Arrays.asList(f)).toString()));
            return prop.getProperty(name);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String getTempDir() {
        Class<?> clazz = Core.class;
        Package p = clazz.getPackage();
        Properties prop = new Properties();
        String basePath = System.getProperties().getProperty("user.home") + "/" + p.getName() + "/temp";
        checkDir(basePath);
        return basePath;
    }

    public static String getExtension(String fileName) {
        char ch;
        int len;
        if (fileName == null
                || (len = fileName.length()) == 0
                || (ch = fileName.charAt(len - 1)) == '/' || ch == '\\'
                || //in the case of a directory
                ch == '.') //in the case of . or ..
        {
            return "";
        }
        int dotInd = fileName.lastIndexOf('.'),
                sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (dotInd <= sepInd) {
            return "";
        } else {
            return "." + fileName.substring(dotInd + 1).toLowerCase();
        }
    }

    public static boolean checkDir(String _directoryName) {
        File directory = new File(_directoryName);
        if (!directory.exists()) {
            directory.mkdir();
            return true;
        } else {
            return true;
        }
    }

    public static boolean checkFile(String _fileName) {
        File file = new File(_fileName);
        return file.exists();
    }

    public static StringBuffer readAllLines(List<File> files) {
        StringBuffer allLines = new StringBuffer();
        try {
            for (File f : files) {
                allLines.append(readAllLines(new StringBuffer(), f.toPath(), "UTF-8"));
                Logger
                        .getLogger(Core.class
                                .getName()).log(Level.INFO, "Succesfull read of file (" + (int) (f.length() / 1000) + "kb" + "): " + f.getName());

            }
        } catch (Exception e) {
            Logger.getLogger(Core.class
                    .getName()).log(Level.INFO, "Error while reading file. \n" + e.getMessage());
        }
        return allLines;
    }

    public static StringBuffer readAllLines(StringBuffer sb, Path p, String charset) throws IOException {
        String scan;
        FileReader file = new FileReader(p.toFile());
        BufferedReader br = new BufferedReader(file);

        while ((scan = br.readLine()) != null) {
            scan += "\r";
            sb.append(scan);
        }
        br.close();
        return sb;
    }

    public static Map<String, List<String>> getURLParams(String url) throws URISyntaxException {
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), Charset.forName("UTF-8"));
        Map<String, List<String>> parameterMap = new HashMap<>();
        for (NameValuePair param : params) {
            parameterMap.get(param.getName()).add(param.getValue());
        }
        return parameterMap;
    }

    public static List<String> getPathParamtersFromUrl(String url) {
        String[] path = url.split("\\?");
        String[] pathparams = path[0].split("\\//");
        String[] param = pathparams[1].split("\\/");
        List<String> p = Arrays.asList(param);
        // return p.stream().filter(x -> !x.contains(".") && !x.contains(":")).collect(Collectors.toList());
        return p.stream().filter(x -> !x.contains(":")).collect(Collectors.toList());
    }

    public static String httpGETRequest(String receiver) throws MalformedURLException, IOException {
        URL url = new URL(receiver);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(50000);
        con.setReadTimeout(50000);
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    public static String http_API_POST_MULTIPART_Request(String _url, String _key, String _apiName, HashMap<String, String> params, HashMap<String, byte[]> files) throws MalformedURLException, IOException, AuthenticationException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(_url);
        UsernamePasswordCredentials apikey = new UsernamePasswordCredentials(_apiName, _key);
        httpPost.addHeader(new BasicScheme().authenticate(apikey, httpPost, null));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        Iterator it_params = params.entrySet().iterator();
        while (it_params.hasNext()) {
            Map.Entry pair = (Map.Entry) it_params.next();
            String textBody = (String) pair.getValue();
            if (textBody == null) {
                textBody = "";
            }
            builder.addTextBody((String) pair.getKey(), textBody);
        }
        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            builder.addBinaryBody((String) pair.getKey(), (byte[]) pair.getValue(), ContentType.APPLICATION_OCTET_STREAM, (String) pair.getKey());
            it.remove();
        }
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        CloseableHttpResponse response = client.execute(httpPost);
        client.close();
        return response.getStatusLine().toString();
    }

    public static HashMap<String, byte[]> getFilesFromFullMessage(DocumentMessage<GetFullMessageResponse> fullMessage) throws UnsealConnectorException {
        HashMap<String, byte[]> files = new HashMap<>();
        int i = 1;
        for (Document annex : fullMessage.getAnnexList()) {
            if (files.get(annex.getFilename()) == null) {
                files.put(annex.getFilename(), annex.getContent());
                i = 1;
            } else {
                files.put(i + "-" + annex.getFilename(), annex.getContent());
                i++;
            }
        }
        files.put(fullMessage.getDocument().getFilename(), fullMessage.getDocument().getContent());
        return files;
    }

}
