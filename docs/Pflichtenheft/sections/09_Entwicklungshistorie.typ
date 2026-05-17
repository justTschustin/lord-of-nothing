= Entwicklungshistorie
Die Entwicklung orientierte sich am SCRUM‑Modell und erfolgte in Sprints. Die Sprintdauer wurde abhängig von den Terminen
der anstehenden Übungsstunden des Moduls "Softwaretechnik" gewählt.

== 1. Sprint: 19.03.2026 - 30.03.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Github Repository aufsetzen], [Justin Hesse], [https://github.com/justTschustin/lord-of-nothing],
  [initiales libGDX-Projektsetup], [Justin Hesse], [],
  [erste klickbare Grid-Logik], [Justin Hesse], [],
  [checkstyle als Linter und spotless als Formatter hinzugefügt], [Justin Hesse], [Auch automatisiert in Github Pipeline],
)

== 2. Sprint: 30.03.2026 - 09.04.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Spritedesign für House], [Luca Kaden], [],
  [Placeholder Sprites für Gebäude], [Luca Kaden], [sowohl für Gebäude, die 1x1 Tiles einnehmen, als auch 1x2 und 2x2],
  [Sidebar], [Valentin Marcinek], [Sidebar neben dem Spielgrid, wo Gebäude zum Platzieren ausgewählt werden können],
  [House als erstes platzierbares Gebäude], [Valentin Marcinek], [mit finalem Sprite],
  [Holz als erste Ressource integriert], [Valentin Marcinek], [Beinhaltet auch Anpassung, dass Ressourcen verbraucht werden, wenn House platziert wird],
  [Sawmill (Sägewerk) platzierbar], [Valentin Marcinek], [mit Placeholder Sprite],
  [Quarry (Steinbruch) platzierbar], [Valentin Marcinek], [mit Placeholder Sprite],
  [Field (Feld) platzierbar], [Valentin Marcinek], [mit Placeholder Sprite],
  [Gebäudeinspektor], [Valentin Marcinek], [sichtbar bei anklicken eines platzierten Gebäudes auf der rechten Seite],
  [Hauptmenü], [Justin Hesse], [minimale Funktionalität (Spiel starten, Einstellungen, Spiel beenden)],
  [Einstellungsmenü], [Justin Hesse], [generelle Logik mit Vollbild/Fensteranwendung-Switch, Auflösungsselector],
  [Pausenmenü], [Justin Hesse], [Button oben rechts im Spiel, welches das Spiel pausiert; ermöglicht auch öffnen des Einstellungsmenüs],
)

== 3. Sprint: 09.04.2026 - 16.04.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Spielgrid überarbeiten], [Christopher Aaron Sabbach], [Gridtiles neu skaliert, Gridgröße mit Sidebar garantieren],
  [Ticksystem], [Justin Hesse], [Zeitberechnung im Spiel -> zeigt Tag und Stunde an],
  [anpassbare Spielgeschwindigkeit], [Justin Hesse], [verändert Tempo des Ticksystems],
  [Dorfbewohner kommen ins Dorf], [Justin Hesse], [kommen im Spiel jeden Tag (Ticksystem) an],
  [Speichern der Einstellungen], [Justin Hesse], [Einstellungen werden als JSON gespeichert und beim Starten wieder geladen],
  [Sawmill Sprite], [Luca Kaden], [],
  [Sawmill platzierbar], [Valentin Marcinek], [mit finalem Sawmill Sprite],
)

== 4. Sprint: 16.04.2026 - 23.04.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Gebäude entfernen], [Christopher Aaron Sabbach], [geben Spieler 50% der Baukosten zurück],
  [Gebäude Preview beim Platzieren], [Christopher Aaron Sabbach], [],
  [Placeholder Sprites für Ressourcen], [Luca Kaden], [Holz, Nahrung, Dorfbewohner, Steine, Soldaten, Zeit],
  [Gebäude-/Ressourcengenerierungslogik], [Valentin Marcinek], [Dorfbewohner können Gebäude zugeordnet werden und Gebäude generiert Ressourcen, wenn Bewohner diesem zugeordnet sind],
  [Kaserne], [Valentin Marcinek], [platzierbar, zugeordnete Bewohner zählen als Soldaten],
)

== 5. Sprint: 23.04.2026 - 05.05.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Hauptmenü Hintergrund Design], [Christopher Aaron Sabbach], [],
  [Gebäudesprites], [Luca Kaden], [Sprites für Kaserne, Quarry, Feld],
  [Spiel speichern und laden], [Justin Hesse], [als JSON gespeichert],
  [Popups], [Justin Hesse], [Popups im Spiel mit Nachrichten an den Spieler, welche das Spiel pausieren und weggeklickt werden],
  [minimale Raid-Logik], [Justin Hesse], [In definierten Intervallen werden Banditenüberfälle via Popup angekündigt und geschehen via Popup (wie viele angreifen, ohne Gameover, ohne Verluste)],
  [Pflichtenheft, Dokumentation], [alle], [WIP, im Fokus],
)

== 6. Sprint: 05.05.2026 - 11.05.2026

#table(
  columns: (auto, auto, auto),
  [Feature], [Autor], [Notiz],
  [Sprites für Ressourcen], [Luca Kaden], [Sprites für Holz, Stein, Nahrung, Bewohner, Zeit, Soldaten],
  [Hintergrundmusik], [Luca Kaden], [verstellbare Lautstärke in Einstellungen],
  [Implementierung Hauptmenü-Hintergrund], [Luca Kaden], [],
  [Raid-Logik], [Justin Hesse], [Verluste anhand einer Gaußschen Glockenkurve berechnet (je nach Anzahl der Angreifenden und Verteidigenden), GameOver],
  [GameOver-Screen], [Justin Hesse], [durch Raid möglich],
  [Eventlog], [Valentin Marcinek], [Eventlog in unterer rechter Ecke, Log für Popup-Nachrichten, Ankünfte von Bewohnern],
  [Menü-/HUD-Texturen], [Valentin Marcinek], [],
  [Sounds], [Valentin Marcinek], [Menü-Klick-Sound, Gebäude-Platzieren-Sound],
  [Raid Banner Texturen], [Christopher Aaron Sabbach], [Design für Raid Banners (Ankündigungen, Geschehen)],
  [Interface/Menü Design Overhaul], [Christopher Aaron Sabbach], [Implementierung neuer HUD-Texturen],
  [Raid Design Overhaul], [Christopher Aaron Sabbach], [Implementierung neuer Raid Banner mit Soundeffekten],
)

== 7. Sprint: 11.05.2026 - 17.05.2026

#table(
  columns: (auto, auto, 1fr),
  [Feature], [Autor], [Notiz],
  [Playtesting & Balancing], [alle], [Intervalle, Generierung, Angreifen usw. anpassen],
  [Dokumentation], [alle], [],
  [Tastatur Shortcuts], [Justin Hesse], [ESC für Pause und Gebäude abwählen, Nummertasten für Spielgeschwindigkeit],
  [Tutorial], [Justin Hesse], [Aufrufbar in Haupt- und Pausenmenü],
)
