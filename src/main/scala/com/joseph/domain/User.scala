package com.joseph.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document
class User {
  @Id
  @Indexed
  @BeanProperty
  var id: String = _
  @BeanProperty
  @Indexed
  var name, email: String = _
  @BeanProperty
  var imageUrl: String = _
  @BeanProperty
  @JsonIgnore
  var role: String = Roles.REGULAR
  @BeanProperty
  @JsonIgnore
  var password: String = _
  @BeanProperty
  var createdOn: Long = _
  @BeanProperty
  var accessToken: String = _
  @BeanProperty
  @JsonIgnore
  var active: Boolean = true
  @BeanProperty
  @JsonIgnore
  var suspended: Boolean = false
  @BeanProperty
  @JsonIgnore
  var fcmToken: String = _

  //override equals
  override def equals(that: scala.Any): Boolean = {
    that match {
      case that: User => that.id.equals(this.id) && that.email.equals(that.email)
      case _ => false
    }
  }
}
