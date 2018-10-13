//package com.joseph.spareapi.converters
//
//import com.joseph.spareapi.domain.User
//import com.mongodb.{BasicDBObject, DBObject}
//import org.springframework.core.convert.converter.Converter
//import org.springframework.stereotype.Component
//
//@Component
//class UserWriterConverter extends Converter[User,DBObject]{
//  override def convert(source: User): DBObject = {
//    val dbobject:DBObject=new BasicDBObject()
//    dbobject.put("name",source.getName)
//    dbobject.put("role",source.getRole)
//    dbobject.put("password",source.getPassword)
//    dbobject.put("createdOn",source.getCreatedOn)
//    dbobject.put("email",source.getEmail)
//    dbobject.removeField("_class")
//    dbobject
//  }
//}
