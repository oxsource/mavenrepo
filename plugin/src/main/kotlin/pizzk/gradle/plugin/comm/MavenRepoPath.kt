package pizzk.gradle.plugin.comm

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URL

object MavenRepoPath {
    private const val JVM_USER_HOME = "user.home"
    private const val HTTP = "http"
    private const val BASE_DIR = ".m2repo"
    const val MANIFEST_DIR = "manifests"
    const val CONTENTS_DIR = "contents"
    fun rootDir(): File = File(System.getProperty(JVM_USER_HOME), BASE_DIR)
    fun http(path: String): Boolean = path.startsWith(HTTP, ignoreCase = true)
    fun download(url: String, dir: File, changing: Boolean): File? {
        return kotlin.runCatching {
            if (!dir.isDirectory) throw Exception("${dir.path} is not a directory")
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val name = request.url.pathSegments.joinToString(separator = "_")
            val file = File(File(dir, request.url.host), name)
            if (file.exists()) {
                if (!changing && file.length() > 0) return@runCatching file
                file.delete()
            }
            val bytes = client.newCall(request).execute().body?.bytes() ?: return@runCatching null
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            file.writeBytes(bytes)
            return@runCatching file
        }.onFailure { System.err.println("download panic: ${it.message}") }.getOrNull()
    }

    fun sync(repository: Repository, dir: File): Repository.Maven? {
        return kotlin.runCatching {
            println("${repository.name} sync...")
            return@runCatching when {
                repository.type == Repository.TYPE_GIT -> GitCommand.sync(repository, dir)
                http(repository.url) -> Repository.Maven(repository.name, URL(repository.url).toURI())
                else -> {
                    val file = File(repository.url)
                    if (!file.isDirectory || !file.exists()) return null
                    Repository.Maven(repository.name, file.toURI())
                }
            }
        }.onFailure { e -> System.err.println("clone panic: ${e.message}") }.getOrNull()
    }
}