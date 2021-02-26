package anode

import org.scalajs.dom

trait InstanceDataSelector { self: AnodeComponent[_, _] =>
  val attributeName = s"data-anode-$classForClass"
  def extractAttributeValue(instance: self.Instance): String
  def selector(attributeValue: String) = s"[$attributeName='$attributeValue']"

  def addDataAttribute(e: dom.Element, instance: Instance): Unit =
    e.setAttribute(attributeName, self.extractAttributeValue(instance))
}
