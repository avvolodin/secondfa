package models

case class RegistrationRequest(id: String, realm: String, login: String, key: String, exp: Long)


