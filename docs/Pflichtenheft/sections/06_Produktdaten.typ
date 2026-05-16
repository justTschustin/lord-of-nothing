= Produktdaten

Ein Datensatz ist eine logisch zusammengehörige Menge gespeicherter Werte. Ein Datenobjekt ist die entsprechende Struktur im Code, die diese Werte zur Laufzeit hält und bei Bedarf als JSON speichert oder lädt.

== Datensätze und Datenobjekte

`GameState` ist der zentrale Spielstand als Persistenz-Snapshot. Er enthält die Ressourcen, das Grid, den aktuellen Spieltag, geplante Raid-Termine und die gespeicherten Eventlog-Nachrichten.

`GameState.GridState` beschreibt das Spielfeld mit Breite, Höhe, allen Tiles und den platzierten Gebäuden.

`GameState.TileState` beschreibt ein einzelnes Grid-Feld mit Koordinaten, Tile-Typ, optionalem Gebäudetyp und optional zugewiesenen Dorfbewohnern.

`GameState.BuildingPlacementState` beschreibt eine Gebäudeplatzierung mit Gebäudetyp, Position und aktueller Arbeiteranzahl.

`GameSettings` speichert benutzerspezifische Einstellungen wie Fullscreen-Modus, Fenstergröße, Spielgeschwindigkeit sowie Lautstärke.

`ResolutionDto` beschreibt eine auswählbare Bildschirmauflösung mit Breite und Höhe.

`ResourceType` definiert die gespeicherten Ressourcentypen des Spiels, darunter Holz, Stein, Nahrung, Dorfbewohner, Kapazität und Soldaten.

Die Datei `settings.json` speichert die Benutzer- und Anzeigeeinstellungen, während `savegame.json` den gespeicherten Spielstand mit den aktuellen Spieldaten enthält.

