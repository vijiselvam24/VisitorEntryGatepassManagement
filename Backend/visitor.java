public class visitor {
    private int visitorId;
    private String name;
    private String phone;

    public visitor(int visitorId, String name, String phone) {
        this.visitorId = visitorId;
        this.name = name;
        this.phone = phone;
    }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}