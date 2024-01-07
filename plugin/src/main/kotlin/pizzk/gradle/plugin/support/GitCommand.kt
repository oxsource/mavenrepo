package pizzk.gradle.plugin.support

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object GitCommand {
    private const val SPACE_SPLIT = " "
    private const val SUCCESS_CODE = 0
    fun remoteUrl(repository: File): String? = kotlin.runCatching {
        val builder = ProcessBuilder("git", "config", "--get", "remote.origin.url")
        builder.directory(repository)
        val process = builder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val remoteUrl = reader.readLine()
        val code = process.waitFor()
        return@runCatching if (code == SUCCESS_CODE) remoteUrl else null
    }.getOrNull()

    fun pull(repository: File, branch: String? = null): Boolean {
        return kotlin.runCatching {
            val args: MutableList<String> = ArrayList(4)
            args.add("git")
            args.add("pull")
            if (!branch.isNullOrEmpty()) {
                args.add("origin")
                args.add(branch)
            }
            val builder = ProcessBuilder(*args.toTypedArray())
            println(args.joinToString(separator = SPACE_SPLIT))
            builder.directory(repository)
            val process = builder.start()
            val code = process.waitFor()
            if (code != SUCCESS_CODE) throw Exception("code $code.")
            return@runCatching true
        }.onFailure { System.err.println("git pull panic: ${it.message}") }.getOrNull() == true
    }

    fun clone(repository: File, url: String, branch: String? = null): Boolean {
        return kotlin.runCatching {
            val args: MutableList<String> = ArrayList(6)
            args.add("git")
            args.add("clone")
            if (!branch.isNullOrEmpty()) {
                args.add("-b")
                args.add(branch)
            }
            args.add(url)
            println(args.joinToString(separator = SPACE_SPLIT))
            args.add(repository.absolutePath)
            val process = ProcessBuilder(*args.toTypedArray()).start()
            val code = process.waitFor()
            if (code != SUCCESS_CODE) throw Exception("code $code.")
            return@runCatching true
        }.onFailure { System.err.println("git clone panic: ${it.message}") }.getOrNull() == true
    }

    fun sync(repository: Repository, dir: File): Repository.Maven? {
        if (!dir.isDirectory) throw Exception("${dir.path} is not a directory.")
        val mavenDir = MavenRepoPath.namespaceDir(dir, repository.name)
        val url = remoteUrl(mavenDir)
        val success = if (repository.url != url) {
            if (mavenDir.exists()) {
                println("${dir.absolutePath} remoteUrl mismatch, will be delete to update")
                mavenDir.deleteRecursively()
            }
            clone(mavenDir, repository.url, repository.branch)
        } else {
            pull(mavenDir, repository.branch)
        }
        if (!success) return null
        return Repository.Maven(repository.name, mavenDir.toURI())
    }
}