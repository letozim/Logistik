package org.example.repository;

import org.example.model.Lieferant;

import java.sql.*;
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
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, l.unternehmensform
            FROM lieferant l
            JOIN person p ON l.lieferanten_id = p.person_id
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Lieferant lieferant = new Lieferant(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("adresse"),
                        rs.getString("telefon"),
                        rs.getString("email"),
                        rs.getString("unternehmensform")
                );
                lieferantListe.add(lieferant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lieferantListe;
    }

    public Lieferant findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, l.unternehmensform
            FROM lieferant l
            JOIN person p ON l.lieferanten_id = p.person_id
            WHERE l.lieferanten_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Lieferant(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("adresse"),
                            rs.getString("telefon"),
                            rs.getString("email"),
                            rs.getString("unternehmensform")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Lieferant lieferant) {
        String insertPerson = "INSERT INTO person (name, adresse, telefon, email) VALUES (?, ?, ?, ?)";
        String insertLieferant = "INSERT INTO lieferant (lieferanten_id, unternehmensform) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, lieferant.getName());
                pstmtPerson.setString(2, lieferant.getAdresse());
                pstmtPerson.setString(3, lieferant.getTelefon());
                pstmtPerson.setString(4, lieferant.getEmail());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        lieferant.setId(personId);

                        // Lieferant speichern
                        try (PreparedStatement pstmtLieferant = conn.prepareStatement(insertLieferant)) {
                            pstmtLieferant.setInt(1, personId);
                            pstmtLieferant.setString(2, lieferant.getUnternehmensform());
                            pstmtLieferant.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Lieferant lieferant) {
        String updatePerson = "UPDATE person SET name = ?, adresse = ?, telefon = ?, email = ? WHERE person_id = ?";
        String updateLieferant = "UPDATE lieferant SET unternehmensform = ? WHERE lieferanten_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, lieferant.getName());
                pstmtPerson.setString(2, lieferant.getAdresse());
                pstmtPerson.setString(3, lieferant.getTelefon());
                pstmtPerson.setString(4, lieferant.getEmail());
                pstmtPerson.setInt(5, lieferant.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtLieferant = conn.prepareStatement(updateLieferant)) {
                pstmtLieferant.setString(1, lieferant.getUnternehmensform());
                pstmtLieferant.setInt(2, lieferant.getId());
                pstmtLieferant.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String deleteLieferant = "DELETE FROM lieferant WHERE lieferanten_id = ?";
        String deletePerson = "DELETE FROM person WHERE person_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtLieferant = conn.prepareStatement(deleteLieferant)) {
                pstmtLieferant.setInt(1, id);
                pstmtLieferant.executeUpdate();
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
