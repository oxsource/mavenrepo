package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.MavenRepoApi

class EchoTask : TaskAction() {
    override fun title(): String = "Echo"
    override fun execute(task: Task) {
        super.execute(task)
        val api = MavenRepoApi.of(task.project)
        task.doLast { println(api?.config()?.toString().orEmpty()) }
    }
}