package pizzk.gradle.plugin.extension

class Locate {
    private val values: MutableSet<String> = mutableSetOf()
    fun name(value: String?) {
        value ?: return
        val s = value.trim()
        if (s.isEmpty()) return
        values.add(s)
    }

    fun value(): Set<String> = values

    override fun toString(): String = value().joinToString()
}