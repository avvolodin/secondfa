package models.dao.schema

import models.dao.entities.{Identity, Realm}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

object RealmSchema {
  class RealmTable(tag: Tag) extends Table[Realm](tag, "realm") {
    def id = column[String]("id")

    def key = column[String]("key")

    def description = column[String]("description")

    override def * : ProvenShape[Realm] = (id, key, description) <> (Realm.tupled, Realm.unapply)
  }

  val Realms = TableQuery[RealmTable]

  class IdentityTable(tag: Tag) extends Table[Identity](tag, "identity") {

    def id = column[String]("id")

    def realmId = column[String]("realmId")

    def token = column[String]("token")

    def login = column[String]("login")

    override def * : ProvenShape[Identity] = (id, realmId, token, login) <> (Identity.tupled, Identity.unapply)

  }
  val Identities = TableQuery[IdentityTable]
}