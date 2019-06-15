package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class ProfileObjectTeacher {

    private String name;

    private String teacherInitial;

    private int classesBookedThisMonth = 0;

    private String designation;

    public ProfileObjectTeacher() {

    }

    public ProfileObjectTeacher(String name, String teacherInitial, int classesBookedThisMonth,String designation) {
        this.name = name;
        this.teacherInitial = teacherInitial;
        this.classesBookedThisMonth = classesBookedThisMonth;
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
