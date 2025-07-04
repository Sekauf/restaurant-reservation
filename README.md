# Restaurant-Reservierungssystem

Dieses Projekt implementiert ein einfaches **Restaurant-Reservierungssystem** als Maven-Java-Anwendung (JDK 17). Es bietet eine Swing-basierte grafische Oberfläche und verwendet eine SQLite-Datenbank zur persistenten Speicherung der Reservierungen.

## Aufbau und Funktionsumfang

- **GUI (Swing):** Ermöglicht das Anlegen neuer Reservierungen über ein Formular (Name des Gasts sowie Auswahl von Datum, Uhrzeit, Personenzahl und Tisch-Nr über Dropdown-Menüs) und listet alle vorhandenen Reservierungen in einer Tabelle auf. Eine ausgewählte Reservierung kann per Knopfdruck wieder gelöscht werden.
- **Datenbank (SQLite):** Die Reservierungen werden in der Datei `db/restaurant.db` gespeichert. Beim ersten Start der Anwendung wird die benötigte Tabelle automatisch angelegt. Fehlt der Ordner `db`, wird er ebenfalls erzeugt, sodass alle Reservierungsdaten zwischen Programmstarts erhalten bleiben.
- **Verhindern von Doppelbuchungen:** Das System prüft beim Anlegen einer Reservierung, ob für die Kombination aus Tisch-Nr und Datum/Uhrzeit bereits eine Reservierung existiert, und verhindert ggf. doppelte Einträge.

## Projektstruktur

Das Projekt folgt einer Schichtentrennung nach MVC:
- `com.restaurant.reservation.model` – Datenklasse **Reservation** (Reservierungsmodell mit Feldern und Methoden).
- `com.restaurant.reservation.dao` – Datenzugriffsschicht (**DAO** für SQLite) zum Ausführen der CRUD-Operationen auf der Datenbank.
- `com.restaurant.reservation.service` – Geschäftslogik-Schicht (**Service**), die Prüfungen vornimmt (z.B. Doppelbuchungen) und DAO-Aufrufe kapselt.
- `com.restaurant.reservation.ui` – Präsentationsschicht (**Swing UI**); enthält das Hauptfenster, Dialoge und den Programmstart.

Zusätzlich enthält der Ordner `docs` bereitgestellte Dokumente (z.B. Projektbeschreibung, GUI-Entwurf), die das Projekt begleiten.

## Nutzung

1. **Voraussetzungen:** Installiertes JDK 17 und Maven. Optional kann das Projekt in einer IDE (z.B. IntelliJ IDEA) geöffnet werden.
2. **Build:** Mit `mvn clean compile` kann das Projekt kompiliert werden. Die benötigte SQLite-JDBC-Bibliothek wird durch Maven automatisch geladen.
3. **Starten der Anwendung:**
    - In IntelliJ IDEA: Die Klasse `MainApp` (im Paket `com.restaurant.reservation.ui`) mit der `main`-Methode ausführen.
    - Alternativ auf der Kommandozeile: `mvn exec:java -Dexec.mainClass=com.restaurant.reservation.ui.MainApp` (erfordert Maven Exec Plugin).
4. **Bedienung:** Im gestarteten Programm können über das Formular unten neue Reservierungen hinzugefügt werden (`Hinzufügen`). Die Liste oben zeigt alle Reservierungen mit ID, Name, Datum, Uhrzeit, Personenzahl und Tisch-Nr. Zum **Löschen** einer Reservierung zunächst einen Eintrag in der Tabelle markieren und dann auf `Löschen` klicken. Bei fehlerhaften Eingaben oder Datenbankfehlern erscheinen entsprechende Fehlermeldungen.
5. **Beenden:** Das Programm kann über das Schließen des Fensters beendet werden.
