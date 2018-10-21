package com.joseph.config

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.gridfs.GridFsTemplate

@Configuration
class MongoConfiguration extends AbstractMongoConfiguration {
  private val databaseName: String = "sparedb"
  @Value("${spring.data.mongodb.uri}")
  private var mongoAddress: String = _

  @Bean
  override def mongoClient(): MongoClient = {
    new MongoClient(mongoAddress)
  }

  override def getDatabaseName: String = {
    databaseName
  }

  @Bean
  @throws[Exception]
  def gridFsTemplate = new GridFsTemplate(mongoDbFactory, mappingMongoConverter)
}
