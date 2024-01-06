package pizzk.gradle.plugin.task

import org.gradle.api.Action
import org.gradle.api.Task

abstract class TaskAction : Action<Task> {
    companion object {
        const val GROUP = "mavenrepo"
    }

    abstract fun title(): String
    fun name(): String = "$GROUP${title()}"
    override fun execute(task: Task) {
        task.group = GROUP
    }
}