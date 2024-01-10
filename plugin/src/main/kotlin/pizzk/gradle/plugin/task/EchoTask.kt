package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.comm.GlobalContext
import pizzk.gradle.plugin.index.MavenRepoApi

class EchoTask : TaskAction() {
    override fun title(): String = "Echo"
    override fun execute(task: Task) {
        super.execute(task)
        val api = GlobalContext.value<MavenRepoApi>() ?: return
        task.doLast { println(api.config()) }
    }
}