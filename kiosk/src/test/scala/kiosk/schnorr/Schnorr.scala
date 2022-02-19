package kiosk.schnorr

import kiosk.encoding.ScalaErgoConverters.{getAddressFromErgoTree, getStringFromAddress}
import kiosk.script.ScriptUtil

object Schnorr extends App {
  lazy val script =
    s"""{
       |  // Checking Schnorr signature in a script
       |  val g: GroupElement = groupGenerator
       |
       |  // Public key for a signature
       |  val Y = SELF.R4[GroupElement].get
       |
       |  // Message to sign
       |  val m = getVar[Coll[Byte]](0).get
       |
       |  // c of signature in (c, s)
       |  val cBytes = getVar[Coll[Byte]](1).get
       |  val c = byteArrayToBigInt(cBytes)
       |  
       |  // s of signature in (c, s)
       |  val s = getVar[BigInt](2).get
       |  
       |  // Computing challenge
       |  
       |  val U = g.exp(s).multiply(Y.exp(c)).getEncoded // as a byte array
       |  
       |  sigmaProp(cBytes == sha256(U ++ m))
       |}""".stripMargin

  lazy val ergoTree = ScriptUtil.compile(Map(), script)
  lazy val address = getStringFromAddress(getAddressFromErgoTree(ergoTree))
  println(address)

}
