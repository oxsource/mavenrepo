package pizzk.gradle.plugin.index

import org.gradle.api.Project
import pizzk.gradle.plugin.PluginComponent
import pizzk.gradle.plugin.task.EchoTask
import pizzk.gradle.plugin.task.HelpTask
import pizzk.gradle.plugin.task.ResolveTask
import pizzk.gradle.plugin.task.TaskAction

class MavenRepoTask private constructor() {
    companion object : PluginComponent {
        private val task by lazy { MavenRepoTask() }
        override fun apply(project: Project) = task.setup(project)
    }

    private val values: MutableList<TaskAction> by lazy { mutableListOf() }

    private fun setup(project: Project) {
        if (values.isNotEmpty()) return
        values.add(HelpTask())
        values.add(EchoTask())
        values.add(ResolveTask())
        //register
        values.forEach { project.tasks.register(it.name(), it::execute) }
    }
}