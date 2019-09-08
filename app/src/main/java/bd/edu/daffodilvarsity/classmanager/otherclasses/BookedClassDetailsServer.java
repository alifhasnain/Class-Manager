package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class BookedClassDetailsServer {

    private String roomNo = "";

    private String time = "";

    private String teacherInitial = "";

    private int[] reservationDate = new int[3];

    private String program = "";

    private String shift;

    private String section;

    private String courseCode;

    private float priority;

    public BookedClassDetailsServer() {

    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacherInitial() {
        return teacherInitial;
    }

    public void setTeacherInitial(String teacherInitial) {
        this.teacherInitial = teacherInitial;
    }

    public int[] getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(int[] reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public float getPriority() {
        return priority;
    }

    public void setPriority(float priority) {
        this.priority = priority;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
