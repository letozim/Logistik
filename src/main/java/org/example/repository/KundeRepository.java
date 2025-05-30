package org.example.repository;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KundeRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Kunde> findAll() {
        List<Kunde> kundenListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   kd.betreuender_mitarbeiter, kd.kundennummer, kd.zahlungsziel, 
                   kd.kreditlimit, kd.rabatt_prozent
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN kunde_details kd ON p.person_id = kd.person_id
            WHERE pr.rolle = 'Kunde' AND pr.aktiv = TRUE AND p.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = createPersonFromResultSet(rs);
                KundeDetails details = createKundeDetailsFromResultSet(rs);
                person.addRolle(PersonRolle.KUNDE);

                Kunde kunde = new Kunde(person, details);
                kundenListe.add(kunde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kundenListe;
    }

    public Kunde findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   kd.betreuender_mitarbeiter, kd.kundennummer, kd.zahlungsziel,
                   kd.kreditlimit, kd.rabatt_prozent
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN kunde_details kd ON p.person_id = kd.person_id
            WHERE p.person_id = ? AND pr.rolle = 'Kunde' AND pr.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Person person = createPersonFromResultSet(rs);
                    KundeDetails details = createKundeDetailsFromResultSet(rs);
                    person.addRolle(PersonRolle.KUNDE);

                    return new Kunde(person, details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Kunde kunde) {
        String insertPerson = "INSERT INTO person_new (name, adresse, telefon, email, typ) VALUES (?, ?, ?, ?, ?)";
        String insertRolle = "INSERT INTO person_rolle (person_id, rolle) VALUES (?, ?)";
        String insertDetails = "INSERT INTO kunde_details (person_id, betreuender_mitarbeiter, kundennummer, zahlungsziel) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, kunde.getName());
                pstmtPerson.setString(2, kunde.getAdresse());
                pstmtPerson.setString(3, kunde.getTelefon());
                pstmtPerson.setString(4, kunde.getEmail());
                pstmtPerson.setString(5, kunde.getTyp().name());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        kunde.setId(personId);

                        // Rolle speichern
                        try (PreparedStatement pstmtRolle = conn.prepareStatement(insertRolle)) {
                            pstmtRolle.setInt(1, personId);
                            pstmtRolle.setString(2, PersonRolle.KUNDE.name());
                            pstmtRolle.executeUpdate();
                        }

                        // Kunde-Details speichern
                        try (PreparedStatement pstmtDetails = conn.prepareStatement(insertDetails)) {
                            pstmtDetails.setInt(1, personId);
                            pstmtDetails.setString(2, kunde.getBetreuenderMitarbeiter());
                            pstmtDetails.setString(3, kunde.getKundennummer());
                            pstmtDetails.setInt(4, kunde.getZahlungsziel());
                            pstmtDetails.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Kunde kunde) {
        String updatePerson = "UPDATE person_new SET name = ?, adresse = ?, telefon = ?, email = ?, typ = ? WHERE person_id = ?";
        String updateDetails = "UPDATE kunde_details SET betreuender_mitarbeiter = ?, kundennummer = ?, zahlungsziel = ? WHERE person_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, kunde.getName());
                pstmtPerson.setString(2, kunde.getAdresse());
                pstmtPerson.setString(3, kunde.getTelefon());
                pstmtPerson.setString(4, kunde.getEmail());
                pstmtPerson.setString(5, kunde.getTyp().name());
                pstmtPerson.setInt(6, kunde.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtDetails = conn.prepareStatement(updateDetails)) {
                pstmtDetails.setString(1, kunde.getBetreuenderMitarbeiter());
                pstmtDetails.setString(2, kunde.getKundennummer());
                pstmtDetails.setInt(3, kunde.getZahlungsziel());
                pstmtDetails.setInt(4, kunde.getId());
                pstmtDetails.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String deleteDetails = "DELETE FROM kunde_details WHERE person_id = ?";
        String deleteRolle = "DELETE FROM person_rolle WHERE person_id = ? AND rolle = 'Kunde'";
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
            person.setTyp(PersonTyp.valueOf(typString.toUpperCase()));
        }

        Timestamp timestamp = rs.getTimestamp("erstellt_am");
        if (timestamp != null) {
            person.setErstelltAm(timestamp.toLocalDateTime());
        }

        person.setAktiv(rs.getBoolean("aktiv"));
        return person;
    }

    private KundeDetails createKundeDetailsFromResultSet(ResultSet rs) throws SQLException {
        KundeDetails details = new KundeDetails();
        details.setPersonId(rs.getInt("person_id"));
        details.setBetreuenderMitarbeiter(rs.getString("betreuender_mitarbeiter"));
        details.setKundennummer(rs.getString("kundennummer"));
        details.setZahlungsziel(rs.getInt("zahlungsziel"));

        if (rs.getBigDecimal("kreditlimit") != null) {
            details.setKreditlimit(rs.getBigDecimal("kreditlimit"));
        }
        if (rs.getBigDecimal("rabatt_prozent") != null) {
            details.setRabattProzent(rs.getBigDecimal("rabatt_prozent"));
        }

        return details;
    }
}