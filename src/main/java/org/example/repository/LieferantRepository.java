package org.example.repository;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LieferantRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Lieferant> findAll() {
        List<Lieferant> lieferantListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   ld.unternehmensform, ld.ustid, ld.lieferantennummer, ld.handelsregisternummer,
                   ld.bewertung, ld.zahlungskonditionen, ld.hauptkategorie
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN lieferant_details ld ON p.person_id = ld.person_id
            WHERE pr.rolle = 'Lieferant' AND pr.aktiv = TRUE AND p.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = createPersonFromResultSet(rs);
                LieferantDetails details = createLieferantDetailsFromResultSet(rs);
                person.addRolle(PersonRolle.LIEFERANT);

                Lieferant lieferant = new Lieferant(person, details);
                lieferantListe.add(lieferant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lieferantListe;
    }

    public Lieferant findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   ld.unternehmensform, ld.ustid, ld.lieferantennummer, ld.handelsregisternummer,
                   ld.bewertung, ld.zahlungskonditionen, ld.hauptkategorie
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN lieferant_details ld ON p.person_id = ld.person_id
            WHERE p.person_id = ? AND pr.rolle = 'Lieferant' AND pr.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Person person = createPersonFromResultSet(rs);
                    LieferantDetails details = createLieferantDetailsFromResultSet(rs);
                    person.addRolle(PersonRolle.LIEFERANT);

                    return new Lieferant(person, details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Lieferant lieferant) {
        String insertPerson = "INSERT INTO person_new (name, adresse, telefon, email, typ) VALUES (?, ?, ?, ?, ?)";
        String insertRolle = "INSERT INTO person_rolle (person_id, rolle) VALUES (?, ?)";
        String insertDetails = """
            INSERT INTO lieferant_details (person_id, unternehmensform, ustid, lieferantennummer,
            handelsregisternummer, bewertung, zahlungskonditionen, hauptkategorie) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, lieferant.getName());
                pstmtPerson.setString(2, lieferant.getAdresse());
                pstmtPerson.setString(3, lieferant.getTelefon());
                pstmtPerson.setString(4, lieferant.getEmail());
                pstmtPerson.setString(5, lieferant.getTyp().getDbWert());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        lieferant.setId(personId);

                        // Rolle speichern
                        try (PreparedStatement pstmtRolle = conn.prepareStatement(insertRolle)) {
                            pstmtRolle.setInt(1, personId);
                            pstmtRolle.setString(2, PersonRolle.LIEFERANT.getDbWert());
                            pstmtRolle.executeUpdate();
                        }

                        // Lieferant-Details speichern
                        try (PreparedStatement pstmtDetails = conn.prepareStatement(insertDetails)) {
                            pstmtDetails.setInt(1, personId);
                            pstmtDetails.setString(2, lieferant.getUnternehmensform());
                            pstmtDetails.setString(3, lieferant.getUstid());
                            pstmtDetails.setString(4, lieferant.getLieferantennummer());
                            pstmtDetails.setString(5, lieferant.getHandelsregisternummer());
                            pstmtDetails.setString(6, lieferant.getBewertung().getDbWert());
                            pstmtDetails.setString(7, lieferant.getZahlungskonditionen());
                            pstmtDetails.setString(8, lieferant.getHauptkategorie());
                            pstmtDetails.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("DEBUG: Lieferant erfolgreich gespeichert");

        } catch (SQLException e) {
            System.err.println("DEBUG: SQL-Fehler beim Speichern des Lieferanten: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Lieferanten: " + e.getMessage(), e);
        }
    }

    public void update(Lieferant lieferant) {
        String updatePerson = "UPDATE person_new SET name = ?, adresse = ?, telefon = ?, email = ?, typ = ? WHERE person_id = ?";
        String updateDetails = """
            UPDATE lieferant_details SET unternehmensform = ?, ustid = ?, lieferantennummer = ?,
            handelsregisternummer = ?, bewertung = ?, zahlungskonditionen = ?, hauptkategorie = ? 
            WHERE person_id = ?
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, lieferant.getName());
                pstmtPerson.setString(2, lieferant.getAdresse());
                pstmtPerson.setString(3, lieferant.getTelefon());
                pstmtPerson.setString(4, lieferant.getEmail());
                pstmtPerson.setString(5, lieferant.getTyp().getDbWert());
                pstmtPerson.setInt(6, lieferant.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtDetails = conn.prepareStatement(updateDetails)) {
                pstmtDetails.setString(1, lieferant.getUnternehmensform());
                pstmtDetails.setString(2, lieferant.getUstid());
                pstmtDetails.setString(3, lieferant.getLieferantennummer());
                pstmtDetails.setString(4, lieferant.getHandelsregisternummer());
                pstmtDetails.setString(5, lieferant.getBewertung().getDbWert());
                pstmtDetails.setString(6, lieferant.getZahlungskonditionen());
                pstmtDetails.setString(7, lieferant.getHauptkategorie());
                pstmtDetails.setInt(8, lieferant.getId());
                pstmtDetails.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren des Lieferanten: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String deleteDetails = "DELETE FROM lieferant_details WHERE person_id = ?";
        String deleteRolle = "DELETE FROM person_rolle WHERE person_id = ? AND rolle = 'Lieferant'";
        String deletePerson = "DELETE FROM person_new WHERE person_id = ? AND NOT EXISTS (SELECT 1 FROM person_rolle WHERE person_id = ? AND aktiv = TRUE)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDetails = conn.prepareStatement(deleteDetails)) {
                pstmtDetails.setInt(1, id);
                pstmtDetails.executeUpdate();
            }

            try (PreparedStatement pstmtRolle = conn.prepareStatement(deleteRolle)) {
                pstmtRolle.setInt(1, id);
                pstmtRolle.executeUpdate();
            }

            // Person nur löschen wenn keine anderen aktiven Rollen
            try (PreparedStatement pstmtPerson = conn.prepareStatement(deletePerson)) {
                pstmtPerson.setInt(1, id);
                pstmtPerson.setInt(2, id);
                pstmtPerson.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen des Lieferanten: " + e.getMessage(), e);
        }
    }

    private Person createPersonFromResultSet(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setId(rs.getInt("person_id"));
        person.setName(rs.getString("name"));
        person.setAdresse(rs.getString("adresse"));
        person.setTelefon(rs.getString("telefon"));
        person.setEmail(rs.getString("email"));

        String typString = rs.getString("typ");
        if (typString != null) {
            person.setTyp(PersonTyp.fromDbWert(typString));
        }

        Timestamp timestamp = rs.getTimestamp("erstellt_am");
        if (timestamp != null) {
            person.setErstelltAm(timestamp.toLocalDateTime());
        }

        person.setAktiv(rs.getBoolean("aktiv"));
        return person;
    }

    private LieferantDetails createLieferantDetailsFromResultSet(ResultSet rs) throws SQLException {
        LieferantDetails details = new LieferantDetails();
        details.setPersonId(rs.getInt("person_id"));
        details.setUnternehmensform(rs.getString("unternehmensform"));
        details.setUstid(rs.getString("ustid"));
        details.setLieferantennummer(rs.getString("lieferantennummer"));
        details.setHandelsregisternummer(rs.getString("handelsregisternummer"));
        details.setZahlungskonditionen(rs.getString("zahlungskonditionen"));
        details.setHauptkategorie(rs.getString("hauptkategorie"));

        String bewertungString = rs.getString("bewertung");
        if (bewertungString != null) {
            details.setBewertung(LieferantBewertung.fromDbWert(bewertungString));
        }

        return details;
    }
}
