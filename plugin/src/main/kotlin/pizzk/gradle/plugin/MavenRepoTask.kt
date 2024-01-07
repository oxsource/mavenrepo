package pizzk.gradle.plugin

import org.gradle.api.Project
import pizzk.gradle.plugin.task.*

class MavenRepoTask(private val project: Project) {

    private val values: MutableList<TaskAction> by lazy { mutableListOf() }
    fun setup() {
        if (values.isNotEmpty()) return
        values.add(HelpTask())
        values.add(EchoTask())
        values.add(ResolveTask())
        //register
        values.forEach { project.tasks.register(it.name(), it::execute) }
    }
}