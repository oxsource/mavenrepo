package pizzk.gradle.plugin.support

import org.gradle.api.Project
import pizzk.gradle.plugin.comm.GlobalContext
import pizzk.gradle.plugin.index.MavenRepoApi
import java.io.File

object ScriptContext {
    private const val DIR_SCRIPT = "scripts"
    private const val DIR_GRADLE = "gradle"
    private const val SUFFIX = ".gradle"
    private val values: MutableMap<String, (Any?) -> Any> = mutableMapOf()
    private val allow: MutableSet<String> = mutableSetOf()
    private fun path(dir: String, path: String): String {
        val values = when {
            dir.isEmpty() -> arrayOf(path)
            else -> arrayOf(dir, DIR_SCRIPT, DIR_GRADLE, path)
        }
        val value = values.joinToString(separator = File.separator)
        return if (value.endsWith(SUFFIX)) value else "$value$SUFFIX"
    }

    fun mount(node: String, value: (Any?) -> Any) {
        if (!allow.contains(node)) return
        values[node] = value
    }

    fun allow(node: String): Boolean {
        return allow.contains(node)
    }

    class Script(private val project: Project, private val repo: String) {
        fun load(path: String, node: String, args: Any? = null): Any? {
            if (path.isEmpty() || node.isEmpty()) return null
            if (repo.isEmpty() && path.isEmpty()) return null
            val api = GlobalContext.value<MavenRepoApi>() ?: return null
            val file = path(api.dir(repo), path)
            allow.add(node)
            try {
                project.apply { it.from(file) }
                return values[node]?.invoke(args)
            } catch (exception: Exception) {
                throw exception
            } finally {
                allow.remove(node)
                values.remove(node)
            }
        }

        fun load(path: String, node: String): Any? = load(path, node, args = null)
    }
}