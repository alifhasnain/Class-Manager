package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class ProfileObjectTeacher {

    private String name = "";

    private String email = "";

    private String teacherInitial = "";

    private String designation = "";

    private String id = "";

    private String contactNo = "";

    public ProfileObjectTeacher() {

    }

    public ProfileObjectTeacher(String name, String email, String teacherInitial, String designation, String id, String contactNo) {
        this.name = name;
        this.email = email;
        this.teacherInitial = teacherInitial;
        this.designation = designation;
        this.id = id;
        this.contactNo = contactNo;
    }

    public ProfileObjectTeacher(String name, String email, String teacherInitial, String designation) {
        this.name = name;
        this.email = email;
        this.teacherInitial = teacherInitial;
        this.designation = designation;
    }

    public void createInstance(ProfileObjectTeacher profile) {
        this.name = profile.getName();
        this.email = profile.getEmail();
        this.teacherInitial = profile.getTeacherInitial();
        this.designation = profile.getDesignation();
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

    public String getTeacherInitial() {
        return teacherInitial;
    }

    public void setTeacherInitial(String teacherInitial) {
        this.teacherInitial = teacherInitial;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
