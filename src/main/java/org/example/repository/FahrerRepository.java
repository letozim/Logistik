package org.example.repository;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FahrerRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Fahrer> findAll() {
        List<Fahrer> fahrerListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   fd.fuehrerscheinklasse, fd.fahrzeugtyp, fd.fuehrerschein_nummer,
                   fd.fuehrerschein_ausgestellt_am, fd.fuehrerschein_ablauf_am,
                   fd.medizinische_untersuchung_ablauf, fd.verfuegbarkeit
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN fahrer_details fd ON p.person_id = fd.person_id
            WHERE pr.rolle = 'Fahrer' AND pr.aktiv = TRUE AND p.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = createPersonFromResultSet(rs);
                FahrerDetails details = createFahrerDetailsFromResultSet(rs);
                person.addRolle(PersonRolle.FAHRER);

                Fahrer fahrer = new Fahrer(person, details);
                fahrerListe.add(fahrer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fahrerListe;
    }

    public Fahrer findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   fd.fuehrerscheinklasse, fd.fahrzeugtyp, fd.fuehrerschein_nummer,
                   fd.fuehrerschein_ausgestellt_am, fd.fuehrerschein_ablauf_am,
                   fd.medizinische_untersuchung_ablauf, fd.verfuegbarkeit
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN fahrer_details fd ON p.person_id = fd.person_id
            WHERE p.person_id = ? AND pr.rolle = 'Fahrer' AND pr.aktiv = TRUE
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Person person = createPersonFromResultSet(rs);
                    FahrerDetails details = createFahrerDetailsFromResultSet(rs);
                    person.addRolle(PersonRolle.FAHRER);

                    return new Fahrer(person, details);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fahrer> findVerfuegbareFahrer() {
        List<Fahrer> fahrerListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, p.typ, p.erstellt_am, p.aktiv,
                   fd.fuehrerscheinklasse, fd.fahrzeugtyp, fd.fuehrerschein_nummer,
                   fd.fuehrerschein_ausgestellt_am, fd.fuehrerschein_ablauf_am,
                   fd.medizinische_untersuchung_ablauf, fd.verfuegbarkeit
            FROM person_new p
            JOIN person_rolle pr ON p.person_id = pr.person_id
            LEFT JOIN fahrer_details fd ON p.person_id = fd.person_id
            WHERE pr.rolle = 'Fahrer' AND pr.aktiv = TRUE AND p.aktiv = TRUE 
            AND fd.verfuegbarkeit = 'Verfuegbar'
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = createPersonFromResultSet(rs);
                FahrerDetails details = createFahrerDetailsFromResultSet(rs);
                person.addRolle(PersonRolle.FAHRER);

                Fahrer fahrer = new Fahrer(person, details);
                fahrerListe.add(fahrer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fahrerListe;
    }

    public void save(Fahrer fahrer) {
        String insertPerson = "INSERT INTO person_new (name, adresse, telefon, email, typ) VALUES (?, ?, ?, ?, ?)";
        String insertRolle = "INSERT INTO person_rolle (person_id, rolle) VALUES (?, ?)";
        String insertDetails = """
            INSERT INTO fahrer_details (person_id, fuehrerscheinklasse, fahrzeugtyp, fuehrerschein_nummer,
            fuehrerschein_ausgestellt_am, fuehrerschein_ablauf_am, medizinische_untersuchung_ablauf, verfuegbarkeit) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, fahrer.getName());
                pstmtPerson.setString(2, fahrer.getAdresse());
                pstmtPerson.setString(3, fahrer.getTelefon());
                pstmtPerson.setString(4, fahrer.getEmail());
                pstmtPerson.setString(5, fahrer.getTyp().getDbWert());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        fahrer.setId(personId);

                        // Rolle speichern
                        try (PreparedStatement pstmtRolle = conn.prepareStatement(insertRolle)) {
                            pstmtRolle.setInt(1, personId);
                            pstmtRolle.setString(2, PersonRolle.FAHRER.getDbWert());
                            pstmtRolle.executeUpdate();
                        }

                        // Fahrer-Details speichern
                        try (PreparedStatement pstmtDetails = conn.prepareStatement(insertDetails)) {
                            pstmtDetails.setInt(1, personId);
                            pstmtDetails.setString(2, fahrer.getFuehrerscheinklasse());
                            pstmtDetails.setString(3, fahrer.getFahrzeugtyp());
                            pstmtDetails.setString(4, fahrer.getFuehrerscheinNummer());

                            if (fahrer.getFuehrerscheinAusgestelltAm() != null) {
                                pstmtDetails.setDate(5, Date.valueOf(fahrer.getFuehrerscheinAusgestelltAm()));
                            } else {
                                pstmtDetails.setNull(5, Types.DATE);
                            }

                            if (fahrer.getFuehrerscheinAblaufAm() != null) {
                                pstmtDetails.setDate(6, Date.valueOf(fahrer.getFuehrerscheinAblaufAm()));
                            } else {
                                pstmtDetails.setNull(6, Types.DATE);
                            }

                            if (fahrer.getMedizinischeUntersuchungAblauf() != null) {
                                pstmtDetails.setDate(7, Date.valueOf(fahrer.getMedizinischeUntersuchungAblauf()));
                            } else {
                                pstmtDetails.setNull(7, Types.DATE);
                            }

                            pstmtDetails.setString(8, fahrer.getVerfuegbarkeit().getDbWert());
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

    public void update(Fahrer fahrer) {
        String updatePerson = "UPDATE person_new SET name = ?, adresse = ?, telefon = ?, email = ?, typ = ? WHERE person_id = ?";
        String updateDetails = """
            UPDATE fahrer_details SET fuehrerscheinklasse = ?, fahrzeugtyp = ?, fuehrerschein_nummer = ?,
            fuehrerschein_ausgestellt_am = ?, fuehrerschein_ablauf_am = ?, medizinische_untersuchung_ablauf = ?, 
            verfuegbarkeit = ? WHERE person_id = ?
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, fahrer.getName());
                pstmtPerson.setString(2, fahrer.getAdresse());
                pstmtPerson.setString(3, fahrer.getTelefon());
                pstmtPerson.setString(4, fahrer.getEmail());
                pstmtPerson.setString(5, fahrer.getTyp().getDbWert());
                pstmtPerson.setInt(6, fahrer.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtDetails = conn.prepareStatement(updateDetails)) {
                pstmtDetails.setString(1, fahrer.getFuehrerscheinklasse());
                pstmtDetails.setString(2, fahrer.getFahrzeugtyp());
                pstmtDetails.setString(3, fahrer.getFuehrerscheinNummer());

                if (fahrer.getFuehrerscheinAusgestelltAm() != null) {
                    pstmtDetails.setDate(4, Date.valueOf(fahrer.getFuehrerscheinAusgestelltAm()));
                } else {
                    pstmtDetails.setNull(4, Types.DATE);
                }

                if (fahrer.getFuehrerscheinAblaufAm() != null) {
                    pstmtDetails.setDate(5, Date.valueOf(fahrer.getFuehrerscheinAblaufAm()));
                } else {
                    pstmtDetails.setNull(5, Types.DATE);
                }

                if (fahrer.getMedizinischeUntersuchungAblauf() != null) {
                    pstmtDetails.setDate(6, Date.valueOf(fahrer.getMedizinischeUntersuchungAblauf()));
                } else {
                    pstmtDetails.setNull(6, Types.DATE);
                }

                pstmtDetails.setString(7, fahrer.getVerfuegbarkeit().getDbWert());
                pstmtDetails.setInt(8, fahrer.getId());
                pstmtDetails.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String deleteDetails = "DELETE FROM fahrer_details WHERE person_id = ?";
        String deleteRolle = "DELETE FROM person_rolle WHERE person_id = ? AND rolle = 'Fahrer'";
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
            person.setTyp(PersonTyp.fromDbWert(typString));
        }

        Timestamp timestamp = rs.getTimestamp("erstellt_am");
        if (timestamp != null) {
            person.setErstelltAm(timestamp.toLocalDateTime());
        }

        person.setAktiv(rs.getBoolean("aktiv"));
        return person;
    }

    private FahrerDetails createFahrerDetailsFromResultSet(ResultSet rs) throws SQLException {
        FahrerDetails details = new FahrerDetails();
        details.setPersonId(rs.getInt("person_id"));
        details.setFuehrerscheinklasse(rs.getString("fuehrerscheinklasse"));
        details.setFahrzeugtyp(rs.getString("fahrzeugtyp"));
        details.setFuehrerscheinNummer(rs.getString("fuehrerschein_nummer"));

        Date ausgestellt = rs.getDate("fuehrerschein_ausgestellt_am");
        if (ausgestellt != null) {
            details.setFuehrerscheinAusgestelltAm(ausgestellt.toLocalDate());
        }

        Date ablauf = rs.getDate("fuehrerschein_ablauf_am");
        if (ablauf != null) {
            details.setFuehrerscheinAblaufAm(ablauf.toLocalDate());
        }

        Date medAblauf = rs.getDate("medizinische_untersuchung_ablauf");
        if (medAblauf != null) {
            details.setMedizinischeUntersuchungAblauf(medAblauf.toLocalDate());
        }

        String verfuegbarkeitString = rs.getString("verfuegbarkeit");
        if (verfuegbarkeitString != null) {
            details.setVerfuegbarkeit(FahrerVerfuegbarkeit.fromDbWert(verfuegbarkeitString));
        }

        return details;
    }
}