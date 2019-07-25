package bd.edu.daffodilvarsity.classmanager.otherclasses;

import com.google.firebase.Timestamp;

public class BookedClassDetails {

    private String roomNo;

    private String time;

    private String teacherInitial;

    private Timestamp reservationTime;

    private String dayOfWeek;

    private Timestamp timeWhenUserBooked;

    private String uid;

    private String docId;

    private String shift;

    private String section;

    private String courseCode;

    private float priority;

    public BookedClassDetails() {

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

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Timestamp getTimeWhenUserBooked() {
        return timeWhenUserBooked;
    }

    public void setTimeWhenUserBooked(Timestamp timeWhenUserBooked) {
        this.timeWhenUserBooked = timeWhenUserBooked;
    }

    public Timestamp getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Timestamp reservationTime) {
        this.reservationTime = reservationTime;
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
}
