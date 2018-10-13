//package com.joseph.spareapi.event
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.mongodb.core.MongoOperations
//import org.springframework.data.mongodb.core.mapping.event.{AbstractMongoEventListener, BeforeConvertEvent}
//import org.springframework.util.ReflectionUtils
//
//class CascadeSaveMongoEventListener @Autowired()(mongoOperations: MongoOperations) extends AbstractMongoEventListener[Object]{
//  override def onBeforeConvert(event: BeforeConvertEvent[Object]): Unit = {
//    val source = event.getSource
//    ReflectionUtils.doWithFields(source.getClass, new Nothing(source, mongoOperations))
//  }
//}
