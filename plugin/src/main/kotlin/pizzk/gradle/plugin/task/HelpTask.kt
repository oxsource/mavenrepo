package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.comm.MavenRepoPath
import pizzk.gradle.plugin.extension.Manifest
import java.io.File
import java.io.InputStreamReader


class HelpTask : TaskAction() {
    override fun title(): String = "Help"

    override fun execute(task: Task) {
        super.execute(task)
        task.doLast {
            val rootDir = MavenRepoPath.rootDir()
            val manifestDir = File(rootDir, MavenRepoPath.MANIFEST_DIR)
            val contentDir = File(rootDir, MavenRepoPath.CONTENTS_DIR)
            var i = 1
            println("${i++}. rootDir: [${rootDir.path}];")
            println("${i++}. manifests { it.local() }: [${File(manifestDir, Manifest.NAME)}];")
            println("${i++}. manifests { it.gitee([changing]) }: built-it public manifest(ignore exist while changing is true)")
            println("${i++}. manifests { it.url(<path>, [changing]) }: support local or http remote file;")
            println("${i++}. manifest example:")
            val classLoader: ClassLoader = MavenRepoPath::class.java.getClassLoader()
            classLoader.getResourceAsStream("manifest.xml")?.use { ins ->
                InputStreamReader(ins).readLines().forEach(System.out::println)
            }
            println("${i++}. once plugin sync, it will collect manifest and locate choose matching repo;")
            println("${i++}. then clone into local(${contentDir.path}) if need;")
            println("${i++}. finally auto inject them into `project.repositories`;")
            println("${i}. mavenrepo { changing(false) } can close auto resolve`.")
        }
    }
}