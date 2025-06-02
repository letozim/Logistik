# 🚛 Logistik-Verwaltung System

Eine umfassende JavaFX-Desktop-Anwendung für die professionelle Verwaltung von Logistikunternehmen mit vollständiger Datenbank-Integration.

## 📋 Überblick

Das Logistik-Verwaltung System ist eine moderne Desktop-Anwendung, die alle Aspekte eines Logistikunternehmens abdeckt - von der Auftragsverwaltung über Stammdaten bis hin zur Warenwirtschaft. Mit einer sauberen Architektur und benutzerfreundlicher Oberfläche.

## ✨ Features

### 📋 Auftragsverwaltung
- **Auftragserstellung**: Vollständige Auftragsabwicklung mit Positionen
- **Status-Tracking**: Verfolgung von Auftragsstatus und Prioritäten
- **Positionsverwaltung**: Detaillierte Auftragsposten mit Mengen und Preisen

### 👥 Stammdatenverwaltung
- **Kundenverwaltung**: Umfassende Kundendaten mit Details
- **Fahrerverwaltung**: Fahrerstammdaten mit Verfügbarkeitsmanagement
- **Lieferantenverwaltung**: Lieferantendaten mit Bewertungssystem
- **Personenverwaltung**: Rollenbasierte Personenverwaltung

### 📦 Warenwirtschaft
- **Artikelverwaltung**: Vollständige Warenstammdaten
- **Kategorisierung**: Strukturierte Warengruppierung
- **Einheitenverwaltung**: Flexible Maßeinheiten-Verwaltung

### 🏗️ Architektur-Features
- **Repository-Pattern**: Saubere Datenzugriffschicht
- **Service-Layer**: Geschäftslogik-Abstraktion
- **Dialog-System**: Konsistente Eingabemasken
- **Modulare Views**: Eigenständige Verwaltungsmodule

## 🛠️ Technische Details

- **Sprache**: Java 20
- **GUI Framework**: JavaFX 20.0.1
- **Build-Tool**: Maven
- **Datenbank**: MySQL 8.0+
- **Architektur**: Layered Architecture (Model-Repository-Service-UI)

## 📁 Projektstruktur

```
src/main/java/
└── model/                          # Datenmodelle
    ├── Auftrag.java               # Auftragsdaten
    ├── AuftragPosition.java       # Auftragspositionen
    ├── AuftragPrioritaet.java     # Prioritäts-Enum
    ├── AuftragStatus.java         # Status-Enum
    ├── Fahrer.java                # Fahrerdaten
    ├── FahrerDetails.java         # Erweiterte Fahrerinfos
    ├── FahrerVerfuegbarkeit.java  # Verfügbarkeits-Enum
    ├── Kunde.java                 # Kundenstammdaten
    ├── KundeDetails.java          # Erweiterte Kundeninfos
    ├── Lieferant.java             # Lieferantendaten
    ├── LieferantBewertung.java    # Bewertungs-Enum
    ├── LieferantDetails.java      # Erweiterte Lieferanteninfos
    ├── Person.java                # Grundlegende Personendaten
    ├── PersonRolle.java           # Rollen-Enum
    ├── PersonTyp.java             # Typ-Enum
    ├── Ware.java                  # Warenstammdaten
    ├── WareEinheit.java           # Einheiten-Enum
    └── WareKategorie.java         # Kategorien-Enum
└── repository/                     # Datenzugriff
    ├── AuftragRepository.java
    ├── FahrerRepository.java
    ├── KundeRepository.java
    ├── LieferantRepository.java
    └── WareRepository.java
└── service/                        # Geschäftslogik
└── ui/                            # Benutzeroberfläche
    ├── AuftragDialog.java         # Auftrags-Eingabedialog
    ├── AuftragVerwaltungView.java # Auftragsverwaltung
    ├── FahrerDialog.java          # Fahrer-Eingabedialog
    ├── KundeDialog.java           # Kunden-Eingabedialog
    ├── LieferantDialog.java       # Lieferanten-Eingabedialog
    ├── StammdatenverwaltungView.java # Stammdatenverwaltung
    ├── WareDialog.java            # Waren-Eingabedialog
    └── WareVerwaltungView.java    # Warenverwaltung
└── MainApp.java                   # Hauptanwendung

database/                           # Datenbank-Backup
├── full-backup.sql                # Vollständiges MySQL-Backup
├── sample-data.sql                # Zusätzliche Beispieldaten
├── migrations/                    # Datenbankmigrationen
└── ER-Diagram.png                 # Datenbankdiagramm (optional)

src/main/resources/
├── application.properties         # MySQL-Konfiguration
└── static/                        # Statische Ressourcen
```

