= Entwicklungsumgebung [zur Entwicklung der Software]

== Software

Zur Entwicklung des Projekts wird folgende Software benötigt:

- *JDK 21* (empfohlen: OpenJDK Temurin 21): Zum Kompilieren und Ausführen des Projekts aus der Entwicklungsumgebung heraus. Das Projekt selbst zielt auf Java-8-Kompatibilität ab (`sourceCompatibility = 8`), erfordert aber JDK 21 für den Build-Prozess sowie zur Nutzung des Gradle-Wrappers.
- *Gradle 9.4.0*: Build-Automatisierung. Wird über den mitgelieferten Gradle-Wrapper (`gradlew`) automatisch bereitgestellt – eine separate Installation ist nicht notwendig.
- *Git*: Versionskontrolle. Zum Klonen des Repositories und zur Zusammenarbeit im Team.
- *IntelliJ IDEA* (empfohlen) oder *Eclipse*: Beide IDEs werden durch die Build-Konfiguration unterstützt. IntelliJ IDEA ist aufgrund der nativen Gradle-Integration empfohlen.
- *Checkstyle 13.4.0* und *Spotless 6.25.0*: Sind in den Gradle-Build integriert und erzwingen einheitlichen Code-Stil. Eine separate Installation entfällt.

== Hardware

Die Entwicklungsumgebung stellt keine außergewöhnlichen Hardwareanforderungen. Es gelten die üblichen Empfehlungen für Java-Entwicklung mit einer modernen IDE:

- *CPU*: Aktueller Multi-Core-Prozessor (x86-64)
- *RAM*: Mindestens 8 GB (empfohlen: 16 GB, insbesondere bei Betrieb von IDE, JVM und Gradle-Daemon gleichzeitig)
- *Speicher*: Mindestens 5 GB freier Festplattenspeicher (IDE, JDK, Gradle-Cache, Projektdateien)
- *Betriebssystem*: Windows 10/11, macOS oder Linux (64-Bit)

Eine dedizierte Grafikkarte ist für die Entwicklung nicht erforderlich; die integrierte Grafik reicht zum Starten und Testen des Spiels aus.

== Orgware

- *GitHub*: Das Projekt wird in einem GitHub-Repository verwaltet. GitHub Issues und Pull Requests dienen zur Aufgabenverwaltung und Code-Review.
- *Gradle Wrapper*: Stellt sicher, dass alle Entwickler dieselbe Gradle-Version verwenden, ohne eine globale Installation vornehmen zu müssen.
