package demo

import demo.model.TodoList.actions.Update
import demo.model.{ModelCircuit, Todo}
import earlyeffect._
import org.scalajs.dom.raw.HTMLInputElement

import scala.scalajs.js.timers

object TodoEditor extends StatefulComponent[Todo, String] {
  override def willMount(instance: I): Unit =
    instance.setState(instance.props().description)

  override def render(props: Todo, state: String, instance: I): VNode = {
    def update(): Unit = ModelCircuit(Update(props.copy(editing = false, description = state)))
    E.input(
        A.id(s"editor-${props.key}"),
        A.`class`("edit"),
        A.value(state),
        A.onKeyDown { k =>
          if (k.keyCode == ESCAPE) ModelCircuit(Update(props.copy(editing = false)))
          else if (k.keyCode == ENTER) update()
        },
        A.onKeyUp(k => instance.setState(k.target.asInstanceOf[HTMLInputElement].value)),
        A.onBlur { _ =>
          ModelCircuit
            .zoom(_.todoList.todos.find { x =>
              x.key == props.key && x.editing
            })
            .value
            .foreach(_ => update())
        }
      )
      .withRef(x => timers.setTimeout(1)(x.asInstanceOf[HTMLInputElement].focus()))
  }
}
