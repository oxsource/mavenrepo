package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.support.MavenRepoPath
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
            println("---BASIC--")
            println("${i++}. local root dir: [${rootDir.path}];")
            println("${i}. manifest.xml config reference:")
            val classLoader: ClassLoader = MavenRepoPath::class.java.getClassLoader()
            classLoader.getResourceAsStream("manifest.xml")?.use { ins ->
                InputStreamReader(ins).readLines().forEach(System.out::println)
            }
            println()
            i = 1
            println("---USAGE--")
            println("${i++}. manifestLocal(): `${File(manifestDir, Manifest.NAME)}`;")
            println("${i++}. manifestGitee(changing: Boolean): `${Manifest.GITEE}`;")
            println("${i++}. manifest(url: String, changing: Boolean): local or http ${Manifest.NAME};")
            println("${i}. include(names: List<String>, global: Boolean): inject named repo into repositories for all or current project;")
            println()
            i = 1
            println("---WORKFLOW--")
            println("${i++}. collect manifests then choose matching repo via include name and priority;")
            println("${i++}. clone or pull manifest git repo into(${contentDir.path}) sub dir;")
            println("${i}. repo transform to `maven{name,url}` and inject into `repositories` via policy;")
            println()
        }
    }
}