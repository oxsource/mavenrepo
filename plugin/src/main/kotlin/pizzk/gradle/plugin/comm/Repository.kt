package pizzk.gradle.plugin.comm

import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.w3c.dom.Element
import java.io.File
import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

class Repository(
        val name: String,
        val url: String,
        val priority: Int = 0,
        val type: String? = null,
        val branch: String? = null,
) {

    override fun toString(): String = "$name|$url|$priority"

    class Maven(private val name: String, private val url: URI) : Action<MavenArtifactRepository> {
        override fun execute(el: MavenArtifactRepository) {
            el.name = name
            el.url = url
        }

        override fun toString(): String = "$name -> $url"
    }

    companion object {
        private const val TAG_REPO = "repo"
        private const val ATTR_NAME = "name"
        private const val ATTR_URL = "url"
        private const val ATTR_PRIORITY = "priority"
        private const val ATTR_TYPE = "type"
        private const val ATTR_BRANCH = "branch"

        const val TYPE_GIT = "git"
        const val NAMESPACE_SPLIT = ":"

        /**
         * see resources/manifest.xml
         */
        fun find(file: File, match: (String) -> Boolean): List<Repository> {
            if (!file.exists() || file.length() <= 0) return emptyList()
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)
            val nodes = document.getElementsByTagName(TAG_REPO)
            val values: MutableList<Repository> = mutableListOf()
            for (i in 0 until nodes.length) {
                val node = nodes.item(i) as? Element ?: continue
                val name = node.getAttribute(ATTR_NAME)
                if (!match(name)) continue
                val url = node.getAttribute(ATTR_URL)
                if (url.isEmpty()) continue
                val priority = node.getAttribute(ATTR_PRIORITY).toIntOrNull() ?: 0
                val type = node.getAttribute(ATTR_TYPE).orEmpty()
                val branch = node.getAttribute(ATTR_BRANCH).orEmpty()
                values.add(Repository(name, url, priority, type, branch))
            }
            return values
        }
    }
}