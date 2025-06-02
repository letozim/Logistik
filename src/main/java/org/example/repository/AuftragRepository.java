package org.example.repository;

import org.example.model.*;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuftragRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Auftrag> findAll() {
        List<Auftrag> auftraege = new ArrayList<>();
        String sql = """
            SELECT a.auftrag_id, a.auftragsnummer, a.kunde_id, a.fahrer_id, 
                   a.lieferadresse, a.lieferdatum, a.status, a.prioritaet,
                   a.gesamtsumme, a.mwst_satz, a.bemerkungen, a.interne_notizen,
                   a.erstellt_am, a.geaendert_am,
                   pk.name as kunde_name, pf.name as fahrer_name
            FROM auftrag a
            LEFT JOIN person_new pk ON a.kunde_id = pk.person_id
            LEFT JOIN person_new pf ON a.fahrer_id = pf.person_id
            ORDER BY a.erstellt_am DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Auftrag auftrag = createAuftragFromResultSet(rs);
                auftraege.add(auftrag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Laden der Aufträge: " + e.getMessage(), e);
        }
        return auftraege;
    }

    public Auftrag findById(int id) {
        String sql = """
            SELECT a.auftrag_id, a.auftragsnummer, a.kunde_id, a.fahrer_id, 
                   a.lieferadresse, a.lieferdatum, a.status, a.prioritaet,
                   a.gesamtsumme, a.mwst_satz, a.bemerkungen, a.interne_notizen,
                   a.erstellt_am, a.geaendert_am,
                   pk.name as kunde_name, pf.name as fahrer_name
            FROM auftrag a
            LEFT JOIN person_new pk ON a.kunde_id = pk.person_id
            LEFT JOIN person_new pf ON a.fahrer_id = pf.person_id
            WHERE a.auftrag_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Auftrag auftrag = createAuftragFromResultSet(rs);
                    // Positionen laden
                    auftrag.setPositionen(findPositionenByAuftragId(id));
                    return auftrag;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AuftragPosition> findPositionenByAuftragId(int auftragId) {
        List<AuftragPosition> positionen = new ArrayList<>();
        String sql = """
            SELECT ap.position_id, ap.auftrag_id, ap.ware_id, ap.menge, 
                   ap.einzelpreis, ap.gesamtpreis, ap.bemerkung, ap.erstellt_am,
                   w.bezeichnung as ware_name, w.artikelnummer as ware_artikelnummer
            FROM auftrag_position ap
            LEFT JOIN ware w ON ap.ware_id = w.ware_id
            WHERE ap.auftrag_id = ?
            ORDER BY ap.position_id
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, auftragId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuftragPosition position = createPositionFromResultSet(rs);
                    positionen.add(position);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positionen;
    }

    public void save(Auftrag auftrag) {
        String insertAuftrag = """
            INSERT INTO auftrag (kunde_id, fahrer_id, lieferadresse, lieferdatum, 
            status, prioritaet, gesamtsumme, mwst_satz, bemerkungen, interne_notizen)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Auftrag speichern
            try (PreparedStatement pstmt = conn.prepareStatement(insertAuftrag, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, auftrag.getKundeId());

                if (auftrag.getFahrerId() > 0) {
                    pstmt.setInt(2, auftrag.getFahrerId());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }

                pstmt.setString(3, auftrag.getLieferadresse());

                if (auftrag.getLieferdatum() != null) {
                    pstmt.setDate(4, Date.valueOf(auftrag.getLieferdatum()));
                } else {
                    pstmt.setNull(4, Types.DATE);
                }

                pstmt.setString(5, auftrag.getStatus().getDbWert());
                pstmt.setString(6, auftrag.getPrioritaet().getDbWert());
                pstmt.setBigDecimal(7, auftrag.getGesamtsumme());
                pstmt.setBigDecimal(8, auftrag.getMwstSatz());
                pstmt.setString(9, auftrag.getBemerkungen());
                pstmt.setString(10, auftrag.getInterneNotizen());

                pstmt.executeUpdate();

                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int auftragId = keys.getInt(1);
                        auftrag.setAuftragId(auftragId);

                        // Positionen speichern
                        savePositionen(conn, auftragId, auftrag.getPositionen());
                    }
                }
            }

            conn.commit();
            System.out.println("DEBUG: Auftrag erfolgreich gespeichert: " + auftrag.getAuftragsnummer());

        } catch (SQLException e) {
            System.err.println("DEBUG: SQL-Fehler beim Speichern des Auftrags: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Auftrags: " + e.getMessage(), e);
        }
    }

    public void update(Auftrag auftrag) {
        String updateAuftrag = """
            UPDATE auftrag SET kunde_id = ?, fahrer_id = ?, lieferadresse = ?, lieferdatum = ?,
            status = ?, prioritaet = ?, gesamtsumme = ?, mwst_satz = ?, bemerkungen = ?, interne_notizen = ?
            WHERE auftrag_id = ?
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Auftrag aktualisieren
            try (PreparedStatement pstmt = conn.prepareStatement(updateAuftrag)) {
                pstmt.setInt(1, auftrag.getKundeId());

                if (auftrag.getFahrerId() > 0) {
                    pstmt.setInt(2, auftrag.getFahrerId());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }

                pstmt.setString(3, auftrag.getLieferadresse());

                if (auftrag.getLieferdatum() != null) {
                    pstmt.setDate(4, Date.valueOf(auftrag.getLieferdatum()));
                } else {
                    pstmt.setNull(4, Types.DATE);
                }

                pstmt.setString(5, auftrag.getStatus().getDbWert());
                pstmt.setString(6, auftrag.getPrioritaet().getDbWert());
                pstmt.setBigDecimal(7, auftrag.getGesamtsumme());
                pstmt.setBigDecimal(8, auftrag.getMwstSatz());
                pstmt.setString(9, auftrag.getBemerkungen());
                pstmt.setString(10, auftrag.getInterneNotizen());
                pstmt.setInt(11, auftrag.getAuftragId());

                pstmt.executeUpdate();

                // Alte Positionen löschen und neue speichern
                deletePositionen(conn, auftrag.getAuftragId());
                savePositionen(conn, auftrag.getAuftragId(), auftrag.getPositionen());
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren des Auftrags: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "UPDATE auftrag SET status = 'Storniert' WHERE auftrag_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen des Auftrags: " + e.getMessage(), e);
        }
    }

    // Status-spezifische Abfragen
    public List<Auftrag> findByStatus(AuftragStatus status) {
        List<Auftrag> auftraege = new ArrayList<>();
        String sql = """
            SELECT a.auftrag_id, a.auftragsnummer, a.kunde_id, a.fahrer_id, 
                   a.lieferadresse, a.lieferdatum, a.status, a.prioritaet,
                   a.gesamtsumme, a.mwst_satz, a.bemerkungen, a.interne_notizen,
                   a.erstellt_am, a.geaendert_am,
                   pk.name as kunde_name, pf.name as fahrer_name
            FROM auftrag a
            LEFT JOIN person_new pk ON a.kunde_id = pk.person_id
            LEFT JOIN person_new pf ON a.fahrer_id = pf.person_id
            WHERE a.status = ?
            ORDER BY a.erstellt_am DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.getDbWert());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Auftrag auftrag = createAuftragFromResultSet(rs);
                    auftraege.add(auftrag);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return auftraege;
    }

    // Hilfsmethoden
    private void savePositionen(Connection conn, int auftragId, List<AuftragPosition> positionen) throws SQLException {
        String insertPosition = """
            INSERT INTO auftrag_position (auftrag_id, ware_id, menge, einzelpreis, gesamtpreis, bemerkung)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(insertPosition)) {
            for (AuftragPosition position : positionen) {
                pstmt.setInt(1, auftragId);
                pstmt.setInt(2, position.getWareId());
                pstmt.setInt(3, position.getMenge());
                pstmt.setBigDecimal(4, position.getEinzelpreis());
                pstmt.setBigDecimal(5, position.getGesamtpreis());
                pstmt.setString(6, position.getBemerkung());
                pstmt.executeUpdate();
            }
        }
    }

    private void deletePositionen(Connection conn, int auftragId) throws SQLException {
        String sql = "DELETE FROM auftrag_position WHERE auftrag_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auftragId);
            pstmt.executeUpdate();
        }
    }

    private Auftrag createAuftragFromResultSet(ResultSet rs) throws SQLException {
        Auftrag auftrag = new Auftrag();
        auftrag.setAuftragId(rs.getInt("auftrag_id"));
        auftrag.setAuftragsnummer(rs.getString("auftragsnummer"));
        auftrag.setKundeId(rs.getInt("kunde_id"));
        auftrag.setKundeName(rs.getString("kunde_name"));
        auftrag.setFahrerId(rs.getInt("fahrer_id"));
        auftrag.setFahrerName(rs.getString("fahrer_name"));
        auftrag.setLieferadresse(rs.getString("lieferadresse"));

        Date lieferdatum = rs.getDate("lieferdatum");
        if (lieferdatum != null) {
            auftrag.setLieferdatum(lieferdatum.toLocalDate());
        }

        auftrag.setStatus(AuftragStatus.fromDbWert(rs.getString("status")));
        auftrag.setPrioritaet(AuftragPrioritaet.fromDbWert(rs.getString("prioritaet")));
        auftrag.setGesamtsumme(rs.getBigDecimal("gesamtsumme"));
        auftrag.setMwstSatz(rs.getBigDecimal("mwst_satz"));
        auftrag.setBemerkungen(rs.getString("bemerkungen"));
        auftrag.setInterneNotizen(rs.getString("interne_notizen"));

        Timestamp erstellt = rs.getTimestamp("erstellt_am");
        if (erstellt != null) {
            auftrag.setErstelltAm(erstellt.toLocalDateTime());
        }

        Timestamp geaendert = rs.getTimestamp("geaendert_am");
        if (geaendert != null) {
            auftrag.setGeaendertAm(geaendert.toLocalDateTime());
        }

        return auftrag;
    }

    private AuftragPosition createPositionFromResultSet(ResultSet rs) throws SQLException {
        AuftragPosition position = new AuftragPosition();
        position.setPositionId(rs.getInt("position_id"));
        position.setAuftragId(rs.getInt("auftrag_id"));
        position.setWareId(rs.getInt("ware_id"));
        position.setWareName(rs.getString("ware_name"));
        position.setWareArtikelnummer(rs.getString("ware_artikelnummer"));
        position.setMenge(rs.getInt("menge"));
        position.setEinzelpreis(rs.getBigDecimal("einzelpreis"));
        position.setGesamtpreis(rs.getBigDecimal("gesamtpreis"));
        position.setBemerkung(rs.getString("bemerkung"));

        Timestamp erstellt = rs.getTimestamp("erstellt_am");
        if (erstellt != null) {
            position.setErstelltAm(erstellt.toLocalDateTime());
        }

        return position;
    }
}