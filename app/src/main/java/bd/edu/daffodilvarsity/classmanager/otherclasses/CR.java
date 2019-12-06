package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class CR {

    private String name;

    private String section;

    private String batch;

    private String phoneNo;

    private String semester;

    public CR(String name, String section, String batch, String phoneNo, String semester) {
        this.name = name;
        this.section = section;
        this.batch = batch;
        this.phoneNo = phoneNo;
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
