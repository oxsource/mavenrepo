package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.comm.GlobalContext
import pizzk.gradle.plugin.index.MavenRepoApi

class ResolveTask : TaskAction() {
    override fun title(): String = "Resolve"
    override fun execute(task: Task) {
        super.execute(task)
        val api = GlobalContext.value<MavenRepoApi>() ?: return
        task.doLast { api.resolve(force = true) }
    }
}