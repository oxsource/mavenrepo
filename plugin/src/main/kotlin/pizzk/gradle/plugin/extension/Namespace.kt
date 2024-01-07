package pizzk.gradle.plugin.extension

class Namespace {
    enum class Policy(val desc: String) {
        ALL("ALL"), PROJECT("PROJECT"), NONE("NONE")
    }

    private val values: MutableMap<String, Policy> = mutableMapOf()
    fun include(names: List<String>, policy: Policy) {
        names.map(String::trim).filter(String::isNotEmpty).forEach { values[it] = policy }
    }

    internal fun value(): Map<String, Policy> = values
    override fun toString(): String = value().map { "${it.key}|${it.value.desc}" }.joinToString()
}