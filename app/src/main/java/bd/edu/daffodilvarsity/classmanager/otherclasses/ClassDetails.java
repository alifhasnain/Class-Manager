package bd.edu.daffodilvarsity.classmanager.otherclasses;

import com.google.firebase.Timestamp;

public class ClassDetails {

    private String room;

    private String courseCode;

    private String courseName = "";

    private String teacherInitial;

    private String time;

    private String shift;

    private String dayOfWeek;

    private String section = "";

    private String batch = "";

    private float priority;

    private Timestamp timestamp = null;

    private String teacherUid = "";

    private String documentId;

    public ClassDetails()   {

    }

    public ClassDetails(String room, String courseCode, String teacherInitial, String shift, String dayOfWeek) {
        this.room = room;
        this.courseCode = courseCode;
        this.teacherInitial = teacherInitial;
        this.shift = shift;
        this.dayOfWeek = dayOfWeek;
    }

    public ClassDetails(String room, String courseCode, String courseName, String teacherInitial, String shift, String dayOfWeek) {
        this.room = room;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.teacherInitial = teacherInitial;
        this.shift = shift;
        this.dayOfWeek = dayOfWeek;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public float getPriority() {
        return priority;
    }

    public void setPriority(float priority) {
        this.priority = priority;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(String teacherUid) {
        this.teacherUid = teacherUid;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}