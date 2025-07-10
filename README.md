# Restaurant-Reservierungssystem

Kleine Swing-Anwendung zum Verwalten von Tischreservierungen. Daten werden in
`db/restaurant.db` per SQLite gespeichert und bei Bedarf mit Beispieldaten
befüllt. Entwickelt mit JDK&nbsp;17 und Maven.

## Build

```bash
mvn clean package
```

Die ausführbare JAR liegt danach unter `target/`.

## Starten

```bash
mvn exec:java -Dexec.mainClass=com.restaurant.reservation.ui.MainApp
```

Alternativ kann die gebaute JAR mit `java -jar` ausgeführt werden.
