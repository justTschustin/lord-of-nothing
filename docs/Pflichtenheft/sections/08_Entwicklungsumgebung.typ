= Entwicklungsumgebung

== Software

Die Entwicklungsumgebung basiert auf einer modernen Java-Toolchain, um Reproduzierbarkeit und Code-Qualität sicherzustellen:

- *JDK 21* (empfohlen: Eclipse Adoptium / Temurin 21): Erforderlich für den Build-Prozess und die Ausführung des Gradle-Wrappers. Das Spiel selbst nutzt Java-8-Sprachfeatures für maximale Kompatibilität.
- *Gradle 9.4.0 (Wrapper)*: Zentrale Build-Automatisierung für Dependency-Management und Asset-Packaging. Durch den Wrapper (`gradlew`) ist keine manuelle Installation erforderlich.
- *Git*: Versionsverwaltung und Koordination der Zusammenarbeit über ein entferntes Repository.
- *IntelliJ IDEA* (empfohlen): Bevorzugte IDE aufgrund der exzellenten Unterstützung für Gradle-Projekte und libGDX-Workflows.
- *Checkstyle 13.4.0 & Spotless 6.25.0*: In den Build-Prozess integrierte Linter und Formatter, die die Einhaltung der vereinbarten Coding-Guidelines (Google Java Style) automatisiert erzwingen.

== Hardware

Die Entwicklung und das Testen erfolgten primär auf zwei unterschiedlichen Systemarchitekturen, um die Cross-Plattform-Kompatibilität des LWJGL3-Backends zu verifizieren:

- *Testsysteme*: Die aktive Entwicklung fand auf Workstations unter **Windows 11** sowie auf Laptops unter **macOS (Apple Silicon)** statt.
- *CPU*: Multi-Core-Prozessoren (x86-64 und ARM64-Architektur).
- *RAM*: Mindestens 8 GB (16 GB empfohlen für den parallelen Betrieb von IDE und Gradle-Daemon).
- *Grafik*: Integrierte Grafiklösungen (Intel UHD / Apple M-Series GPU) sind für die flüssige Darstellung des Grid-Renderings ausreichend.

== Orgware

- *GitHub-Workflow*: Nutzung von GitHub zur Aufgabenverwaltung (Issues) und zur Qualitätssicherung. Der Schutz des `main`-Branch erfordert zwingend Peer-Reviews über Pull Requests.
- *Branching-Strategie*: Feature-basierte Entwicklung in separaten Zweigen zur Vermeidung von Code-Konflikten und zur sauberen Trennung der Meilensteine.
- *Automatisierung*: Nutzung der integrierten Gradle-Tasks zur lokalen Verifikation des Code-Styles vor jedem Commit.
