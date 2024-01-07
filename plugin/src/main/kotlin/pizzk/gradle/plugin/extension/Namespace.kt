package pizzk.gradle.plugin.extension

class Namespace {
    private val values: MutableSet<String> = mutableSetOf()
    fun name(value: String?) {
        value ?: return
        val s = value.trim()
        if (s.isEmpty()) return
        values.add(s)
    }

    fun names(vararg values: String) {
        if (values.isEmpty()) return
        values.forEach(this::name)
    }

    fun value(): Set<String> = values

    override fun toString(): String = value().joinToString()
}