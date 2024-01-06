package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.MavenRepoExtension

class EchoTask(private val config: MavenRepoExtension) : TaskAction() {
    override fun title(): String = "Echo"
    override fun execute(task: Task) {
        super.execute(task)
        task.doLast { println(config) }
    }
}