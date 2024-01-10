package pizzk.gradle.plugin.comm

import org.gradle.api.Project
import pizzk.gradle.plugin.extension.Namespace
import pizzk.gradle.plugin.index.MavenRepoApi
import kotlin.reflect.KClass

object GlobalContext {
    private val values: MutableMap<KClass<*>, Any> = mutableMapOf()
    fun values(): Map<KClass<*>, Any> = values
    inline fun <reified T> value(): T? = values()[T::class] as? T
    fun inject(value: Any) = when (value) {
        is MavenRepoApi -> values[value::class] = value
        else -> Unit
    }

    inline fun <reified T> createExt(project: Project, name: String): T {
        return project.extensions.create(name, T::class.java)
    }

    fun joinExt(project: Project, name: String, value: Any) {
        val api = value<MavenRepoApi>() ?: return
        val scope = api.config().scope()
        val wildcard = scope.firstOrNull()
        val targets = when {
            wildcard.isNullOrEmpty() -> setOf(project)
            wildcard == Namespace.SCOPE_ALL -> setOf(project.rootProject)
            else -> project.rootProject.allprojects.filter { it.name == project.name || scope.contains(it.name) }
        }
        targets.forEach { it.extensions.add(name, value) }
        println("joinExt `${name}` for [${targets.joinToString { it.name }}]")
    }
}