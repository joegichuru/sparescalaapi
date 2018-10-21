//package com.joseph.spareapi.controllers;
//
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.gridfs.GridFSBucket;
//import com.mongodb.client.gridfs.GridFSBuckets;
//import com.mongodb.client.gridfs.model.GridFSFile;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.gridfs.GridFsResource;
//import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//import org.springframework.http.CacheControl;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.security.Principal;
//
//@Controller
//@RequestMapping("/items/p")
//public class ResourceController {
////    @Autowired
////    private ItemService itemService;
////    @Autowired
////    private UserService userService;
//    @Value("${spring.data.mongodb.database}")
//    private String dbName;
//    @Autowired
//    private MongoClient mongoClient;
//    @Autowired
//    private GridFsTemplate gridFsTemplate;
//
//    @GetMapping(value = "/{id}")
//    @ResponseBody()
//    public ResponseEntity<byte[]> findImages(@PathVariable("id") String id, Principal principal) throws Exception {
//        GridFsResource image = getResource(id);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setCacheControl(CacheControl.noCache().getHeaderValue());
//        InputStream inputStream = image.getInputStream();
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        byte[] data = new byte[0xFFFF];
//        for (int len = inputStream.read(data); len != -1; len = inputStream.read(data)) {
//            buffer.write(data, 0, len);
//        }
//        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(buffer.toByteArray(), httpHeaders, HttpStatus.OK);
//        return responseEntity;
//    }
//
//    private GridFsResource getResource(String id) {
//        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
//        return new GridFsResource(file, getGridfs().openDownloadStream(file.getObjectId()));
//    }
//
//    private GridFSBucket getGridfs() {
//        MongoDatabase db = mongoClient.getDatabase(dbName);
//        return GridFSBuckets.create(db);
//    }
//}
