package bd.edu.daffodilvarsity.classmanager.notification;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotificationObjStudent {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String teacherName;

    private String teacherEmail;

    private String courseCode;

    private String courseName;

    private String section;

    private String shift;

    private String time;

    private String roomNo;

    private String date;

    public NotificationObjStudent() {

    }

    public NotificationObjStudent(String teacherName, String teacherEmail, String courseCode,String courseName, String section, String shift, String time, String roomNo, String date) {
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.section = section;
        this.shift = shift;
        this.time = time;
        this.roomNo = roomNo;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
