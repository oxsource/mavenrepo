package pizzk.gradle.plugin

import pizzk.gradle.plugin.extension.Manifest
import pizzk.gradle.plugin.extension.Namespace

abstract class MavenRepoExtension {
    private var changing: Boolean = true
    private val manifests = Manifest()
    private val namespace = Namespace()

    private val config = object : MavenRepoApi.Config {
        override fun manifests(): Map<String, Boolean> = manifests.value()

        override fun namespaces(): Map<String, Namespace.Policy> = namespace.value()

        override fun changing(): Boolean = changing

        override fun toString(): String = "manifests: [$manifests]\nnamespace: [$namespace]\nchanging: [$changing]"
    }

    fun manifests(block: Manifest.() -> Unit) = manifests.apply(block)
    fun namespace(block: Namespace.() -> Unit) = namespace.apply(block)
    fun changing(value: Boolean) {
        changing = value
    }

    fun manifestLocal() = manifests.local()
    fun manifestGitee(changing: Boolean) = manifests.gitee(changing)
    fun manifest(url: String, changing: Boolean) = manifests.url(url, changing)
    fun include(names: List<String>, global: Boolean) {
        val policy = if (global) Namespace.Policy.ALL else Namespace.Policy.PROJECT
        namespace.include(names, policy)
    }

    fun include(names: List<String>) = include(names, global = false)

    fun config(): MavenRepoApi.Config = config
    override fun toString(): String = config.toString()
}