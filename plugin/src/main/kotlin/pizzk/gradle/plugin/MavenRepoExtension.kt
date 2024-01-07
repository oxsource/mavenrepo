package pizzk.gradle.plugin

import pizzk.gradle.plugin.extension.Namespace
import pizzk.gradle.plugin.extension.Manifest

abstract class MavenRepoExtension {
    private var changing: Boolean = true
    private val manifests = Manifest()
    private val namespace = Namespace()
    private val config = object : MavenRepoApi.Config {
        override fun manifests(): Map<String, Boolean> = manifests.value()

        override fun namespaces(): Set<String> = namespace.value()

        override fun changing(): Boolean = changing

        override fun toString(): String = "manifests: $manifests\nlocate: $namespace\nchanging: $changing"
    }

    fun manifests(block: Manifest.() -> Unit) = manifests.apply(block)
    fun namespace(block: Namespace.() -> Unit) = namespace.apply(block)
    fun changing(value: Boolean) {
        changing = value
    }

    fun config(): MavenRepoApi.Config = config
    override fun toString(): String = config.toString()
}