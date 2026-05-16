= Nicht-Funktionale Anforderungen

Die folgende Tabelle beschreibt die nicht-funktionalen Anforderungen. Jede Anforderung besitzt einen eindeutigen Bezeichner der Form NFA-XXX.

#table(
  columns: (auto, auto, 1fr),
  [Bezeichner], [Anforderung], [Beschreibung],
  [NFA-010], [Plattformkompatibilität], [Das Spiel muss auf Desktop-PCs unter Windows, macOS und Linux lauffähig sein.],
  [NFA-020], [Offline-Nutzbarkeit], [Das Spiel muss vollständig offline nutzbar sein und darf keine Internetverbindung zum Spielen benötigen.],
  [NFA-030], [Leistungsanforderung], [Das Spiel soll auf durchschnittlicher Hardware flüssig laufen; die grafischen und spielmechanischen Anforderungen sind deshalb auf einen ressourcenschonenden Desktop-Betrieb ausgelegt.],
  [NFA-040], [Benutzbarkeit], [Die Bedienung soll für Gelegenheitsspieler verständlich und möglichst intuitiv sein.],
  [NFA-050], [Persistenz], [Einstellungen und Spielstände müssen zuverlässig als JSON gespeichert und beim Laden validiert werden, sodass fehlende oder ungültige Werte durch Standardwerte ersetzt werden können.],
  [NFA-060], [Fehlertoleranz], [Die Anwendung soll stabil mit Fehlern in Konfigurations- oder Savegame-Dateien umgehen und dabei keine unbenutzbaren Daten erzeugen.],
  [NFA-070], [Grafische Bedienung], [Das Spiel soll ohne Konsolenbedienung nutzbar sein; die Steuerung erfolgt über die grafische Benutzeroberfläche.],
)
