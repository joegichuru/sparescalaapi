package com.joseph.spareapi.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.index.{GeoSpatialIndexed, Indexed}
import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document(collection = "")
case class Item() {
  @Id
  @Indexed
  @BeanProperty
  var id: String = _
  @BeanProperty
  @Indexed @NotNull
  var name: String = _
  @BeanProperty
  var price, postedOn: Long = _
  @BeanProperty
  var duration: String = MONTH
  @BeanProperty @NotNull
  var itemType, description: String = _
  @BeanProperty @NotNull
  var category:Long=0
  @BeanProperty
  var isLiked, isPublished: Boolean = _
  @BeanProperty
  var lat, lon: Double = _
  @BeanProperty
  var amenities: Set[String] = _
  @BeanProperty
  var email, phone, website: String = _
  @BeanProperty
  var imageUrls: java.util.ArrayList[String] = _
  @BeanProperty @NotNull
  var bathrooms, bedrooms: Int = _
  @BeanProperty
  var comments, likes: Long = _
  @BeanProperty @NotNull
  var area: Long = _
  @BeanProperty
  var user: User = _
  @BeanProperty @GeoSpatialIndexed @JsonIgnore
  var point:Point=_




  def addUser(user: User): Item = {
    this.user = user
    this
  }

}
