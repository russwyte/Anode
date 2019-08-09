package earlyeffect

import earlyeffect.impl.EarlyEffect

import scala.scalajs.js
import scala.scalajs.js.Dictionary

trait EarlyComponent[Props, State] { self =>
  import dictionaryNames._

  def instanceConstructor: js.Dynamic

  val defaultKey = self.getClass.getName

  type OurInstance = EarlyInstance[Props, State]

  def didMount(instance: OurInstance): Unit    = ()
  def willMount(instance: OurInstance): Unit   = ()
  def willUnMount(instance: OurInstance): Unit = ()

  def didUpdate(oldProps: Props, oldState: State, instance: OurInstance): Unit = ()

  def baseDictionary(props: Props): Dictionary[js.Any] =
    js.Dictionary(
      Seq[(String, js.Any)](
        (Props, props.asInstanceOf[js.Any]),
        (ComponentConstructor, self.asInstanceOf[js.Any]),
        ("key", defaultKey) // this is a precaution - I may want to make this optional
      ): _*
    )

  def apply(props: Props): VNode =
    VNode(EarlyEffect.h(instanceConstructor, baseDictionary(props)))

}