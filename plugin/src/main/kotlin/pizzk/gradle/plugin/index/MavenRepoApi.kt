package pizzk.gradle.plugin.index

import org.gradle.api.Project
import pizzk.gradle.plugin.PluginComponent
import pizzk.gradle.plugin.extension.Manifest
import pizzk.gradle.plugin.extension.Namespace
import pizzk.gradle.plugin.support.PathContext
import pizzk.gradle.plugin.support.Repository
import java.io.File
import java.net.URI

class MavenRepoApi private constructor(project: Project) {
    companion object : PluginComponent {
        private const val NAME = "MavenRepoApi"
        private var instance: MavenRepoApi? = null
        fun get(): MavenRepoApi? = instance
        override fun apply(project: Project) {
            val value = MavenRepoApi(project)
            instance = value
            project.afterEvaluate(value::setup)
        }
    }

    private val config: Config = MavenRepoConfig.create(project).value()
    private val caches: MutableList<Repository.Maven> = mutableListOf()
    private fun setup(project: Project) {
        val scope = config.scope()
        val wildcard = scope.firstOrNull()
        val targets = when {
            wildcard.isNullOrEmpty() -> setOf(project)
            wildcard == Namespace.SCOPE_ALL -> setOf(project.rootProject)
            else -> project.rootProject.allprojects.filter { it.name == project.name || scope.contains(it.name) }
        }
        targets.forEach { it.extensions.add(NAME, this) }
        println("extend `$NAME` for [${targets.joinToString { it.name }}]")
    }

    fun config(): Config = config
    fun resolve(force: Boolean = true): List<Repository.Maven> {
        if (!force && caches.isNotEmpty()) return caches
        //prepare local file path
        val rootDir = PathContext.rootDir()
        val manifestDir = File(rootDir, PathContext.MANIFEST_DIR)
        if (!manifestDir.exists()) manifestDir.mkdirs()
        val config: Config = config()
        //resolve manifests
        val total = 5
        val manifests = config.manifests().mapNotNull { el ->
            val (path, changing) = el
            return@mapNotNull when {
                //special dir: ~/.m2repo/manifests/manifest.xml
                path == Manifest.NAME -> File(manifestDir, path)
                //http remote file will download into ~/.m2repo/manifests/xxx/manifest.xml
                PathContext.http(path) -> PathContext.download(path, manifestDir, changing)
                //other local dir
                else -> File(path)
            }
        }
        println("1/$total. collect manifests:")
        if (manifests.isEmpty()) return emptyList()
        println(manifests.joinToString(separator = "\n", transform = File::getPath))
        println()
        //resolve and filter repos
        val namespaces = config.namespaces().keys.toSet()
        println("2/$total. collect namespace:")
        if (namespaces.isEmpty()) return emptyList()
        println(namespaces.joinToString())
        println()
        val finder: (File) -> List<Repository> = { Repository.find(it, namespaces) }
        val groups = manifests.map(finder).flatten().groupBy { it.name }
        //choose best while exist repeat repo with same name
        val repos = groups.values.mapNotNull { it.maxByOrNull { e -> e.priority } }
        println("3/$total. choose repos:")
        if (repos.isEmpty()) return emptyList()
        println(repos.joinToString(separator = "\n", transform = Repository::toString))
        println()
        //sync repo
        val contentsDir = File(rootDir, PathContext.CONTENTS_DIR)
        if (!contentsDir.exists()) contentsDir.mkdirs()
        println("4/$total. sync repos:")
        val values = repos.mapNotNull { PathContext.sync(it, contentsDir) }
        println()
        //inject maven
        println("5/$total. transform mavens:")
        if (values.isEmpty()) return emptyList()
        println(values.joinToString(separator = "\n", transform = Repository.Maven::toString))
        println()
        caches.clear()
        caches.addAll(values)
        return values
    }

    fun uri(name: String?): URI? = resolve(force = false).firstOrNull { it.name == name }?.url
    fun dir(name: String?): String? {
        name ?: return null
        val contentsDir = File(PathContext.rootDir(), PathContext.CONTENTS_DIR)
        return PathContext.namespaceDir(contentsDir, name).absolutePath
    }

    interface Config {
        fun scope(): Set<String>;
        fun manifests(): Map<String, Boolean>
        fun namespaces(): Map<String, Set<String>>
        fun changing(): Boolean
    }
}