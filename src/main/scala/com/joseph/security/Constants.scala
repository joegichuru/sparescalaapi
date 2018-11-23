package com.joseph.security

object Constants {
  val EXPIRATION_TIME:Long=864000000//10 days
  val SECRET:String="S3Cr47K045dgt"// signing key
  val HEADER_STRING:String="Authorization"
  val PREFIX="Bearer"
  val SIGN_UP_URL="/auth/signup"
  val FILE_URLS="/resource/p/**"
  val ACTIVATE="/auth/activate/**"
  val PASSWORD_RESET="/auth/password/reset"
  val SWAGGER="/swagger-resources**"
  val SWAGGER2="/swagger-resources/**"
  val API_DOCS="/v2/api-docs"
  val SWAGGER_UI="/swagger-ui.html"
}
