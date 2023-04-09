
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

dependencies {
    implementation(project(":data"))
    implementation(project(":web"))
    implementation(project(":user"))
    implementation(project(":file"))
    implementation(project(":place"))
    implementation(project(":item"))
    implementation(project(":character"))
    implementation(project(":tag"))
    implementation("org.apache.pdfbox:pdfbox:3.0.0-alpha3")
    implementation(files("../libs/hwplib-1.1.2.jar"))
}
