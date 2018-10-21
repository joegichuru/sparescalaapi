package com.joseph.controllers

import java.io.ByteArrayOutputStream
import java.security.Principal

import com.mongodb.MongoClient
import com.mongodb.client.gridfs.GridFSBuckets
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.data.mongodb.gridfs.{GridFsResource, GridFsTemplate}
import org.springframework.http.{CacheControl, HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, ResponseBody}

@Controller
@RequestMapping(Array("/items/p"))
class ResourceController @Autowired()(mongoClient: MongoClient, gridFsTemplate: GridFsTemplate)() {
  @Value("${spring.data.mongodb.database}")
  private var dbName:String = _

  @GetMapping(value = Array("/{id}"))
  @ResponseBody
  @throws[Exception]
  def findImages(@PathVariable("id") id: String, principal: Principal): ResponseEntity[Array[Byte]] = {
    val image = getResource(id)
    val httpHeaders = new HttpHeaders
    httpHeaders.setCacheControl(CacheControl.noCache.getHeaderValue)
    val inputStream = image.getInputStream
    val buffer = new ByteArrayOutputStream
    val data = new Array[Byte](0xFFFF)
    var len = inputStream.read(data)
    while ( {
      len != -1
    }) {
      buffer.write(data, 0, len)

      len = inputStream.read(data)
    }
    val responseEntity = new ResponseEntity[Array[Byte]](buffer.toByteArray, httpHeaders, HttpStatus.OK)
    responseEntity
  }

  private def getResource(id: String) = {
    val file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)))
    new GridFsResource(file, getGridfs.openDownloadStream(file.getObjectId))
  }

  private def getGridfs = {
    val db = mongoClient.getDatabase(dbName)
    GridFSBuckets.create(db)
  }

}
