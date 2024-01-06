package pizzk.gradle.plugin.extension

class Manifest {
    companion object {
        const val NAME = "manifest.xml"
        const val GITEE = "https://gitee.com/oxsource/mavenrepo.manifest/raw/main/manifest.xml"
    }

    private val values: MutableMap<String, Boolean> = mutableMapOf()
    fun local() = url(NAME, changing = false)
    fun gitee(changing: Boolean = false) = url(GITEE, changing)

    fun url(value: String?, changing: Boolean = false) {
        value ?: return
        val s = value.trim()
        if (s.isEmpty()) return
        values[s] = changing
    }

    fun value(): Map<String, Boolean> = values

    override fun toString(): String = value().map { "${it.key}|${it.value}" }.joinToString()
}