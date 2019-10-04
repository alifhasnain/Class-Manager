package bd.edu.daffodilvarsity.classmanager.routine;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RoutineClassDetails {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String room;

    private String courseCode;

    private String courseName;

    private String teacherInitial;

    private String time;

    private String dayOfWeek;

    private String shift;

    private String section;

    private float priority;

    private boolean notificationEnabled;

    public RoutineClassDetails() {

    }

    public RoutineClassDetails(String room, String courseCode, String courseName, String teacherInitial, String time, String dayOfWeek, String shift, String section, float priority) {
        this.room = room;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.teacherInitial = teacherInitial;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
        this.shift = shift;
        this.section = section;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public int getId() {
        return id;
    }

    public String getRoom() {
        return room;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherInitial() {
        return teacherInitial;
    }

    public String getTime() {
        return time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getSection() {
        return section;
    }

    public float getPriority() {
        return priority;
    }

    public String getShift() {
        return shift;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeacherInitial(String teacherInitial) {
        this.teacherInitial = teacherInitial;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setPriority(float priority) {
        this.priority = priority;
    }
}
