package org.example.repository;

import org.example.model.Kunde;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KundeRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_db";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Kunde> findAll() {
        List<Kunde> kundenListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, k.betreuender_mitarbeiter
            FROM kunde k
            JOIN person p ON k.kunden_id = p.person_id
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kunde kunde = new Kunde(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("adresse"),
                        rs.getString("telefon"),
                        rs.getString("email"),
                        rs.getString("betreuender_mitarbeiter")
                );
                kundenListe.add(kunde);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kundenListe;
    }

    public Kunde findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, k.betreuender_mitarbeiter
            FROM kunde k
            JOIN person p ON k.kunden_id = p.person_id
            WHERE k.kunden_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Kunde(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("adresse"),
                            rs.getString("telefon"),
                            rs.getString("email"),
                            rs.getString("betreuender_mitarbeiter")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Kunde kunde) {
        String insertPerson = "INSERT INTO person (name, adresse, telefon, email) VALUES (?, ?, ?, ?)";
        String insertKunde = "INSERT INTO kunde (kunden_id, betreuender_mitarbeiter) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, kunde.getName());
                pstmtPerson.setString(2, kunde.getAdresse());
                pstmtPerson.setString(3, kunde.getTelefon());
                pstmtPerson.setString(4, kunde.getEmail());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        kunde.setId(personId);

                        // Kunde speichern
                        try (PreparedStatement pstmtKunde = conn.prepareStatement(insertKunde)) {
                            pstmtKunde.setInt(1, personId);
                            pstmtKunde.setString(2, kunde.getBetreuenderMitarbeiter());
                            pstmtKunde.executeUpdate();
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
        String updatePerson = "UPDATE person SET name = ?, adresse = ?, telefon = ?, email = ? WHERE person_id = ?";
        String updateKunde = "UPDATE kunde SET betreuender_mitarbeiter = ? WHERE kunden_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, kunde.getName());
                pstmtPerson.setString(2, kunde.getAdresse());
                pstmtPerson.setString(3, kunde.getTelefon());
                pstmtPerson.setString(4, kunde.getEmail());
                pstmtPerson.setInt(5, kunde.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtKunde = conn.prepareStatement(updateKunde)) {
                pstmtKunde.setString(1, kunde.getBetreuenderMitarbeiter());
                pstmtKunde.setInt(2, kunde.getId());
                pstmtKunde.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String deleteKunde = "DELETE FROM kunde WHERE kunden_id = ?";
        String deletePerson = "DELETE FROM person WHERE person_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtKunde = conn.prepareStatement(deleteKunde)) {
                pstmtKunde.setInt(1, id);
                pstmtKunde.executeUpdate();
            }

            try (PreparedStatement pstmtPerson = conn.prepareStatement(deletePerson)) {
                pstmtPerson.setInt(1, id);
                pstmtPerson.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
