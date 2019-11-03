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

    private String section;

    private String shift;

    public NotificationObjStudent() {

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
}
