package pizzk.gradle.plugin.support

import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.w3c.dom.Element
import pizzk.gradle.plugin.extension.Namespace
import java.io.File
import java.net.URI
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class Repository(
    val name: String,
    val url: String,
    val priority: Int = 0,
    val type: String? = null,
    val branch: String? = null,
) {

    override fun toString(): String = "$name|$url|$priority"

    class Maven(val name: String, val url: URI) : Action<MavenArtifactRepository> {
        override fun execute(el: MavenArtifactRepository) {
            el.name = name
            el.url = url
        }

        override fun toString(): String = "$name -> $url"
    }

    object Node {
        const val TAG_GROUP = "group"
        const val TAG_REPO = "repo"

        const val ATTR_NAME = "name"
        const val ATTR_URL = "url"
        const val ATTR_PRIORITY = "priority"
        const val ATTR_TYPE = "type"
        const val ATTR_BRANCH = "branch"

        const val TYPE_GIT = "git"

        fun <T> list(node: Element, tag: String, map: (Pair<String, Element>) -> T?): List<T> {
            if (tag.isEmpty()) return emptyList()
            val nodes = node.getElementsByTagName(tag)
            val values: MutableList<T> = LinkedList()
            for (i in 0 until nodes.length) {
                val e = nodes.item(i) as? Element ?: continue
                val name = e.getAttribute(ATTR_NAME)
                if (name.isEmpty()) continue
                map(Pair(name, e))?.let(values::add)
            }
            return values
        }

        fun value(key: String, vararg nodes: Element): String {
            nodes.forEach { e ->
                val value = e.getAttribute(key)
                if (value.isNotEmpty()) return value
            }
            return ""
        }
    }

    companion object {

        /**
         * see resources/manifest.xml
         */
        fun find(file: File, namespace: Set<String>): List<Repository> {
            if (!file.exists() || file.length() <= 0) return emptyList()
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)
            val groups = namespace.map(Namespace.Companion::split).map { it.first }
            //filter and map group
            return Node.list(document.documentElement, Node.TAG_GROUP) group@{ group ->
                if (!groups.contains(group.first)) return@group null
                //filter and map repo
                return@group Node.list(group.second, Node.TAG_REPO) repo@{ repo ->
                    val name = Namespace.compose(group.first, repo.first)
                    if (!namespace.contains(name)) return@repo null
                    val url = Node.value(Node.ATTR_URL, repo.second, group.second)
                    val type = Node.value(Node.ATTR_TYPE, repo.second, group.second)
                    //attr only in repo node
                    val priority = Node.value(Node.ATTR_PRIORITY, repo.second).toIntOrNull() ?: 0
                    val branch = Node.value(Node.ATTR_BRANCH, repo.second)
                    return@repo Repository(name, url, priority, type, branch)
                }
            }.flatten()
        }
    }
}