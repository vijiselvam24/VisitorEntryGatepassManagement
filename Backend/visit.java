public class visit {
    private int visitId;
    private int visitorId;
    private String purpose;
    private String visitDate;

    public visit(int visitId, int visitorId, String purpose, String visitDate) {
        this.visitId = visitId;
        this.visitorId = visitorId;
        this.purpose = purpose;
        this.visitDate = visitDate;
    }

    public int getVisitId() { return visitId; }
    public void setVisitId(int visitId) { this.visitId = visitId; }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
}