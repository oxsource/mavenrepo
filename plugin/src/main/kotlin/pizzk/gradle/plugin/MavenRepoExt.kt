package pizzk.gradle.plugin

import pizzk.gradle.plugin.comm.GitCommand
import pizzk.gradle.plugin.comm.MavenRepoPath
import java.io.File
import java.net.URI

class MavenRepoExt {
    companion object {
        const val NAME = "MavenRepoExt"
    }

    fun findMavenLocalURI(name: String?): URI? {
        return kotlin.runCatching {
            if (name.isNullOrEmpty()) return@runCatching null
            val rootDir = MavenRepoPath.rootDir()
            val contentsDir = File(rootDir, MavenRepoPath.CONTENTS_DIR)
            val mavenDir = MavenRepoPath.namespaceDir(contentsDir, name)
            val remoteUrl = GitCommand.remoteUrl(mavenDir)
            if (remoteUrl.isNullOrEmpty()) return@runCatching null
            return@runCatching mavenDir.toURI()
        }.getOrNull()
    }
}