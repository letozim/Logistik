package org.example.repository;

import org.example.model.Ware;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// für Datenbankoperationen
public class WarenRepository {

    private final String url = "jdbc:mysql://localhost:3306/logistik_crm";
    private final String user = "root";      // MySQL-Benutzer
    private final String password = "meinDatenbank";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Ware> findAll() {
        List<Ware> warenListe = new ArrayList<>();
        String sql = "SELECT * FROM ware";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ware ware = new Ware(
                        rs.getString("artikelnummer"),
                        rs.getString("name"),
                        rs.getString("beschreibung"),
                        rs.getInt("menge"),
                        rs.getString("einheit"),
                        rs.getDouble("preis")
                );
                warenListe.add(ware);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warenListe;
    }

    public void save(Ware ware) {
        String sql = "INSERT INTO ware (artikelnummer, name, beschreibung, menge, einheit, preis) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ware.getArtikelnummer());
            pstmt.setString(2, ware.getName());
            pstmt.setString(3, ware.getBeschreibung());
            pstmt.setInt(4, ware.getMenge());
            pstmt.setString(5, ware.getEinheit());
            pstmt.setDouble(6, ware.getPreis());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Ware ware) {
        String sql = "UPDATE ware SET name=?, beschreibung=?, menge=?, einheit=?, preis=? WHERE artikelnummer=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ware.getName());
            pstmt.setString(2, ware.getBeschreibung());
            pstmt.setInt(3, ware.getMenge());
            pstmt.setString(4, ware.getEinheit());
            pstmt.setDouble(5, ware.getPreis());
            pstmt.setString(6, ware.getArtikelnummer());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String artikelnummer) {
        String sql = "DELETE FROM ware WHERE artikelnummer=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artikelnummer);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Ware findByArtikelnummer(String artikelnummer) {
        String sql = "SELECT * FROM ware WHERE artikelnummer = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, artikelnummer);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Ware(
                            rs.getString("artikelnummer"),
                            rs.getString("name"),
                            rs.getString("beschreibung"),
                            rs.getInt("menge"),
                            rs.getString("einheit"),
                            rs.getDouble("preis")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
