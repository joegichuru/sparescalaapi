package com.joseph.dao.services

import java.util.{Calendar, Date}

import com.joseph.dao.repositories.ViewsRepository
import com.joseph.domain.{Item, User, View}
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
@Service
class ViewsService @Autowired()(viewsRepository: ViewsRepository) {

  def registerView(user:User,item:Item):Unit={
    val view=new View()
    view.setCreated(new Date().getTime)
    view.setItemId(item.getId)
    view.setUserId(user.getId)
    viewsRepository.save(view)
  }

  def findCount():Long=viewsRepository.count()

  def today():String={
    val date=new Date()
    val views=viewsRepository.findAllByCreatedGreaterThanOrderByCreatedDesc(date.getTime-(3600*60*6))
    //tranform into hourly items
    var jsonContainer=new JSONObject()
    //get 1 hr
    var times=views.asScala.map(v=>{
      var d=new Date(v.getCreated)
      var calender=Calendar.getInstance()
      calender.setTime(d)
      calender.get(Calendar.HOUR_OF_DAY)
    }).toList
    val s=times.toSet
    var d=s.map(e=>jsonContainer.put(e.toString, times.count(_==e)))
    //jsonContainer.put("weekly",d.mkString)
   // d.mkString
    jsonContainer.toString()
  }
}
