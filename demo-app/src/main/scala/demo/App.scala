package demo

import demo.model.TodoList.actions._
import demo.model._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import earlyeffect.core._

object App {

  val container: dom.Element = dom.document.querySelector("div[id='root']")

  def render(list: TodoList): Unit =
    Preact.render(todoList(list), container)

  def main(args: Array[String]): Unit = {
    ModelCircuit.subscribe(ModelCircuit.zoom(_.todoList))(x => render(x.value))
    render(ModelCircuit.zoom(_.todoList).value)
  }

  def todoList(l: TodoList): VirtualNode =
    E.div(
      E.section(
        A.`class`("todoapp"),
        E.div(
          E.header(
            A.`class`("header"),
            E.h1("todos"),
            newTodo
          ),
          if (l.todos.nonEmpty)
            Seq(
              list(l.filtered),
              footer(l)
            )
          else Empty
        )
      ),
      E.footer(
        A.`class`("info"),
        fragment(
          E.p("Double-click to edit a todo"),
          E.p("Rendered by Preactor - a Scala.js wrapper for Preact")
        )
      )
    )

  def list(todos: Seq[Todo]) = {

    def view(todo: Todo): VirtualNode =
      E.div(
        A.`class`("view"),
        E.input(
          A.`class`("toggle"),
          A.`type`("checkbox"),
          A.checked(todo.complete),
          A.onChange(_ => ModelCircuit(Update(todo.copy(complete = !todo.complete))))
        ),
        E.label(todo.description, A.onDoubleClick(x => {
          todos.foreach(x => ModelCircuit(Update(x.copy(editing = false))))
          ModelCircuit(Update(todo.copy(editing = true)))
        })),
        E.button(A.`class`("destroy"), A.onClick(_ => ModelCircuit(Delete(todo))))
      )

    def item(todo: Todo) =
      E.li(
        A.key(todo.key),
        A.`class`("editing").when(todo.editing),
        if (todo.editing) {
          TodoEditor(todo)
        } else {
          view(todo)
        }
      )

    E.section(
      A.`class`("main"),
      E.input(
        A.id("toggle-all"),
        A.`class`("toggle-all"),
        A.`type`("checkbox"),
        A.checked(todos.forall(_.complete)),
        A.onChange(e => {
          ModelCircuit(SetAll(e.target.asInstanceOf[HTMLInputElement].checked))
        })
      ),
      E.label(A.`for`("toggle-all")),
      E.ul(
        A.`class`("todo-list"),
        todos.map(item)
      )
    )
  }

  val newTodo = E.input(
    A.id("new-todo"),
    A.`class`("new-todo"),
    A.placeholder("What needs to be done?"),
    A.onKeyDown(x => {
      val e = x.target.asInstanceOf[HTMLInputElement]
      if (x.keyCode == ENTER && e.value.nonEmpty) {
        ModelCircuit(Add(Todo(e.value)))
        e.value = ""
      }
    })
  )

  def footer(todoList: TodoList) = {
    val left              = todoList.todos.count(!_.complete)
    val s                 = if (left > 1) " items left" else " item left"
    def change(f: Filter) = ModelCircuit(ApplyFilter(f))
    E.footer(
      A.`class`("footer"),
      E.span(A.`class`("todo-count"), E.strong(left), E.span(s)),
      E.ul(
        A.`class`("filters"),
        E.li("All", A.`class`("selected").when(todoList.filter == All), A.onClick(_ => change(All))),
        E.li("Active", A.`class`("selected").when(todoList.filter == Active), A.onClick(_ => change(Active))),
        E.li("Completed", A.`class`("selected").when(todoList.filter == Completed), A.onClick(_ => change(Completed)))
      ),
      E.button(A.`class`("clear-completed"), "Clear completed", A.onClick(_ => {
        ModelCircuit(ClearCompleted)
      }))
    )
  }

}