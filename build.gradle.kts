plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

allprojects {
    group = "io.github.lipinskipawel"
    version = "7.0.2" // or "7.0.2-SNAPSHOT" for snapshots
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
