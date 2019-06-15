package bd.edu.daffodilvarsity.classmanager.otherclasses;

public class ProfileObjectStudent {

    private String name;

    private String id;

    private String section;

    private String department;

    private String shift;

    private String level;

    private String term;

    public ProfileObjectStudent() {

    }

    public ProfileObjectStudent(String name, String id, String section, String department, String shift, String level, String term) {
        this.name = name;
        this.id = id;
        this.section = section;
        this.department = department;
        this.shift = shift;
        this.level = level;
        this.term = term;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
