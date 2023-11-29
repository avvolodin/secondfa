package models

import com.google.inject.{ImplementedBy, Singleton}

@Singleton
@ImplementedBy(classOf[FcmServiceImpl])
trait FcmService {
  //def sendNotify(token: String, )
}

class FcmServiceImpl extends FcmService {

}

