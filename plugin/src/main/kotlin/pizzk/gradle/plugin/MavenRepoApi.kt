package pizzk.gradle.plugin

import org.gradle.api.Project
import pizzk.gradle.plugin.support.MavenRepoPath
import pizzk.gradle.plugin.support.Repository
import pizzk.gradle.plugin.extension.Manifest
import pizzk.gradle.plugin.extension.Namespace
import java.io.File
import java.net.URI

class MavenRepoApi(project: Project) {
    companion object {
        const val NAME = "MavenRepoApi"
        fun of(project: Project): MavenRepoApi? = project.extensions.getByName(NAME) as? MavenRepoApi
    }

    private val config: Config = project.extensions.create(MavenRepoPlugin.NAME, MavenRepoExtension::class.java).config()
    private val caches: MutableList<Repository.Maven> = mutableListOf()
    fun config(): Config = config
    fun resolve(force: Boolean = true): List<Repository.Maven> {
        if (!force && caches.isNotEmpty()) return caches
        //prepare local file path
        val rootDir = MavenRepoPath.rootDir()
        val manifestDir = File(rootDir, MavenRepoPath.MANIFEST_DIR)
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
                MavenRepoPath.http(path) -> MavenRepoPath.download(path, manifestDir, changing)
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
        val finder: (File) -> List<Repository> = { Repository.find(it, namespaces::contains) }
        val groups = manifests.map(finder).flatten().groupBy { it.name }
        //choose best while exist repeat repo with same name
        val repos = groups.values.mapNotNull { it.maxByOrNull { e -> e.priority } }
        println("3/$total. choose repos:")
        if (repos.isEmpty()) return emptyList()
        println(repos.joinToString(separator = "\n", transform = Repository::toString))
        println()
        //sync repo
        val contentsDir = File(rootDir, MavenRepoPath.CONTENTS_DIR)
        if (!contentsDir.exists()) contentsDir.mkdirs()
        println("4/$total. sync repos:")
        val values = repos.mapNotNull { MavenRepoPath.sync(it, contentsDir) }
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
    interface Config {
        fun manifests(): Map<String, Boolean>
        fun namespaces(): Map<String, Namespace.Policy>
        fun changing(): Boolean
    }
}