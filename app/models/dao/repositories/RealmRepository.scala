package models.dao.repositories

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.entities.{Identity, Realm}
import models.dao.schema.RealmSchema.{Identities, Realms}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@ImplementedBy(classOf[RealmRepositoryImpl])
trait RealmRepository {
  def get(id: String): Future[Option[Realm]]
  def findWithIdentity(realm: String, login: String): Future[Option[(Realm, Identity)]]

  def setIdentity(realm: String, login: String, token: String): Future[Unit]
}

@Singleton
class RealmRepositoryImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                    (implicit ec: ExecutionContext)
        extends RealmRepository with HasDatabaseConfigProvider[PostgresProfile] {
  import dbConfig.profile.api._
  override def get(id: String): Future[Option[Realm]] = {
    db.run(Realms.filter(_.id === id).take(1).result.headOption)
  }

  override def findWithIdentity(realm: String, login: String): Future[Option[(Realm, Identity)]] = {
    val query = for {
      (r,i) <- Realms join Identities on (_.id === _.realmId) if i.login === login
    } yield (r,i)
    db.run(query.take(1).result.headOption)
  }

  override def setIdentity(realm: String, login: String, token: String): Future[Unit] = {
    val delete = Identities.filter(i=>i.realmId === realm && i.login === login).delete
    val insert = Identities += Identity(UUID.randomUUID().toString, realm, token, login)
    val tr = DBIO.seq(delete, insert).transactionally
    db.run(tr)
  }
}