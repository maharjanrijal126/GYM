package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    // Encapsulation: private DB credentials
    private final String url    = "jdbc:mysql://localhost:3306/gym_db";
    private final String dbUser = "root";
    private final String dbPass = "Rij@lmhrzn12";

    // Returns a live DB connection — throws RuntimeException so callers see the real error
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPass);
    }

    // ─── READ ───────────────────────────────────────────────────────────────

    // View all users (READ from CRUD)
    public void viewUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Users in database:");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("userid") + " | " +
                    rs.getString("username") + " | " +
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validate login — returns role string or null if credentials are wrong
    public String validateLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("role");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get userid by username
    public int getUserId(String username) {
        String sql = "SELECT userid FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("userid");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Fetch all members with their membership info (READ)
    public List<String[]> getAllMembers() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT m.memberid, m.full_name, m.contact_number, m.email, " +
            "       mt.membership_type_name, ms.start_date, ms.end_date, ms.status " +
            "FROM members m " +
            "LEFT JOIN membership ms ON m.memberid = ms.memberid " +
            "LEFT JOIN membership_type mt ON ms.membership_type_id = mt.membership_type_id";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("memberid"),
                    rs.getString("full_name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("membership_type_name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Search members by name or contact number (READ)
    public List<String[]> searchMembers(String keyword) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT m.memberid, m.full_name, m.contact_number, m.email, " +
            "       mt.membership_type_name, ms.start_date, ms.end_date, ms.status " +
            "FROM members m " +
            "LEFT JOIN membership ms ON m.memberid = ms.memberid " +
            "LEFT JOIN membership_type mt ON ms.membership_type_id = mt.membership_type_id " +
            "WHERE m.full_name LIKE ? OR m.contact_number LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("memberid"),
                    rs.getString("full_name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("membership_type_name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get member details for member dashboard (READ)
    public String[] getMemberDetails(int userId) {
        String sql =
            "SELECT m.full_name, m.contact_number, m.email, m.join_date, " +
            "       mt.membership_type_name, ms.start_date, ms.end_date, ms.status " +
            "FROM members m " +
            "LEFT JOIN membership ms ON m.memberid = ms.memberid " +
            "LEFT JOIN membership_type mt ON ms.membership_type_id = mt.membership_type_id " +
            "WHERE m.userid = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("full_name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("join_date"),
                    rs.getString("membership_type_name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("status")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Dashboard stat counts: [total, active, expired]
    public int[] getDashboardStats() {
        int[] stats = {0, 0, 0};
        String sql =
            "SELECT COUNT(*) AS total, " +
            "  SUM(CASE WHEN ms.status='Active'  THEN 1 ELSE 0 END) AS active, " +
            "  SUM(CASE WHEN ms.status='Expired' THEN 1 ELSE 0 END) AS expired " +
            "FROM members m LEFT JOIN membership ms ON m.memberid = ms.memberid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("active");
                stats[2] = rs.getInt("expired");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Get all staff records (READ)
    public List<String[]> getAllStaff() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT s.staffid, u.username, s.salary FROM staff s " +
            "JOIN users u ON s.userid = u.userid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("staffid"),
                    rs.getString("username"),
                    rs.getString("salary")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─── CREATE ─────────────────────────────────────────────────────────────

    // Add a member by admin — inserts user + member + membership in one transaction
    public boolean addMember(String username, String password,
                             String fullName, String contact,
                             String email, String joinDate,
                             int membershipTypeId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Insert user
            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'member')",
                Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.executeUpdate();
            ResultSet keys = userStmt.getGeneratedKeys();
            int newUserId = keys.next() ? keys.getInt(1) : -1;

            // 2. Insert member details
            PreparedStatement memStmt = conn.prepareStatement(
                "INSERT INTO members (userid, full_name, contact_number, email, join_date) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            memStmt.setInt(1, newUserId);
            memStmt.setString(2, fullName);
            memStmt.setString(3, contact);
            memStmt.setString(4, email);
            memStmt.setString(5, joinDate);
            memStmt.executeUpdate();
            ResultSet memKeys = memStmt.getGeneratedKeys();
            int newMemberId = memKeys.next() ? memKeys.getInt(1) : -1;

            // 3. Look up plan duration
            int durationMonths = 1;
            PreparedStatement durStmt = conn.prepareStatement(
                "SELECT duration_months FROM membership_type WHERE membership_type_id=?");
            durStmt.setInt(1, membershipTypeId);
            ResultSet durRs = durStmt.executeQuery();
            if (durRs.next()) durationMonths = durRs.getInt("duration_months");

            // 4. Insert membership
            PreparedStatement mshipStmt = conn.prepareStatement(
                "INSERT INTO membership (memberid, membership_type_id, start_date, end_date, status) " +
                "VALUES (?, ?, ?, DATE_ADD(?, INTERVAL ? MONTH), 'Active')");
            mshipStmt.setInt(1, newMemberId);
            mshipStmt.setInt(2, membershipTypeId);
            mshipStmt.setString(3, joinDate);
            mshipStmt.setString(4, joinDate);
            mshipStmt.setInt(5, durationMonths);
            mshipStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // Self-registration — inserts user + member + Basic membership in one transaction
    public boolean registerMember(String username, String password,
                                  String fullName, String contact,
                                  String email, String joinDate) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Insert user with role = 'member'
            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'member')",
                Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.executeUpdate();
            ResultSet keys = userStmt.getGeneratedKeys();
            int newUserId = keys.next() ? keys.getInt(1) : -1;

            // 2. Insert member personal details
            PreparedStatement memStmt = conn.prepareStatement(
                "INSERT INTO members (userid, full_name, contact_number, email, join_date) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            memStmt.setInt(1, newUserId);
            memStmt.setString(2, fullName);
            memStmt.setString(3, contact);
            memStmt.setString(4, email);
            memStmt.setString(5, joinDate);
            memStmt.executeUpdate();
            ResultSet memKeys = memStmt.getGeneratedKeys();
            int newMemberId = memKeys.next() ? memKeys.getInt(1) : -1;

            // 3. Assign Basic (id=1) membership for 1 month
            PreparedStatement mshipStmt = conn.prepareStatement(
                "INSERT INTO membership (memberid, membership_type_id, start_date, end_date, status) " +
                "VALUES (?, 1, ?, DATE_ADD(?, INTERVAL 1 MONTH), 'Active')");
            mshipStmt.setInt(1, newMemberId);
            mshipStmt.setString(2, joinDate);
            mshipStmt.setString(3, joinDate);
            mshipStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // Add new staff — inserts user + staff in one transaction
    public boolean addStaff(String username, String password, double salary) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'staff')",
                Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.executeUpdate();
            ResultSet keys = userStmt.getGeneratedKeys();
            int newUserId = keys.next() ? keys.getInt(1) : -1;

            PreparedStatement staffStmt = conn.prepareStatement(
                "INSERT INTO staff (userid, salary) VALUES (?, ?)");
            staffStmt.setInt(1, newUserId);
            staffStmt.setDouble(2, salary);
            staffStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // ─── UPDATE ─────────────────────────────────────────────────────────────

    // Update member contact info (UPDATE)
    public boolean updateMember(int memberId, String fullName, String contact, String email) {
        String sql = "UPDATE members SET full_name=?, contact_number=?, email=? WHERE memberid=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, contact);
            stmt.setString(3, email);
            stmt.setInt(4, memberId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Renew membership by extending end_date (UPDATE)
    public boolean renewMembership(int memberId, int months) {
        String sql =
            "UPDATE membership SET end_date = DATE_ADD(end_date, INTERVAL ? MONTH), " +
            "status = 'Active' WHERE memberid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, months);
            stmt.setInt(2, memberId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── DELETE ─────────────────────────────────────────────────────────────

    // Delete member — deletes user row (cascades to member + membership)
    public boolean deleteMember(int memberId) {
        String getUser = "SELECT userid FROM members WHERE memberid=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(getUser)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userid");
                PreparedStatement delStmt = conn.prepareStatement(
                    "DELETE FROM users WHERE userid=?");
                delStmt.setInt(1, userId);
                return delStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete staff — deletes user row (cascades to staff)
    public boolean deleteStaff(int staffId) {
        String getUser = "SELECT userid FROM staff WHERE staffid=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(getUser)) {

            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userid");
                PreparedStatement delStmt = conn.prepareStatement(
                    "DELETE FROM users WHERE userid=?");
                delStmt.setInt(1, userId);
                return delStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if username already taken (used for validation before INSERT)
    public boolean usernameExists(String username) {
        String sql = "SELECT userid FROM users WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Test DB connectivity — called on startup to catch config issues early
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
