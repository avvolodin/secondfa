package models

import com.google.inject.{ImplementedBy, Singleton}
import net.glxn.qrgen.javase.QRCode

@ImplementedBy(classOf[QrServiceImpl])
trait QrService {
  def getQrPng(text: String): Array[Byte]
}

@Singleton
class QrServiceImpl extends QrService {
  override def getQrPng(text: String): Array[Byte] =
    QRCode.from(text).withSize(300,300).stream().toByteArray
}