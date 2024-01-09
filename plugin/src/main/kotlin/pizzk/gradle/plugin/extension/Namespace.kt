package pizzk.gradle.plugin.extension

class Namespace {
    companion object {
        const val SCOPE_ALL = "*"
        const val SCOPE_CUR = "."
        private const val SPLIT = ":"
        private const val GROUP_DEFAULT = "default"
        fun segment(s: String): Boolean = s.contains(SPLIT)
        fun split(s: String): Pair<String, String> {
            val parts = s.split(SPLIT)
            if (parts.size < 2) return Pair(GROUP_DEFAULT, s)
            return Pair(parts[0], parts[1])
        }

        fun compose(group: String, name: String): String {
            return if (group == GROUP_DEFAULT) name else "$group$SPLIT$name"
        }
    }

    private val values: MutableMap<String, Set<String>> = mutableMapOf()
    fun include(names: List<String>, scope: Set<String>) {
        names.map(String::trim).filter(String::isNotEmpty).forEach { values[it] = scope }
    }

    internal fun value(): Map<String, Set<String>> = values
    override fun toString(): String = value().map { "${it.key}:${it.value.joinToString()}" }.joinToString()
}