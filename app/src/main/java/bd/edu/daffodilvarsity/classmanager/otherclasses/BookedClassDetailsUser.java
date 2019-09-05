package bd.edu.daffodilvarsity.classmanager.otherclasses;

import com.google.firebase.Timestamp;

public class BookedClassDetailsUser {

    private String roomNo = "";

    private String time = "";

    private String teacherInitial = "";

    private String teacherEmail = "";

    private Timestamp reservationDate;

    private String dayOfWeek = "";

    private Timestamp timeWhenUserBooked;

    private String uid = "";

    private String program = "";

    private String docId;

    private String shift;

    private String section;

    private String courseCode;

    private float priority;

    public BookedClassDetailsUser() {

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Timestamp getTimeWhenUserBooked() {
        return timeWhenUserBooked;
    }

    public void setTimeWhenUserBooked(Timestamp timeWhenUserBooked) {
        this.timeWhenUserBooked = timeWhenUserBooked;
    }

    public Timestamp getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Timestamp reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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
