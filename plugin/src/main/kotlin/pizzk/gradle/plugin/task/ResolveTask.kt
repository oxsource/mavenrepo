package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.MavenRepoExtension
import pizzk.gradle.plugin.comm.MavenRepoPath
import pizzk.gradle.plugin.comm.Repository
import pizzk.gradle.plugin.extension.Manifest
import java.io.File

class ResolveTask(private val config: MavenRepoExtension) : TaskAction() {
    override fun title(): String = "Resolve"
    override fun execute(task: Task) {
        super.execute(task)
        task.doLast(this::resolve)
        if (!config.changing()) return
        resolve(task)
    }

    private fun resolve(task: Task) {
        //prepare local file path
        val rootDir = MavenRepoPath.rootDir()
        val manifestDir = File(rootDir, MavenRepoPath.MANIFEST_DIR)
        if (!manifestDir.exists()) manifestDir.mkdirs()
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
        println("1/$total. resolve manifests:")
        if (manifests.isEmpty()) return
        println(manifests.joinToString(separator = "\n", transform = File::getPath))
        println()
        //resolve and filter repos
        val namespaces = config.locate().toSet()
        println("2/$total. locate namespaces:")
        if (namespaces.isEmpty()) return
        println(namespaces.joinToString())
        println()
        val finder: (File) -> List<Repository> = { Repository.find(it, namespaces::contains) }
        val groups = manifests.map(finder).flatten().groupBy { it.name }
        //choose best while exist repeat repo with same name
        val repos = groups.values.mapNotNull { it.maxByOrNull { e -> e.priority } }
        println("3/$total. resolve repos:")
        if (repos.isEmpty()) return
        println(repos.joinToString(separator = "\n", transform = Repository::toString))
        println()
        //sync repo
        val contentsDir = File(rootDir, MavenRepoPath.CONTENTS_DIR)
        if (!contentsDir.exists()) contentsDir.mkdirs()
        println("4/$total. sync repos:")
        val mavens = repos.mapNotNull { MavenRepoPath.sync(it, contentsDir) }
        println()
        //inject maven
        println("5/$total. inject mavens:")
        if (mavens.isEmpty()) return
        println(mavens.joinToString(separator = "\n", transform = Repository.Maven::toString))
        println()
        mavens.forEach(task.project.repositories::maven)
    }
}