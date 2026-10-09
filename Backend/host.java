public class host {
    private int hostId;
    private String name;
    private String department;
    private String email;

    public host(int hostId, String name, String department, String email) {
        this.hostId = hostId;
        this.name = name;
        this.department = department;
        this.email = email;
    }

    public int getHostId() { return hostId; }
    public void setHostId(int hostId) { this.hostId = hostId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}