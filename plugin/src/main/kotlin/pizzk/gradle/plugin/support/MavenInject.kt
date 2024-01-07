package pizzk.gradle.plugin.support

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import pizzk.gradle.plugin.MavenRepoApi
import pizzk.gradle.plugin.extension.Namespace

class MavenInject {
    fun apply(project: Project) {
        val api = MavenRepoApi.of(project) ?: return
        val config = api.config()
        val namespaces = config.namespaces()
        val values = api.resolve(config.changing())
        values.forEach { el ->
            val policy = namespaces[el.name] ?: return@forEach
            val repos: List<RepositoryHandler> = when (policy) {
                Namespace.Policy.PROJECT -> listOf(project.repositories)
                Namespace.Policy.ALL -> project.rootProject.allprojects.map { it.repositories }
                else -> emptyList()
            }
            println("`${el.name}` inject for `$policy`")
            repos.forEach { it.maven(el) }
        }
    }
}