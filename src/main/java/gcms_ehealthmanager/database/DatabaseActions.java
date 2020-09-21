/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import gcms_ehealthmanager.Core;
import gcms_ehealthmanager.database.filters.FilterObject;
import gcms_ehealthmanager.database.filters.FilterRule;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author Matthias
 */
public class DatabaseActions {

    static MongoClient mongo;
    static Map<String, MongoDatabase> databases = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(DatabaseActions.class.getName());
    static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    public static void connect() {
        String host = Core.getProp("database.host");
        mongo = new MongoClient(host,
                MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());

    }

    public static void createDatabaseMap() {

        if (databases.get("ehealthbox") == null) {
            //mongo.dropDatabase("users");
            databases.put("ehealthbox", openOrCreateDB("ehealthbox"));
        }
        if (databases.get("ehealthbox_files") == null) {
            //mongo.dropDatabase("users");
            databases.put("ehealthbox_files", openOrCreateDB("ehealthbox_files"));
        }
    }

    public static MongoDatabase getDatabase(String db) {
        return databases.get(db);
    }

    static private MongoDatabase openOrCreateDB(String db) {
        try {
            return mongo.getDatabase(db);

        } catch (Exception e) {
            LOG.info(e.getLocalizedMessage());

            return null;
        }
    }

    public static MongoCollection<Document> getObjects(String collection, String objectClassName) throws ClassNotFoundException {
        MongoCollection<Document> results = null;
        Class cls = Class.forName(objectClassName);
        results = databases.get("ehealthbox").getCollection(collection, cls);
        return results;
    }

    public static Document getObject(String collection, String objectClassName, String idName, String id) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MongoCollection<Document> ObjectItems = getObjects(collection, objectClassName);
        ArrayList<Document> results = null;
        results = ObjectItems.find(and(eq(idName, id))).into(new ArrayList<Document>());
        if (results.size() > 0) {
            return Document.parse(mapper.writeValueAsString(results.get(0)));
        } else {
            return null;
        }
    }

    public static void insertObject(String collection, String objectClassName, Document _doc) throws ClassNotFoundException {
        getObjects(collection, objectClassName).insertOne(_doc);
        LOG.info("One Object inserted");
    }

    public static void updateObject(String collection, String objectClassName, BasicDBObject _bson, String idName) throws ClassNotFoundException {
        Bson newDocument = new Document("$set", _bson);
        getObjects(collection, objectClassName).findOneAndUpdate(and(eq(idName, _bson.get(idName))), newDocument, (new FindOneAndUpdateOptions()).upsert(true));
        LOG.info("One Object updated");
    }

    public static void insertFile(InputStream inputStream, String name, FileObject _fileobject) {
        ObjectId fileId = null;
        try {
            GridFSBucket gridBucket = GridFSBuckets.create(getDatabase("ehealthbox_files"));
            ObjectMapper mapper = new ObjectMapper();
            Document document = Document.parse(mapper.writeValueAsString(_fileobject));
            GridFSUploadOptions uploadOptions = new GridFSUploadOptions().chunkSizeBytes(1024).metadata(document);
            fileId = gridBucket.uploadFromStream(name, inputStream, uploadOptions);
        } catch (JsonProcessingException e) {
        }
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }

    public static String getFile(String _fileName, String baseURL, String tempURL) {
        String extension = Core.getExtension(_fileName);
        String base64Filename = Base64.getEncoder().encodeToString(_fileName.getBytes()) + extension;
        String outputPath = tempURL + "/" + _fileName;
        String viewPath = baseURL + "/temp/" + _fileName;
        try {
            MongoDatabase database = databases.get("ehealthbox_files");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            gridBucket.downloadToStream(_fileName, fileOutputStream);

            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewPath;
    }

    public static String getFileAsString(String _fileName) {
        String output = "";
        try {
            MongoDatabase database = databases.get("ehealthbox_files");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            //FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            gridBucket.downloadToStream(_fileName, byteArrayOutputStream);
            output = new String(byteArrayOutputStream.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static String downloadFileToDir(String _fileName, String dir) {
        if (!Core.checkFile(_fileName)) {
            String extension = Core.getExtension(_fileName);
            try {
                MongoDatabase database = databases.get("ehealthbox_files");
                GridFSBucket gridBucket = GridFSBuckets.create(database);
                FileOutputStream fileOutputStream = new FileOutputStream(dir + "/" + _fileName);
                gridBucket.downloadToStream(_fileName, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dir + "/" + _fileName;
    }

    public static ArrayList<Document> getObjectsSpecificList(String collection, String objectClassName, Bson bson, Bson sort, int limit, String[] excludes, int page, int rows) throws ClassNotFoundException {
        Class cls = Class.forName(objectClassName);
        List<Field> fields = Arrays.asList(cls.getDeclaredFields());
        List<String> columns = fields.stream().map(e -> e.getName()).collect(Collectors.toList());

        if (excludes != null) {
            for (String exclude : excludes) {
                columns.remove(exclude);
            }
        }
        ArrayList<Document> results = null;
        try {
            MongoCollection<Document> ObjectItems = getObjects(collection, objectClassName);
            results = ObjectItems.find(bson).sort(sort).skip(rows * (page - 1)).limit(limit).projection(
                    fields(include(columns))
            ).into(new ArrayList<Document>());
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            return results;
        }

        return results;
    }

    public static Long getObjectCount(String collection, String objectClassName, Bson bson) throws ClassNotFoundException {
        MongoCollection<Document> results = null;
        Class cls = Class.forName(objectClassName);
        results = databases.get("ehealthbox").getCollection(collection, cls);
        return results.count(bson);
    }

    public static BasicDBObject createFilterObject(String[] json) {

        BasicDBObject filter = new BasicDBObject();
        if (json != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                FilterObject filterObject = mapper.readValue(json[0], FilterObject.class);
                for (FilterRule rule : filterObject.getRules()) {
                    if (rule.getOp().equals("cn")) {
                        filter.put(rule.getField(), new BasicDBObject("$regex", rule.getData()));
                    }
                }
            } catch (JsonProcessingException ex) {
                Logger.getLogger(DatabaseActions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return filter;
    }

}
