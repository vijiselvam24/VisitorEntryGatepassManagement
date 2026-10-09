public class gatepass {
    private int passId;
    private int visitorId;
    private int hostId;
    private String status;

    public gatepass(int passId, int visitorId, int hostId, String status) {
        this.passId = passId;
        this.visitorId = visitorId;
        this.hostId = hostId;
        this.status = status;
    }

    public int getPassId() { return passId; }
    public void setPassId(int passId) { this.passId = passId; }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public int getHostId() { return hostId; }
    public void setHostId(int hostId) { this.hostId = hostId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}