package pizzk.gradle.plugin.task

import org.gradle.api.Task
import pizzk.gradle.plugin.support.PathContext
import pizzk.gradle.plugin.extension.Manifest
import java.io.File
import java.io.InputStreamReader


class HelpTask : TaskAction() {
    override fun title(): String = "Help"

    override fun execute(task: Task) {
        super.execute(task)
        task.doLast {
            val rootDir = PathContext.rootDir()
            val manifestDir = File(rootDir, PathContext.MANIFEST_DIR)
            val contentDir = File(rootDir, PathContext.CONTENTS_DIR)
            var i = 1
            println("---BASIC--")
            println("${i++}. local root dir: [${rootDir.path}];")
            println("${i}. manifest.xml config reference:")
            val classLoader: ClassLoader = PathContext::class.java.getClassLoader()
            classLoader.getResourceAsStream("manifest.xml")?.use { ins ->
                InputStreamReader(ins).readLines().forEach(System.out::println)
            }
            println()
            i = 1
            println("---USAGE--")
            println("${i++}. changing(changing: Boolean): resolve ignore cache while set changing true;")
            println("${i++}. scope(value: List<String>): extension api add to which projects;")
            println("${i++}. manifestLocal(): `${File(manifestDir, Manifest.NAME)}`;")
            println("${i++}. manifestGitee(changing: Boolean): `${Manifest.GITEE}`;")
            println("${i++}. manifest(url: String, changing: Boolean): local or http ${Manifest.NAME};")
            println("${i}. include(names: List<String>, scope: List<String>): inject named repo into which project's repositories;")
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