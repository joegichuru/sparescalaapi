package com.joseph.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document
class View {
  @BeanProperty
  @Id
  var id:String=_
  @BeanProperty
  var itemId:String=_
  @BeanProperty
  var userId:String=_
  @BeanProperty
  var created:Long=_
}
