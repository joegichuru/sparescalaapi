package com.joseph.spareapi.security

object Constants {
  val EXPIRATION_TIME:Long=864000000//10 days
  val SECRET:String="S3Cr47K045dgt"// signing key
  val HEADER_STRING:String="Authorization"
  val PREFIX="Bearer"
  val SIGN_UP_URL="/auth/signup"
  val FILE_URLS="/items/p/**"
  val ACTIVATE="/auth/activate/**"
}
