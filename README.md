## WHAT

A simple gradle plugin:

1. Manage and switch maven repositories under multiple namespaces;
2. Very convenient for private git maven warehouse management.

## HOW

*support gradle version('gradle/wrapper/gradle-wrapper.properties'): gradle-7.6+*
<pre>
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
</pre>

1. project 'build.gradle'

<pre>
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath('io.github.oxsource:maven-repo-plugin:1.0.2')
    }
}
...

apply plugin: 'pizzk.gradle.maven.repo'

mavenrepo {
    changing(false)
    manifests {
        manifestLocal()
        manifestGitee(false)
    }
    namespace {
        include(["jitpack"], ['*'])
    }
}
</pre>

2. you can run gradle task 'mavenrepoHelp' for help

<pre>
Task :usage:mavenrepoHelp
---BASIC--
1. local root dir: [/Users/pizzk/.m2repo];
2. manifest.xml config reference:
</pre>

    <?xml version="1.0" encoding="utf-8" ?>
    <manifest>
    <group name="default">
        <repo name="jitpack" url="https://jitpack.io"/>
    </group>
    <group name="<group>(required)" url="[git:|http:|file:](required)" type="[git](optional)">
        <repo name="<name>(required)" url="[@](required|inherit)" type="[@](required|inherit)" branch="[*](optional)" priority="[1-100](optional)"/>
    </group>
    </manifest>

<pre>
---USAGE--
1. changing(changing: Boolean): resolve ignore cache while set changing true;
2. scope(value: List<String>): extension api add to which projects;
3. manifestLocal(): `/Users/pizzk/.m2repo/manifests/manifest.xml`;
4. manifestGitee(changing: Boolean): `https://gitee.com/oxsource/mavenrepo.manifest/raw/main/manifest.xml`;
5. manifest(url: String, changing: Boolean): local or http manifest.xml;
6. include(names: List<String>, scope: List<String>): inject named repo into which project's repositories;

---WORKFLOW--
1. collect manifests then choose matching repo via include name and priority;
2. clone or pull manifest git repo into(/Users/pizzk/.m2repo/contents) sub dir;
3. repo transform to `maven{name,url}` and inject into `repositories` via policy;
</pre>
