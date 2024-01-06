package pizzk.gradle.plugin

import org.gradle.api.Project
import pizzk.gradle.plugin.task.*

class MavenRepoTask(private val project: Project) {

    private val values: MutableList<TaskAction> by lazy { mutableListOf() }
    fun setup() {
        if (values.isNotEmpty()) return
        val config = project.extensions.create(MavenRepoPlugin.NAME, MavenRepoExtension::class.java)
        values.add(HelpTask())
        values.add(EchoTask(config))
        values.add(ResolveTask(config))
        //register
        values.forEach { project.tasks.register(it.name(), it::execute) }
    }
}