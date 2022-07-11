plugins { id("io.vacco.oss.gitflow") version "0.9.8" }

group = "io.vacco.vapula"
version = "0.1.0"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
  sharedLibrary(true, false)
}

dependencies {
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("redis.clients:jedis:3.0.1")

  testImplementation("io.vacco.shax:shax:1.7.30.0.0.7")
  testImplementation("com.google.code.gson:gson:2.9.0")
}
