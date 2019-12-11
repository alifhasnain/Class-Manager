package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class CRObj {

    private String name;

    private String email;

    private String courseCode;

    private String courseName;

    private String section;

    private String phoneNo;

    public CRObj(String name, String email, String courseCode, String courseName, String section, String phoneNo) {
        this.name = name;
        this.email = email;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.section = section;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
