package bd.edu.daffodilvarsity.classmanager.otherclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelperClass {

    public static final String SHARED_PREFERENCE_TAG = "shared_preferences";

    public static final String PROGRAM = "program";

    public static final String PROGRAM_BSC = "B.Sc. in CSE";

    public static final String PROGRAM_MSC = "M.Sc. in CSE";

    public static final String SHIFT = "shift";

    public static final String SHIFT_DAY = "Day";

    public static final String SHIFT_EVENING = "Evening";

    public static final String LEVEL = "level";

    public static final String TERM = "term";

    public static final String COURSES_HASH_MAP = "course_list";

    public static final String USER_TYPE = "user_type";

    public static final String USER_TYPE_STUDENT = "user_type_student";

    public static final String USER_TYPE_TEACHER = "user_type_teacher";

    public static final String USER_TYPE_ADMIN = "user_type_admin";



    /*private Map<String,String> courcesBscDay;

    private Map<String,String> courcesBscEvening;

    private Map<String,String> courcesMsc;

    public HelperClass() {

    }*/

    public ArrayList<String> getCourseList(String program, String shift, String level, String term) {

        if(program.equals(PROGRAM_BSC) && shift.equals("Day"))    {
            switch (level) {
                case "Level 1":
                    switch (term) {
                        case "Term 1":
                            return L1T1DayBsc();
                        case "Term 2":
                            return L1T2DayBsc();
                        case "Term 3":
                            return L1T3DayBsc();
                    }
                    break;
                case "Level 2":
                    switch (term) {
                        case "Term 1":
                            return L2T1DayBsc();
                        case "Term 2":
                            return L2T2DayBsc();
                        case "Term 3":
                            return L2T3DayBsc();
                    }
                    break;
                case "Level 3":
                    switch (term) {
                        case "Term 1":
                            return L3T1DayBsc();
                        case "Term 2":
                            return L3T2DayBsc();
                        case "Term 3":
                            return L3T3DayBsc();
                    }
                    break;
                case "Level 4":
                    switch (term) {
                        case "Term 1":
                            return L4T1DayBsc();
                        case "Term 2":
                            return L4T2DayBsc();
                        case "Term 3":
                            return L4T3DayBsc();
                    }
                    break;
            }
        }
        else if(program.equals(PROGRAM_BSC) && shift.equals("Evening"))  {
            switch (level) {
                case "Level 1":
                    switch (term) {
                        case "Term 1":
                            return L1T1EveningBsc();
                        case "Term 2":
                            return L1T2DayBsc();
                        case "Term 3":
                            return L1T3DayBsc();
                    }
                    break;
                case "Level 2":
                    switch (term) {
                        case "Term 1":
                            return L2T1EveningBsc();
                        case "Term 2":
                            return L2T2EveningBsc();
                        case "Term 3":
                            return L2T3EveningBsc();
                    }
                    break;
                case "Level 3":
                    switch (term) {
                        case "Term 1":
                            return L3T1EveningBsc();
                        case "Term 2":
                            return L3T2EveningBsc();
                        case "Term 3":
                            return L3T3EveningBsc();
                    }
                    break;
            }
        }

        return null;
    }

    private ArrayList<String> L1T1DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE112");
        courseCodes.add("MAT111");
        courseCodes.add("ENG113");
        courseCodes.add("PHY113");

        return courseCodes;
    }

    private ArrayList<String> L1T2DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT121");
        courseCodes.add("CSE122");
        courseCodes.add("CSE123");
        courseCodes.add("PHY123");
        courseCodes.add("PHY124");
        courseCodes.add("ENG123");

        return courseCodes;
    }

    private ArrayList<String> L1T3DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE131");
        courseCodes.add("CSE132");
        courseCodes.add("CSE133");
        courseCodes.add("CSE134");
        courseCodes.add("CSE135");
        courseCodes.add("MAT131");

        return courseCodes;
    }

    private ArrayList<String> L2T1DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT211");
        courseCodes.add("CSE212");
        courseCodes.add("CSE213");
        courseCodes.add("CSE214");
        courseCodes.add("CSE215");
        courseCodes.add("ED201");

        return courseCodes;
    }

    private ArrayList<String> L2T2DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE221");
        courseCodes.add("CSE222");
        courseCodes.add("STA133");
        courseCodes.add("CSE224");
        courseCodes.add("CSE225");

        return courseCodes;
    }

    private ArrayList<String> L2T3DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE231");
        courseCodes.add("CSE232");
        courseCodes.add("CSE233");
        courseCodes.add("CSE234");
        courseCodes.add("CSE235");

        return courseCodes;
    }

    private ArrayList<String> L3T1DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE311");
        courseCodes.add("CSE312");
        courseCodes.add("CSE313");
        courseCodes.add("CSE314");
        courseCodes.add("GED321");

        return courseCodes;
    }

    private ArrayList<String> L3T2DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE321");
        courseCodes.add("CSE322");
        courseCodes.add("CSE323");
        courseCodes.add("CSE324");
        courseCodes.add("ECO314");

        return courseCodes;
    }

    private ArrayList<String> L3T3DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE331");
        courseCodes.add("CSE332");
        courseCodes.add("CSE333");
        courseCodes.add("CSE334");
        courseCodes.add("ACT301");

        return courseCodes;
    }

    private ArrayList<String> L4T1DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE412");
        courseCodes.add("CSE413");
        courseCodes.add("CSE414");
        courseCodes.add("CSE415");
        courseCodes.add("CSE417");
        courseCodes.add("CSE418");

        return courseCodes;
    }

    private ArrayList<String> L4T2DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE421");
        courseCodes.add("CSE422");
        courseCodes.add("CSE423");

        return courseCodes;
    }

    private ArrayList<String> L4T3DayBsc()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE498");

        return courseCodes;
    }

    private ArrayList<String> L1T1EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT121");
        courseCodes.add("CSE131");
        courseCodes.add("ENG113");
        courseCodes.add("PHY123");
        courseCodes.add("PHY123L");

        return courseCodes;
    }

    private ArrayList<String> L1T2EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE213");
        courseCodes.add("CSE213L");
        courseCodes.add("ACC214");
        courseCodes.add("ECO314");
        courseCodes.add("MAT134");

        return courseCodes;
    }

    private ArrayList<String> L1T3EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE221");
        courseCodes.add("CSE222");
        courseCodes.add("CSE222L");
        courseCodes.add("MAT211");
        courseCodes.add("STA223");

        return courseCodes;
    }

    private ArrayList<String> L2T1EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE231");
        courseCodes.add("CSE231L");
        courseCodes.add("CSE224");
        courseCodes.add("CSE224L");
        courseCodes.add("CSE232");
        courseCodes.add("CSE233");

        return courseCodes;
    }

    private ArrayList<String> L2T2EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add(" CSE322");
        courseCodes.add("CSE313");
        courseCodes.add("CSE313L");
        courseCodes.add("CSE311");
        courseCodes.add("CSE311L");
        courseCodes.add("CSE312");

        return courseCodes;
    }

    private ArrayList<String> L2T3EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE331");
        courseCodes.add("CSE331L");
        courseCodes.add("CSE413");
        courseCodes.add("CSE413L");
        courseCodes.add("CSE323");
        courseCodes.add("CSE323L");

        return courseCodes;
    }

    private ArrayList<String> L3T1EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE321");
        courseCodes.add("CSE321L");
        courseCodes.add("CSE421");
        courseCodes.add("CSE421L");
        courseCodes.add("CSE431");
        courseCodes.add("MGT414");


        return courseCodes;
    }

    private ArrayList<String> L3T2EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE412");
        courseCodes.add("CSE412L");
        courseCodes.add("CSE411");
        courseCodes.add("CSE332");
        courseCodes.add("CSE499");


        return courseCodes;
    }

    private ArrayList<String> L3T3EveningBsc()  {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE333");
        courseCodes.add("CSE432");
        courseCodes.add("CSE499");


        return courseCodes;
    }

    public static ArrayList<String> getClassTimes()    {
        ArrayList<String> classTimes = new ArrayList<>();

        classTimes.add("08:30AM-10:00AM");
        classTimes.add("10:00AM-11:30AM");
        classTimes.add("11.30AM-01:00PM");
        classTimes.add("01:00PM-02:30PM");
        classTimes.add("02:30PM-04:00PM");
        classTimes.add("04:00PM-05:30PM");
        classTimes.add("6.00PM-7.30PM");
        classTimes.add("7.30PM-9.00PM");

        return classTimes;
    }

    public static ArrayList<String> getSevenDaysOfWeek()    {

        ArrayList<String> daysOfWeek = new ArrayList<>();

        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");

        return daysOfWeek;
    }

    public static Map<String,String> getCoursesDay()    {

        Map<String,String> coursesDay = new HashMap<>();

        coursesDay.put("CSE112","Computer Fundamentals");
        coursesDay.put("MAT111","Mathematics-I: Differential and Integral Calculus");
        coursesDay.put("ENG113","Basic Functional English and English Spoken");
        coursesDay.put("PHY113","Physics-I: Mechanics, Heat and Thermodynamics,Waves and Oscillation, Optics");
        coursesDay.put("MAT121","Mathematics -II: Complex Variable, linear Algebra and Coordinate Geometry");
        coursesDay.put("CSE122","Programming and Problem Solving");
        coursesDay.put("CSE123","Problem Solving Lab");
        coursesDay.put("PHY123","Physics-II: Electricity, Magnetism and Modern Physics");
        coursesDay.put("PHY124","Physics-II Lab");
        coursesDay.put("ENG123","Writing and Comprehension");
        coursesDay.put("CSE131","Discrete Mathematics");
        coursesDay.put("CSE132","Electrical Circuits");
        coursesDay.put("CSE133","Electrical Circuits Lab");
        coursesDay.put("CSE134","Data Structure");
        coursesDay.put("CSE135","Data Structure Lab");
        coursesDay.put("MAT131","Ordinary and Partial Differential Equations");
        coursesDay.put("MAT211","Engineering Mathematics");
        coursesDay.put("CSE212","Digital Electronics");
        coursesDay.put("CSE213","Digital Electronics Lab");
        coursesDay.put("CSE214","Object Oriented Programming");
        coursesDay.put("CSE215","Object Oriented Programming Lab");
        coursesDay.put("GED201","Bangladesh Studies");
        coursesDay.put("CSE221","Algorithms");
        coursesDay.put("CSE222","Algorithms Lab");
        coursesDay.put("STA133","Statistics and Probability");
        coursesDay.put("CSE224","Electronic Devices and Circuits");
        coursesDay.put("CSE225","Electronic Devices and Circuits Lab");
        coursesDay.put("CSE231","Microprocessor and Assembly Language");
        coursesDay.put("CSE232","Microprocessor and Assembly Language Lab");
        coursesDay.put("CSE233","Data Communication");
        coursesDay.put("CSE234","Numerical Methods");
        coursesDay.put("CSE235","Introduction to Bio-Informatics");
        coursesDay.put("CSE311","Database Management System");
        coursesDay.put("CSE312","Database Management System Lab");
        coursesDay.put("CSE313","Computer Networks");
        coursesDay.put("CSE314","Computer Networks Lab");
        coursesDay.put("ECO314","Economics");
        coursesDay.put("CSE321","System Analysis and Design");
        coursesDay.put("CSE322","Computer Architecture and Organization");
        coursesDay.put("CSE323","Operating Systems");
        coursesDay.put("CSE324","Operating Systems Lab");
        coursesDay.put("GED321","Art of Living");
        coursesDay.put("CSE331","Compiler Design");
        coursesDay.put("CSE332","Compiler Design Lab");
        coursesDay.put("CSE333","Software Engineering");
        coursesDay.put("CSE334","Wireless Programming");
        coursesDay.put("ACT301","Financial and Managerial Accounting 2");
        coursesDay.put("CSE412","Artificial Intelligence");
        coursesDay.put("CSE413","Artificial Intelligence Lab");
        coursesDay.put("CSE414","Simulation and Modelling");
        coursesDay.put("CSE415","Simulation and Modelling Lab");
        coursesDay.put("CSE417","Web Engineering");
        coursesDay.put("CSE418","Web Engineering Lab");
        coursesDay.put("CSE421","Computer Graphics");
        coursesDay.put("CSE422","Computer Graphics Lab");
        coursesDay.put("CSE423","Embedded Systems");
        coursesDay.put("CSE499","Project / Internship (Phase I, to be completed in Level-4 Term-3");
        coursesDay.put("CSE498","Social and Professional Issues in Computing");

        return coursesDay;
    }

    public static Map<String,String> getCoursesEvening()    {
        return null;
    }

}
