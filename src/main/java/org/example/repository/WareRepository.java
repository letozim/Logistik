package org.example.repository;

import org.example.model.Ware;
import org.example.model.WareKategorie;
import org.example.model.WareEinheit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WareRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Kategorien verwalten
    public List<WareKategorie> findAllKategorien() {
        List<WareKategorie> kategorien = new ArrayList<>();
        String sql = "SELECT kategorie_id, kategorie_name, beschreibung, aktiv, erstellt_am FROM ware_kategorie WHERE aktiv = TRUE ORDER BY kategorie_name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WareKategorie kategorie = new WareKategorie();
                kategorie.setKategorieId(rs.getInt("kategorie_id"));
                kategorie.setKategorieName(rs.getString("kategorie_name"));
                kategorie.setBeschreibung(rs.getString("beschreibung"));
                kategorie.setAktiv(rs.getBoolean("aktiv"));

                Timestamp timestamp = rs.getTimestamp("erstellt_am");
                if (timestamp != null) {
                    kategorie.setErstelltAm(timestamp.toLocalDateTime());
                }

                kategorien.add(kategorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategorien;
    }

    // Waren verwalten
    public List<Ware> findAll() {
        List<Ware> warenListe = new ArrayList<>();
        String sql = """
            SELECT w.ware_id, w.artikelnummer, w.bezeichnung, w.beschreibung, w.einheit,
                   w.einkaufspreis, w.verkaufspreis, w.mindestbestand, w.aktueller_bestand,
                   w.lieferant_id, w.lagerort, w.lieferzeit_tage, w.aktiv, w.erstellt_am, w.geaendert_am,
                   wk.kategorie_id, wk.kategorie_name, wk.beschreibung as kategorie_beschreibung,
                   p.name as lieferant_name
            FROM ware w
            LEFT JOIN ware_kategorie wk ON w.kategorie_id = wk.kategorie_id
            LEFT JOIN person_new p ON w.lieferant_id = p.person_id
            WHERE w.aktiv = TRUE
            ORDER BY w.bezeichnung
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ware ware = createWareFromResultSet(rs);
                warenListe.add(ware);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warenListe;
    }

    public List<Ware> findUnterMindestbestand() {
        List<Ware> warenListe = new ArrayList<>();
        String sql = """
            SELECT w.ware_id, w.artikelnummer, w.bezeichnung, w.beschreibung, w.einheit,
                   w.einkaufspreis, w.verkaufspreis, w.mindestbestand, w.aktueller_bestand,
                   w.lieferant_id, w.lagerort, w.lieferzeit_tage, w.aktiv, w.erstellt_am, w.geaendert_am,
                   wk.kategorie_id, wk.kategorie_name, wk.beschreibung as kategorie_beschreibung,
                   p.name as lieferant_name
            FROM ware w
            LEFT JOIN ware_kategorie wk ON w.kategorie_id = wk.kategorie_id
            LEFT JOIN person_new p ON w.lieferant_id = p.person_id
            WHERE w.aktiv = TRUE AND w.aktueller_bestand < w.mindestbestand
            ORDER BY (w.mindestbestand - w.aktueller_bestand) DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ware ware = createWareFromResultSet(rs);
                warenListe.add(ware);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warenListe;
    }

    public Ware findById(int id) {
        String sql = """
            SELECT w.ware_id, w.artikelnummer, w.bezeichnung, w.beschreibung, w.einheit,
                   w.einkaufspreis, w.verkaufspreis, w.mindestbestand, w.aktueller_bestand,
                   w.lieferant_id, w.lagerort, w.lieferzeit_tage, w.aktiv, w.erstellt_am, w.geaendert_am,
                   wk.kategorie_id, wk.kategorie_name, wk.beschreibung as kategorie_beschreibung,
                   p.name as lieferant_name
            FROM ware w
            LEFT JOIN ware_kategorie wk ON w.kategorie_id = wk.kategorie_id
            LEFT JOIN person_new p ON w.lieferant_id = p.person_id
            WHERE w.ware_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createWareFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Ware ware) {
        String sql = """
            INSERT INTO ware (artikelnummer, bezeichnung, beschreibung, kategorie_id, einheit,
            einkaufspreis, verkaufspreis, mindestbestand, aktueller_bestand, lieferant_id,
            lagerort, lieferzeit_tage, aktiv) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ware.getArtikelnummer());
            pstmt.setString(2, ware.getBezeichnung());
            pstmt.setString(3, ware.getBeschreibung());

            if (ware.getKategorie() != null) {
                pstmt.setInt(4, ware.getKategorie().getKategorieId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, ware.getEinheit().getDbWert());
            pstmt.setBigDecimal(6, ware.getEinkaufspreis());
            pstmt.setBigDecimal(7, ware.getVerkaufspreis());
            pstmt.setInt(8, ware.getMindestbestand());
            pstmt.setInt(9, ware.getAktuellerBestand());

            if (ware.getLieferantId() > 0) {
                pstmt.setInt(10, ware.getLieferantId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setString(11, ware.getLagerort());
            pstmt.setInt(12, ware.getLieferzeitTage());
            pstmt.setBoolean(13, ware.isAktiv());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    ware.setWareId(keys.getInt(1));
                }
            }

            System.out.println("DEBUG: Ware erfolgreich gespeichert: " + ware.getBezeichnung());

        } catch (SQLException e) {
            System.err.println("DEBUG: SQL-Fehler beim Speichern der Ware: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern der Ware: " + e.getMessage(), e);
        }
    }

    public void update(Ware ware) {
        String sql = """
            UPDATE ware SET artikelnummer = ?, bezeichnung = ?, beschreibung = ?, kategorie_id = ?,
            einheit = ?, einkaufspreis = ?, verkaufspreis = ?, mindestbestand = ?, aktueller_bestand = ?,
            lieferant_id = ?, lagerort = ?, lieferzeit_tage = ?, aktiv = ?, geaendert_am = CURRENT_TIMESTAMP
            WHERE ware_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ware.getArtikelnummer());
            pstmt.setString(2, ware.getBezeichnung());
            pstmt.setString(3, ware.getBeschreibung());

            if (ware.getKategorie() != null) {
                pstmt.setInt(4, ware.getKategorie().getKategorieId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, ware.getEinheit().getDbWert());
            pstmt.setBigDecimal(6, ware.getEinkaufspreis());
            pstmt.setBigDecimal(7, ware.getVerkaufspreis());
            pstmt.setInt(8, ware.getMindestbestand());
            pstmt.setInt(9, ware.getAktuellerBestand());

            if (ware.getLieferantId() > 0) {
                pstmt.setInt(10, ware.getLieferantId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setString(11, ware.getLagerort());
            pstmt.setInt(12, ware.getLieferzeitTage());
            pstmt.setBoolean(13, ware.isAktiv());
            pstmt.setInt(14, ware.getWareId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren der Ware: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "UPDATE ware SET aktiv = FALSE WHERE ware_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen der Ware: " + e.getMessage(), e);
        }
    }

    // Bestand aktualisieren
    public void updateBestand(int wareId, int neuerBestand) {
        String sql = "UPDATE ware SET aktueller_bestand = ?, geaendert_am = CURRENT_TIMESTAMP WHERE ware_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, neuerBestand);
            pstmt.setInt(2, wareId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren des Bestands: " + e.getMessage(), e);
        }
    }

    private Ware createWareFromResultSet(ResultSet rs) throws SQLException {
        Ware ware = new Ware();
        ware.setWareId(rs.getInt("ware_id"));
        ware.setArtikelnummer(rs.getString("artikelnummer"));
        ware.setBezeichnung(rs.getString("bezeichnung"));
        ware.setBeschreibung(rs.getString("beschreibung"));
        ware.setEinheit(WareEinheit.fromDbWert(rs.getString("einheit")));
        ware.setEinkaufspreis(rs.getBigDecimal("einkaufspreis"));
        ware.setVerkaufspreis(rs.getBigDecimal("verkaufspreis"));
        ware.setMindestbestand(rs.getInt("mindestbestand"));
        ware.setAktuellerBestand(rs.getInt("aktueller_bestand"));
        ware.setLieferantId(rs.getInt("lieferant_id"));
        ware.setLieferantName(rs.getString("lieferant_name"));
        ware.setLagerort(rs.getString("lagerort"));
        ware.setLieferzeitTage(rs.getInt("lieferzeit_tage"));
        ware.setAktiv(rs.getBoolean("aktiv"));

        // Kategorie
        int kategorieId = rs.getInt("kategorie_id");
        if (!rs.wasNull()) {
            WareKategorie kategorie = new WareKategorie();
            kategorie.setKategorieId(kategorieId);
            kategorie.setKategorieName(rs.getString("kategorie_name"));
            kategorie.setBeschreibung(rs.getString("kategorie_beschreibung"));
            ware.setKategorie(kategorie);
        }

        // Timestamps
        Timestamp erstellt = rs.getTimestamp("erstellt_am");
        if (erstellt != null) {
            ware.setErstelltAm(erstellt.toLocalDateTime());
        }

        Timestamp geaendert = rs.getTimestamp("geaendert_am");
        if (geaendert != null) {
            ware.setGeaendertAm(geaendert.toLocalDateTime());
        }

        return ware;
    }
}