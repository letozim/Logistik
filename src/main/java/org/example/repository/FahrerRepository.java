package org.example.repository;

import org.example.model.Fahrer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FahrerRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_db";
    private final String user = "root";
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Fahrer> findAll() {
        List<Fahrer> fahrerListe = new ArrayList<>();
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email, 
                   f.fuehrerscheinklasse, f.fahrzeugtyp
            FROM fahrer f
            JOIN person p ON f.fahrer_id = p.person_id
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fahrer fahrer = new Fahrer(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("adresse"),
                        rs.getString("telefon"),
                        rs.getString("email"),
                        rs.getString("fuehrerscheinklasse"),
                        rs.getString("fahrzeugtyp")
                );
                fahrerListe.add(fahrer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fahrerListe;
    }

    public Fahrer findById(int id) {
        String sql = """
            SELECT p.person_id, p.name, p.adresse, p.telefon, p.email,
                   f.fuehrerscheinklasse, f.fahrzeugtyp
            FROM fahrer f
            JOIN person p ON f.fahrer_id = p.person_id
            WHERE f.fahrer_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Fahrer(
                            rs.getInt("person_id"),
                            rs.getString("name"),
                            rs.getString("adresse"),
                            rs.getString("telefon"),
                            rs.getString("email"),
                            rs.getString("fuehrerscheinklasse"),
                            rs.getString("fahrzeugtyp")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Fahrer fahrer) {
        String insertPerson = "INSERT INTO person (name, adresse, telefon, email) VALUES (?, ?, ?, ?)";
        String insertFahrer = "INSERT INTO fahrer (fahrer_id, fuehrerscheinklasse, fahrzeugtyp) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Person speichern
            try (PreparedStatement pstmtPerson = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPerson.setString(1, fahrer.getName());
                pstmtPerson.setString(2, fahrer.getAdresse());
                pstmtPerson.setString(3, fahrer.getTelefon());
                pstmtPerson.setString(4, fahrer.getEmail());
                pstmtPerson.executeUpdate();

                try (ResultSet keys = pstmtPerson.getGeneratedKeys()) {
                    if (keys.next()) {
                        int personId = keys.getInt(1);
                        fahrer.setId(personId);

                        // Fahrer speichern
                        try (PreparedStatement pstmtFahrer = conn.prepareStatement(insertFahrer)) {
                            pstmtFahrer.setInt(1, personId);
                            pstmtFahrer.setString(2, fahrer.getFuehrerscheinklasse());
                            pstmtFahrer.setString(3, fahrer.getFahrzeugtyp());
                            pstmtFahrer.executeUpdate();
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
        String updatePerson = "UPDATE person SET name = ?, adresse = ?, telefon = ?, email = ? WHERE person_id = ?";
        String updateFahrer = "UPDATE fahrer SET fuehrerscheinklasse = ?, fahrzeugtyp = ? WHERE fahrer_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPerson = conn.prepareStatement(updatePerson)) {
                pstmtPerson.setString(1, fahrer.getName());
                pstmtPerson.setString(2, fahrer.getAdresse());
                pstmtPerson.setString(3, fahrer.getTelefon());
                pstmtPerson.setString(4, fahrer.getEmail());
                pstmtPerson.setInt(5, fahrer.getId());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtFahrer = conn.prepareStatement(updateFahrer)) {
                pstmtFahrer.setString(1, fahrer.getFuehrerscheinklasse());
                pstmtFahrer.setString(2, fahrer.getFahrzeugtyp());
                pstmtFahrer.setInt(3, fahrer.getId());
                pstmtFahrer.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String deleteFahrer = "DELETE FROM fahrer WHERE fahrer_id = ?";
        String deletePerson = "DELETE FROM person WHERE person_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtFahrer = conn.prepareStatement(deleteFahrer)) {
                pstmtFahrer.setInt(1, id);
                pstmtFahrer.executeUpdate();
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
