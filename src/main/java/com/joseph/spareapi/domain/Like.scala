package com.joseph.spareapi.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document
class Like {
  @Id
  @Indexed
  @BeanProperty
  var id:String=_
  @BeanProperty  @Indexed
  var itemId:String=_
  @BeanProperty
  var user:User=_
  @BeanProperty
  var createdOn:Long=_
}
