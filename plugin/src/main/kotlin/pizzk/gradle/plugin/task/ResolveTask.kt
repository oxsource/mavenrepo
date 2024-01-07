package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.MavenRepoApi

class ResolveTask : TaskAction() {
    override fun title(): String = "Resolve"
    override fun execute(task: Task) {
        super.execute(task)
        val api = MavenRepoApi.of(task.project) ?: return
        task.doLast { api.resolve(force = true) }
        if (!api.config().changing()) return
        api.resolve(force = true)
    }
}