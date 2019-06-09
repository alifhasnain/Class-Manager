package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class ClassDetails {

    private String room;

    private String courseCode;

    private String teacherInitial;

    private String time;

    public ClassDetails()   {

    }

    public ClassDetails(String room, String courseCode, String teacherInitial) {
        this.room = room;
        this.courseCode = courseCode;
        this.teacherInitial = teacherInitial;
    }

    public ClassDetails(String room, String courseCode, String teacherInitial, String time) {
        this.room = room;
        this.courseCode = courseCode;
        this.teacherInitial = teacherInitial;
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTeacherInitial() {
        return teacherInitial;
    }

    public void setTeacherInitial(String teacherInitial) {
        this.teacherInitial = teacherInitial;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}