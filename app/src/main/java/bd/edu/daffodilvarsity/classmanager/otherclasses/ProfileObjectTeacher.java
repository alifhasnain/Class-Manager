package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class ProfileObjectTeacher {

    private String name;

    private String email;

    private String teacherInitial;

    private int classesBookedThisMonth = 0;

    private String designation;

    public ProfileObjectTeacher() {

    }

    public ProfileObjectTeacher(String name, String email, String teacherInitial, int classesBookedThisMonth, String designation) {
        this.name = name;
        this.email = email;
        this.teacherInitial = teacherInitial;
        this.classesBookedThisMonth = classesBookedThisMonth;
        this.designation = designation;
    }

    public void createInstance(ProfileObjectTeacher profile)    {
        this.name = profile.getName();
        this.email = profile.getEmail();
        this.teacherInitial = profile.getTeacherInitial();
        this.classesBookedThisMonth = profile.getClassesBookedThisMonth();
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

    public int getClassesBookedThisMonth() {
        return classesBookedThisMonth;
    }

    public void setClassesBookedThisMonth(int classesBookedThisMonth) {
        this.classesBookedThisMonth = classesBookedThisMonth;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
