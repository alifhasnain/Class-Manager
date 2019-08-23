package bd.edu.daffodilvarsity.classmanager.room;

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

    private int notificationId;

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

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
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

    public int getNotificationId() {
        return notificationId;
    }

    public String getShift() {
        return shift;
    }
}
