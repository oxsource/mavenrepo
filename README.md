## WHAT

A simple gradle plugin:

1. Manage and switch maven repositories under multiple namespaces;
2. Very convenient for private git maven warehouse management.

## HOW

1. project 'build.gradle'

<pre>
buildscript {
    dependencies {
        classpath('io.github.oxsource:maven-repo-plugin:1.0.0')
    }
}
</pre>

2. module 'build.gradle'

<pre>
plugins {
    id 'pizzk.gradle.maven.repo'
}
...
mavenrepo {
    changing(false)
    manifests {
        manifestLocal()
        manifestGitee(false)
    }
    namespace {
        include(["local", "jitpack"])
    }
}
</pre>

3. run gradle task help

<div style="display: block; margin-left: auto; margin-right: auto;">
    <img src="./doc/help.png" alt="help" width="479" height="411" />
</div>