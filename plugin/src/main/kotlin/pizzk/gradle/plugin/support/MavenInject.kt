package pizzk.gradle.plugin.support

import org.gradle.api.Project
import pizzk.gradle.plugin.MavenRepoApi
import pizzk.gradle.plugin.extension.Namespace

class MavenInject {
    fun apply(project: Project) {
        val api = MavenRepoApi.get() ?: return
        val config = api.config()
        val namespaces = config.namespaces()
        val projects = project.rootProject.allprojects
        val mavens = api.resolve(config.changing())
        mavens.forEach { e ->
            val scope = namespaces[e.name]
            val wildcard = scope?.firstOrNull()
            if (wildcard.isNullOrEmpty()) return@forEach
            val targets = when (wildcard) {
                Namespace.SCOPE_CUR -> setOf(project)
                Namespace.SCOPE_ALL -> projects
                else -> projects.filter { scope.contains(it.name) }
            }
            if (targets.isEmpty()) return@forEach
            targets.forEach { it.repositories.maven(e) }
            println("inject `${e.name}` for [${targets.joinToString { it.name }}]")
        }
    }
}