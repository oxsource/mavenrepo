package pizzk.gradle.plugin

import org.gradle.api.Project
import pizzk.gradle.plugin.extension.Manifest
import pizzk.gradle.plugin.extension.Namespace

abstract class MavenRepoExtension {
    private var scope: MutableSet<String> = mutableSetOf()
    private var changing: Boolean = true
    private val manifests = Manifest()
    private val namespace = Namespace()

    private val config = object : MavenRepoApi.Config {
        override fun scope(): Set<String> = scope

        override fun manifests(): Map<String, Boolean> = manifests.value()

        override fun namespaces(): Map<String, Set<String>> = namespace.value()

        override fun changing(): Boolean = changing

        override fun toString(): String = "manifests: [$manifests]\nnamespace: [$namespace]\nchanging: [$changing]"
    }

    fun manifests(block: Manifest.() -> Unit) = manifests.apply(block)
    fun namespace(block: Namespace.() -> Unit) = namespace.apply(block)
    fun changing(value: Boolean) {
        changing = value
    }

    fun scope(value: List<String>) {
        scope.clear()
        scope.addAll(value)
    }

    fun manifestLocal() = manifests.local()
    fun manifestGitee(changing: Boolean) = manifests.gitee(changing)
    fun manifest(url: String, changing: Boolean) = manifests.url(url, changing)
    fun include(names: List<String>, scope: List<String>) = namespace.include(names, scope.toSet())
    fun config(): MavenRepoApi.Config = config
    override fun toString(): String = config.toString()

    companion object {
        fun create(project: Project): MavenRepoExtension {
            return project.extensions.create(MavenRepoPlugin.NAME, MavenRepoExtension::class.java)
        }
    }
}