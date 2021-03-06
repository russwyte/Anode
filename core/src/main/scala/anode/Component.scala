package anode

import anode.impl.VNodeJS

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSName

trait Component[Props] extends AnodeComponent[Props, Nothing] { theComponent =>

  def render(props: Props): VNode

  //noinspection ScalaUnusedSymbol
  def didUpdate(oldProps: Props, instance: Instance, oldInstance: UndefOr[Instance]): Unit = ()

  def shouldUpdate(nextProps: Props, previous: Instance): Boolean = nextProps != previous.props

  override lazy val instanceConstructor: js.Dynamic = js.constructorOf[StatelessInstance]

  final private class StatelessInstance extends InstanceFacade[Props, Nothing] {

    override def componentDidMount(): Unit = didMount(this)

    override def componentWillMount(): Unit = willMount(this)

    override def componentWillUnmount(): Unit = willUnMount(this)

    @JSName("render")
    override def renderJS(props: js.Dynamic, state: js.Dynamic): VNodeJS =
      addSelectors(render(lookupProps(props)), this)

    override def shouldComponentUpdate(nextProps: js.Dynamic, nextState: js.Dynamic, nextContext: js.Dynamic): Boolean =
      shouldUpdate(lookupProps(nextProps), this)

    override def componentDidUpdate(oldProps: js.Dynamic, oldState: js.Dynamic, snapshot: js.Dynamic): Unit =
      didUpdate(
        oldProps = lookupProps(oldProps),
        oldState = lookupState(oldState),
        instance = this,
        oldInstance = snapshot.asInstanceOf[UndefOr[AnodeComponent[Props, Nothing]#Instance]],
      )

    override def componentDidCatch(e: js.Error): Unit = didCatch(e, this)
  }

}

object Component {

  implicit class FunctionalComponent[T](f: (T) => VNode) extends Component[T] {
    override def render(props: T): VNode = f(props)
  }
  implicit def applySelf[Comp <: Component[Comp], T <: Arg](self: Comp): T = self.apply(self).asInstanceOf[T]
}
