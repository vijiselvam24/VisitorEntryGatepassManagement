import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class VisitorServer {
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/gatpass";
    private static final String DB_USER = "root";

    private static Connection getConnection() throws SQLException {
        String password = System.getenv("GATPASS_DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new SQLException("GATPASS_DB_PASSWORD is not set");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, password);
    }

    private static String json(String value) {
        if (value == null) return "null";
        return "\"" + value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/api/visitor", exchange -> {
            Headers h = exchange.getResponseHeaders();
            h.set("Access-Control-Allow-Origin", "*");
            h.set("Access-Control-Allow-Methods", "POST, OPTIONS");
            h.set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            int status = 200;
            String response;

            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    status = 405;
                    response = "Method not allowed";
                } else {
                    String body = new String(
                            exchange.getRequestBody().readAllBytes(),
                            StandardCharsets.UTF_8);
                    Map<String, String> form = new HashMap<>();

                    for (String pair : body.split("&")) {
                        String[] parts = pair.split("=", 2);
                        if (parts.length == 2) {
                            form.put(
                                    URLDecoder.decode(parts[0],
                                            StandardCharsets.UTF_8),
                                    URLDecoder.decode(parts[1],
                                            StandardCharsets.UTF_8));
                        }
                    }

                    String name = form.getOrDefault("visitorName", "");
                    String phone = form.getOrDefault("phone", "");
                    String address = form.getOrDefault("address", "");
                    String idProof = form.getOrDefault("idProof", "");

                    if (name.isBlank() || phone.isBlank()) {
                        status = 400;
                        response = "Name and phone are required";
                    } else {
                        String sql = "INSERT INTO visitors " +
                                "(visitor_name, phone, address, id_proof_type) " +
                                "VALUES (?, ?, ?, ?)";

                        try (Connection con = getConnection();
                             PreparedStatement ps =
                                     con.prepareStatement(sql)) {
                            ps.setString(1, name);
                            ps.setString(2, phone);
                            ps.setString(3, address);
                            ps.setString(4, idProof);
                            ps.executeUpdate();
                        }
                        response = "Visitor saved successfully";
                    }
                }
            } catch (Exception e) {
                status = 500;
                response = "Could not save visitor: " + e.getMessage();
                e.printStackTrace();
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            h.set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        server.createContext("/api/visitors", exchange -> {
            Headers h = exchange.getResponseHeaders();
            h.set("Access-Control-Allow-Origin", "*");
            h.set("Content-Type", "application/json; charset=UTF-8");

            int status = 200;
            String response;

            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    status = 405;
                    response = "{\"error\":\"Method not allowed\"}";
                } else {
                    StringBuilder result = new StringBuilder("[");
                    boolean first = true;

                    String sql = "SELECT visitor_id, visitor_name, phone, " +
                            "address, id_proof_type FROM visitors " +
                            "ORDER BY visitor_id DESC";

                    try (Connection con = getConnection();
                         PreparedStatement ps = con.prepareStatement(sql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            if (!first) result.append(",");
                            first = false;

                            result.append("{")
                                    .append("\"visitor_id\":")
                                    .append(rs.getInt("visitor_id"))
                                    .append(",\"visitor_name\":")
                                    .append(json(rs.getString("visitor_name")))
                                    .append(",\"phone\":")
                                    .append(json(rs.getString("phone")))
                                    .append(",\"address\":")
                                    .append(json(rs.getString("address")))
                                    .append(",\"id_proof_type\":")
                                    .append(json(rs.getString("id_proof_type")))
                                    .append("}");
                        }
                    }
                    result.append("]");
                    response = result.toString();
                }
            } catch (Exception e) {
                status = 500;
                response = "{\"error\":\"Could not load visitors\"}";
                e.printStackTrace();
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        
server.createContext("/api/host", exchange -> {
    Headers h = exchange.getResponseHeaders();
    h.set("Access-Control-Allow-Origin", "*");
    h.set("Access-Control-Allow-Methods", "POST, OPTIONS");
    h.set("Access-Control-Allow-Headers", "Content-Type");

    if ("OPTIONS".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    int status = 200;
    String response;

    try {
        if (!"POST".equals(exchange.getRequestMethod())) {
            status = 405;
            response = "Method not allowed";
        } else {
            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            Map<String, String> form = new HashMap<>();

            for (String pair : body.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    form.put(
                        URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    );
                }
            }

            String department = form.getOrDefault("department", "").trim();
            String hostName = form.getOrDefault("hostName", "").trim();
            String hostContact = form.getOrDefault("hostContact", "").trim();

            if (department.isEmpty() || hostName.isEmpty()
                    || hostContact.isEmpty()) {
                status = 400;
                response = "Please fill all fields.";
            } else {
                try (Connection con = getConnection()) {
                    con.setAutoCommit(false);

                    try {
                        int departmentId;

                        String findSql =
                            "SELECT department_id FROM departments " +
                            "WHERE department_name = ?";

                        try (PreparedStatement ps =
                                 con.prepareStatement(findSql)) {
                            ps.setString(1, department);

                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    departmentId = rs.getInt("department_id");
                                } else {
                                    String insertDepartment =
                                        "INSERT INTO departments " +
                                        "(department_name) VALUES (?)";

                                    try (PreparedStatement insert =
                                             con.prepareStatement(
                                                 insertDepartment,
                                                 Statement.RETURN_GENERATED_KEYS)) {
                                        insert.setString(1, department);
                                        insert.executeUpdate();

                                        try (ResultSet keys =
                                                 insert.getGeneratedKeys()) {
                                            if (!keys.next()) {
                                                throw new SQLException(
                                                    "Could not create department");
                                            }
                                            departmentId = keys.getInt(1);
                                        }
                                    }
                                }
                            }
                        }

                        String insertHost =
                            "INSERT INTO hosts " +
                            "(host_name, host_contact, department_id) " +
                            "VALUES (?, ?, ?)";

                        try (PreparedStatement ps =
                                 con.prepareStatement(insertHost)) {
                            ps.setString(1, hostName);
                            ps.setString(2, hostContact);
                            ps.setInt(3, departmentId);
                            ps.executeUpdate();
                        }

                        con.commit();
                        response = "Host details saved successfully!";
                    } catch (Exception e) {
                        con.rollback();
                        throw e;
                    }
                }
            }
        }
    } catch (Exception e) {
        status = 500;
        response = "Could not save host: " + e.getMessage();
        e.printStackTrace();
    }

    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
    h.set("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
});

server.createContext("/api/visit", exchange -> {
    Headers h = exchange.getResponseHeaders();
    h.set("Access-Control-Allow-Origin", "*");
    h.set("Access-Control-Allow-Methods", "POST, OPTIONS");
    h.set("Access-Control-Allow-Headers", "Content-Type");

    if ("OPTIONS".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    int status = 200;
    String response;

    try {
        if (!"POST".equals(exchange.getRequestMethod())) {
            status = 405;
            response = "Method not allowed";
        } else {
            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            Map<String, String> form = new HashMap<>();
            for (String pair : body.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    form.put(
                        URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    );
                }
            }

            int visitorId = Integer.parseInt(form.getOrDefault("visitorId", ""));
            int hostId = Integer.parseInt(form.getOrDefault("hostId", ""));
            String visitDate = form.getOrDefault("visitDate", "");
            String purpose = form.getOrDefault("purpose", "").trim();
            String entryTime = form.getOrDefault("entryTime", "");
            String exitTime = form.getOrDefault("exitTime", "");

            if (visitDate.isBlank() || purpose.isBlank() || entryTime.isBlank()) {
                throw new IllegalArgumentException(
                    "Visit date, purpose and entry time are required."
                );
            }

            String sql = "INSERT INTO visits " +
                "(visitor_id, host_id, visit_date, purpose, entry_time, exit_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, visitorId);
                ps.setInt(2, hostId);
                ps.setDate(3, Date.valueOf(visitDate));
                ps.setString(4, purpose);
                ps.setTimestamp(5, Timestamp.valueOf(entryTime.replace("T", " ") + ":00"));

                if (exitTime.isBlank()) {
                    ps.setNull(6, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(6, Timestamp.valueOf(exitTime.replace("T", " ") + ":00"));
                }

                ps.executeUpdate();
            }

            response = "Visit details saved successfully!";
        }
    } catch (Exception e) {
        status = 400;
        response = "Could not save visit: " + e.getMessage();
        e.printStackTrace();
    }

    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
    h.set("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
});

server.createContext("/api/gatepass", exchange -> {
    Headers h = exchange.getResponseHeaders();
    h.set("Access-Control-Allow-Origin", "*");
    h.set("Access-Control-Allow-Methods", "POST, OPTIONS");
    h.set("Access-Control-Allow-Headers", "Content-Type");

    if ("OPTIONS".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    int status = 200;
    String response;

    try {
        if (!"POST".equals(exchange.getRequestMethod())) {
            status = 405;
            response = "Method not allowed";
        } else {
            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            Map<String, String> form = new HashMap<>();
            for (String pair : body.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    form.put(
                        URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    );
                }
            }

            int visitId = Integer.parseInt(
                form.getOrDefault("visitId", "")
            );
            String issuedAt = form.getOrDefault("issuedAt", "");
            String passStatus = form.getOrDefault("status", "");

            if (issuedAt.isBlank() ||
                !java.util.Arrays.asList(
                    "Active", "Completed", "Cancelled"
                ).contains(passStatus)) {
                throw new IllegalArgumentException(
                    "Please enter issue date and a valid status."
                );
            }

            String sql = "INSERT INTO gatepasses " +
                "(visit_id, issued_at, status) VALUES (?, ?, ?)";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, visitId);
                ps.setTimestamp(
                    2, Timestamp.valueOf(
                        issuedAt.replace("T", " ") + ":00"
                    )
                );
                ps.setString(3, passStatus);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        response = "Gate pass generated successfully! Pass ID: "
                            + keys.getInt(1);
                    } else {
                        response = "Gate pass saved successfully!";
                    }
                }
            }
        }
    } catch (Exception e) {
        status = 400;
        response = "Could not generate gate pass: " + e.getMessage();
        e.printStackTrace();
    }

    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
    h.set("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
});
        server.start();
        System.out.println("Server running on port 8080");
    }
}