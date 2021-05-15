package kiosk.script

import kiosk.ergo._
import org.ergoplatform.ErgoAddressEncoder
import org.ergoplatform.ErgoAddressEncoder.MainnetNetworkPrefix
import sigmastate.Values.ErgoTree
import sigmastate.eval.CompiletimeIRContext
import sigmastate.lang.SigmaCompiler
import special.sigma.GroupElement

import scala.collection.mutable.{Map => MMap}

object ScriptUtil {

  val networkPrefix = MainnetNetworkPrefix
  private val compiler = SigmaCompiler(networkPrefix)

  implicit val ergoAddressEncoder: ErgoAddressEncoder = new ErgoAddressEncoder(networkPrefix)

  def addIfNotExist(envMap: MMap[String, KioskType[_]], name: String, kioskType: KioskType[_]) = {
    envMap
      .get(name)
      .fold(
        envMap += name -> kioskType
      )(_ => throw new Exception(s"Variable $name is already defined"))
  }

  def compile(env: Map[String, KioskType[_]], ergoScript: String): ErgoTree = {
    import sigmastate.lang.Terms._
    implicit val irContext = new CompiletimeIRContext
    compiler.compile(env.mapValues(_.value), ergoScript).asSigmaProp
  }

  implicit def mapToBetterMMap(map: MMap[String, KioskType[_]]) = BetterMMap(map)
}

case class BetterMMap(map: MMap[String, KioskType[_]]) {
  def setCollByte(name: String, bytes: Array[Byte]) = ScriptUtil.addIfNotExist(map, name, KioskCollByte(bytes))

  def setLong(name: String, long: Long) = ScriptUtil.addIfNotExist(map, name, KioskLong(long))

  def setInt(name: String, int: Int) = ScriptUtil.addIfNotExist(map, name, KioskInt(int))

  def setGroupElement(name: String, groupElement: GroupElement) = ScriptUtil.addIfNotExist(map, name, KioskGroupElement(groupElement))
}
