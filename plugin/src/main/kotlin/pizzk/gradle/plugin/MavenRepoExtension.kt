package pizzk.gradle.plugin

import pizzk.gradle.plugin.extension.Locate
import pizzk.gradle.plugin.extension.Manifest

abstract class MavenRepoExtension {
    private var changing: Boolean = true
    private val manifests = Manifest()
    private val locate = Locate()
    fun manifests(block: Manifest.() -> Unit) = manifests.apply(block)
    fun manifests(): Map<String, Boolean> = manifests.value()
    fun locate(block: Locate.() -> Unit) = locate.apply(block)
    fun locate(): Set<String> = locate.value()
    fun changing(value: Boolean) {
        changing = value
    }

    fun changing(): Boolean = changing

    override fun toString(): String = "manifests: $manifests\nlocate: $locate\nchanging: $changing"
}