## 📦 Installation

### Voraussetzungen
- **Java Development Kit (JDK) 20**
- **Maven 3.6+**
- **MySQL Server 8.0+**
- **JavaFX 20.0.1**

### MySQL Installation

**macOS (mit Homebrew):**
```bash
# MySQL installieren
brew install mysql

# MySQL starten
brew services start mysql

# Root-Passwort setzen (bei Erstinstallation)
mysql_secure_installation
```

**Windows:**
1. [MySQL Installer](https://dev.mysql.com/downloads/installer/) herunterladen
2. MySQL Server + MySQL Workbench installieren
3. Root-Passwort während Installation setzen

**Linux (Ubuntu/Debian):**
```bash
# MySQL installieren
sudo apt update
sudo apt install mysql-server

# MySQL konfigurieren
sudo mysql_secure_installation

# MySQL starten
sudo systemctl start mysql
sudo systemctl enable mysql
```

### Setup

1. **Repository klonen:**
   ```bash
   git clone https://github.com/letozim/Logistik.git
   cd Logistik
   ```

2. **Dependencies installieren:**
   ```bash
   mvn clean install
   ```

3. **Datenbank einrichten:**
   
   ```bash
   # 1. MySQL-Shell öffnen
   mysql -u root -p
   
   # 2. Datenbank erstellen
   CREATE DATABASE logistik_crm;
   EXIT;
   
   # 3. Vollständiges Backup importieren (Schema + Daten)
   mysql -u root -p logistik_crm < database/full-backup.sql
   ```

4. **Anwendung starten:**
   ```bash
   mvn javafx:run
   ```

## 🚀 Quick Start Guide

**Komplette Einrichtung in 5 Minuten:**

```bash
# 1. Repository klonen
git clone https://github.com/letozim/Logistik.git
cd Logistik

# 2. MySQL Datenbank einrichten
mysql -u root -p -e "CREATE DATABASE logistik_crm;"
mysql -u root -p logistik_crm < database/full-backup.sql

# 3. Passwort in Repository-Klassen anpassen
# ⚠️ WICHTIG: Ändere in allen *Repository.java Dateien:
# private final String password = "meinDatenbank";  // <- Dein MySQL-Passwort

# 4. Anwendung kompilieren und starten
mvn clean install
mvn javafx:run
```

**🎯 Fertig! Das Logistik CRM sollte jetzt laufen.**
   
   Oder direkt über IDE:
   ```bash
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes org.example.MainApp
   ```

### 🔧 Datenbank-Konfiguration

**Aktuelle Implementierung:**
Die Datenbankverbindung ist direkt in den Repository-Klassen konfiguriert:

```java
public class AuftragRepository {
    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";  // ⚠️ Passwort anpassen!
}
```

**⚠️ Wichtiger Hinweis:**
- **Passwort anpassen**: Ändere `"meinDatenbank"` zu deinem MySQL-Passwort
- **Alle Repository-Klassen**: Gleiche Konfiguration in allen `*Repository.java` Dateien



## 🚀 Verwendung

### Anwendung starten
1. Führe `MainApp.java` aus oder nutze `mvn javafx:run`
2. Das Hauptmenü öffnet sich mit allen verfügbaren Modulen
3. Navigiere durch die verschiedenen Verwaltungsbereiche

### Module verwenden
- **📋 Auftragsverwaltung**: Aufträge erstellen, bearbeiten und verwalten
- **👥 Stammdaten**: Kunden, Fahrer und Lieferanten verwalten
- **📦 Warenverwaltung**: Artikel und Lagerbestände verwalten

## 🔧 Entwicklung

### Architektur-Prinzipien
- **Separation of Concerns**: Klare Trennung von UI, Geschäftslogik und Datenzugriff
- **Repository-Pattern**: Abstrahierte Datenzugriffschicht
- **Enum-basierte Konfiguration**: Typsichere Status- und Kategorie-Verwaltung
- **Dialog-Pattern**: Wiederverwendbare Eingabemasken

### Neue Features hinzufügen
1. **Model erweitern**: Neue Entitäten im `model`-Package
2. **Repository erstellen**: Datenzugriff im `repository`-Package
3. **Service implementieren**: Geschäftslogik im `service`-Package
4. **UI erstellen**: Dialog und View im `ui`-Package
5. **MainApp erweitern**: Neuen Menüpunkt hinzufügen

### Code-Konventionen
- Deutsche Klassennamen für fachliche Klarheit
- Enum-basierte Konfiguration für Status und Kategorien
- Konsistente Dialog-Implementierung
- Repository-Pattern für Datenzugriff

## 🗄️ Datenbank

### Datenbank-Setup
- **Name**: `logistik_crm`
- **Engine**: MySQL 8.0+
- **Charset**: UTF8MB4

### Schema-Dateien
Das komplette Datenbank-Backup ist bereits im Repository enthalten:
```
database/
├── full-backup.sql     # ✅ Vollständiges DB-Backup (Schema + Daten)

```

**📥 Backup direkt verwenden:**
Die `full-backup.sql` Datei enthält alle CREATE TABLE Statements UND bereits vorhandene Daten - perfekt für sofortigen Start!

**✅ Vorteile des Full-Backups:**
- Komplette Datenbankstruktur
- Bereits vorhandene Test-/Produktionsdaten
- Ein einziger Import-Befehl
- Sofort einsatzbereit

### Wichtige Tabellen
| Tabelle | Beschreibung |
|---------|-------------|
| `auftrag` | Hauptaufträge mit Status und Priorität |
| `auftrag_position` | Einzelpositionen der Aufträge |
| `person_new` | Grundlegende Personendaten |
| `kunde_details` | Erweiterte Kundendaten |
| `fahrer_details` | Fahrerdaten mit Verfügbarkeit |
| `lieferant_details` | Lieferantenstammdaten |
| `person_rolle` | Rollen-Zuweisungen für Personen |
| `ware` | Artikelstammdaten |
| `ware_kategorie` | Warengruppierungen |

### Datenmodell-Übersicht
- **👤 Person**: Basis für Kunden, Fahrer, Lieferanten (`person_new`)
- **📋 Auftrag**: Aufträge mit Positionen und Status (`auftrag`, `auftrag_position`)
- **📦 Ware**: Artikel mit Kategorien (`ware`, `ware_kategorie`)

### Beziehungen
- auftrag ↔ person_new (1:n)
- auftrag ↔ auftrag_position (1:n)
- auftrag_position ↔ ware (n:1)
- person_new ↔ person_rolle (n:m)

## 📋 Roadmap

### Geplante Features
- [ ] **🚚 Fahrzeugverwaltung**: Vollständiges Flottenmanagement
- [ ] **📊 Dashboard**: Kennzahlen und Übersichten
- [ ] **📈 Reporting**: Auswertungen und Berichte
- [ ] **🔒 Benutzerverwaltung**: Multi-User mit Rechtesystem
- [ ] **📱 REST-API**: Backend-API für mobile Apps
- [ ] **☁️ Cloud-Integration**: Synchronisation und Backup

### Technische Verbesserungen
- [ ] **🧪 Unit Tests**: Vollständige Testabdeckung
- [ ] **📝 Logging**: Strukturiertes Logging-System
- [ ] **⚡ Performance**: Query-Optimierung
- [ ] **🔐 Security**: Authentifizierung und Autorisierung

## 🔐 Sicherheitshinweise

### Passwort-Schutz für GitHub
Wenn du den Code auf GitHub pushst, solltest du das Datenbankpasswort entfernen:

**1. Umgebungsvariable verwenden:**
```java
private final String password = System.getenv("MYSQL_PASSWORD");
```

**2. .gitignore erweitern:**
```
# Database configuration
src/main/resources/database.properties
config/local.properties
```

**3. Beispiel-Konfiguration bereitstellen:**
```java
// DatabaseConfig.example.java
public class DatabaseConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/logistik_crm";
    public static final String USER = "root";
    public static final String PASSWORD = "YOUR_PASSWORD_HERE"; // <- Anpassen
}
```

## 🐛 Bekannte Probleme

- **Hardcoded Passwörter**: Datenbankpasswörter sind aktuell im Code - vor GitHub-Push entfernen
- **Fahrzeugmodul**: Noch nicht implementiert
- **Datenmigration**: Tools für Datenübernahme fehlen
- **Backup-System**: Automatische Backups nicht implementiert

## 🤝 Beitragen

1. **Fork** das Repository
2. **Feature-Branch** erstellen (`git checkout -b feature/NeuesFunktion`)
3. **Änderungen committen** (`git commit -m 'Neue Funktion hinzugefügt'`)
4. **Branch pushen** (`git push origin feature/NeuesFunktion`)
5. **Pull Request** erstellen

### Coding-Standards
- Verwende deutsche Bezeichnungen für fachliche Klassen
- Implementiere Repository-Pattern für Datenzugriff
- Erstelle einheitliche Dialog-Klassen
- Dokumentiere komplexe Geschäftslogik

## 📄 Lizenz

Dieses Projekt steht unter der [MIT License](LICENSE).

## 👨‍💻 Autor

**letozim**
- GitHub: [@letozim](https://github.com/letozim)


---

**© 2024 Logistik-Verwaltung System - Version 1.0-SNAPSHOT**
