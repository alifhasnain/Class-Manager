package bd.edu.daffodilvarsity.classmanager.otherclasses;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HelperClass {

    private static HelperClass sHelperClass;

    public static final String SHARED_PREFERENCE_TAG = "shared_preferences";

    public static final String PROGRAM = "program";

    public static final String PROGRAM_BSC = "B.Sc. in CSE";

    public static final String PROGRAM_MSC = "M.Sc. in CSE";

    public static final String SHIFT = "shift";

    public static final String SHIFT_DAY = "Day";

    public static final String SHIFT_EVENING = "Evening";

    public static final String LEVEL = "level";

    public static final String TERM = "term";

    public static final String ROUTINE_VERSION_UPDATE = "routine_update";

    public static final String COURSES_HASH_MAP = "course_list";

    public static final String USER_TYPE = "user_type";

    public static final String USER_TYPE_STUDENT = "user_type_student";

    public static final String USER_TYPE_TEACHER = "user_type_teacher";

    public static final String USER_TYPE_ADMIN = "user_type_admin";

    public static final String STUDENT_PROFILE = "student_offline_profile";

    public static final String TEACHER_PROFILE = "teacher_offline_profile";

    public static final String TEACHER_INITIAL = "teacher_initial";

    public static final String USER_EMAIL = "user_email";

    public static final int NOTIFICATION_ALARM_REQ_CODE = 1111;

    public static final String WORK_SCHEDULER_ID = "work_scheduler_tag";

    public static final String STUDENT_NOTIFICATION_ENABLED = "student_notification";


    /*private Map<String,String> courcesBscDay;

    private Map<String,String> courcesBscEvening;

    private Map<String,String> courcesMsc;

    public HelperClass() {

    }*/

    private HelperClass() {
        //Required private constructor for singleton
        if (sHelperClass != null) {
            throw new RuntimeException("Use getInstance() to get the single instance of the class.");
        }
    }

    public static HelperClass getInstance() {

        if (sHelperClass == null) {
            sHelperClass = new HelperClass();
        }

        return sHelperClass;
    }

    public ArrayList<String> getCourseList(String program, String shift, String level, String term) {

        if (program.equals(PROGRAM_BSC) && shift.equals("Day")) {
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
        } else if (program.equals(PROGRAM_BSC) && shift.equals("Evening")) {
            switch (level) {
                case "Level 1":
                    switch (term) {
                        case "Term 1":
                            return L1T1EveningBsc();
                        case "Term 2":
                            return L1T2EveningBsc();
                        case "Term 3":
                            return L1T3EveningBsc();
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

    private ArrayList<String> L1T1DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE112");
        courseCodes.add("MAT111");
        courseCodes.add("ENG113");
        courseCodes.add("PHY113");
        courseCodes.add("PHY114");
        courseCodes.add("GED111");

        return courseCodes;
    }

    private ArrayList<String> L1T2DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT121");
        courseCodes.add("CSE122");
        courseCodes.add("CSE123");
        courseCodes.add("ENG123");
        courseCodes.add("CSE124");
        courseCodes.add("GED121");

        return courseCodes;
    }

    private ArrayList<String> L1T3DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE131");
        courseCodes.add("CSE132");
        courseCodes.add("CSE133");
        courseCodes.add("CSE134");
        courseCodes.add("CSE135");
        courseCodes.add("CSE136");
        courseCodes.add("GED131");

        return courseCodes;
    }

    private ArrayList<String> L2T1DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT211");
        courseCodes.add("CSE212");
        courseCodes.add("CSE213");
        courseCodes.add("CSE221-0");
        courseCodes.add("CSE222-0");
        courseCodes.add("CSE216");
        courseCodes.add("ACT211");

        return courseCodes;
    }

    private ArrayList<String> L2T2DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE221");
        courseCodes.add("CSE222");
        courseCodes.add("STA133");
        courseCodes.add("CSE224");
        courseCodes.add("CSE225");

        return courseCodes;
    }

    private ArrayList<String> L2T3DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE231");
        courseCodes.add("CSE232");
        courseCodes.add("CSE233");
        courseCodes.add("CSE234");
        courseCodes.add("CSE235");

        return courseCodes;
    }

    private ArrayList<String> L3T1DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE311");
        courseCodes.add("CSE312");
        courseCodes.add("CSE313");
        courseCodes.add("CSE314");
        courseCodes.add("GED321");

        return courseCodes;
    }

    private ArrayList<String> L3T2DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE321");
        courseCodes.add("CSE322");
        courseCodes.add("CSE323");
        courseCodes.add("CSE324");
        courseCodes.add("ECO314");

        return courseCodes;
    }

    private ArrayList<String> L3T3DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE331");
        courseCodes.add("CSE332");
        courseCodes.add("CSE333");
        courseCodes.add("CSE334");
        courseCodes.add("ACT301");

        return courseCodes;
    }

    private ArrayList<String> L4T1DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE412");
        courseCodes.add("CSE413");
        courseCodes.add("CSE414");
        courseCodes.add("CSE415");
        courseCodes.add("CSE417");
        courseCodes.add("CSE418");

        return courseCodes;
    }

    private ArrayList<String> L4T2DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE421");
        courseCodes.add("CSE422");
        courseCodes.add("CSE423");
        courseCodes.add("CSE450");
        courseCodes.add("CSE499");

        return courseCodes;
    }

    private ArrayList<String> L4T3DayBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE498");
        courseCodes.add("CSE444");
        courseCodes.add("CSE499");

        return courseCodes;
    }

    private ArrayList<String> L1T1EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT121");
        courseCodes.add("CSE131");
        courseCodes.add("ENG113");
        courseCodes.add("PHY123");
        courseCodes.add("PHY123L");

        return courseCodes;
    }

    private ArrayList<String> L1T2EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE213");
        courseCodes.add("CSE213L");
        courseCodes.add("ACC214");
        courseCodes.add("ECO314");
        courseCodes.add("MAT134");

        return courseCodes;
    }

    private ArrayList<String> L1T3EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE221");
        courseCodes.add("CSE222");
        courseCodes.add("CSE222L");
        courseCodes.add("MAT211");
        courseCodes.add("STA223");

        return courseCodes;
    }

    private ArrayList<String> L2T1EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE231");
        courseCodes.add("CSE231L");
        courseCodes.add("CSE224");
        courseCodes.add("CSE224L");
        courseCodes.add("CSE232");
        courseCodes.add("CSE233");

        return courseCodes;
    }

    private ArrayList<String> L2T2EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE322");
        courseCodes.add("CSE313");
        courseCodes.add("CSE313L");
        courseCodes.add("CSE311");
        courseCodes.add("CSE311L");
        courseCodes.add("CSE312");

        return courseCodes;
    }

    private ArrayList<String> L2T3EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE331");
        courseCodes.add("CSE331L");
        courseCodes.add("CSE413");
        courseCodes.add("CSE413L");
        courseCodes.add("CSE323");
        courseCodes.add("CSE323L");

        return courseCodes;
    }

    private ArrayList<String> L3T1EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE321");
        courseCodes.add("CSE321L");
        courseCodes.add("CSE421");
        courseCodes.add("CSE421L");
        courseCodes.add("CSE431");
        courseCodes.add("MGT414");


        return courseCodes;
    }

    private ArrayList<String> L3T2EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE412");
        courseCodes.add("CSE412L");
        courseCodes.add("CSE411");
        courseCodes.add("CSE332");
        courseCodes.add("CSE499");


        return courseCodes;
    }

    private ArrayList<String> L3T3EveningBsc() {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE333");
        courseCodes.add("CSE432");
        courseCodes.add("CSE499");


        return courseCodes;
    }

    public static ArrayList<String> getClassTimes() {
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

    public static ArrayList<String> getSixDaysOfWeek() {

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

    public static ArrayList<String> getSevenDaysOfWeek() {

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

    public static LinkedHashMap<String, String> getCoursesDay() {

        LinkedHashMap<String, String> coursesDay = new LinkedHashMap<>();

        coursesDay.put("CSE112", "Computer Fundamentals");
        coursesDay.put("MAT111", "Basic Mathematics");
        coursesDay.put("ENG113", "Basic Functional English and English Spoken");
        coursesDay.put("PHY113", "Basic Physics");
        coursesDay.put("PHY114", "Basic Physics Lab");
        coursesDay.put("GED111", "History of Bangladesh and Bangla Language");

        coursesDay.put("MAT121", "Complex Variable, linear Algebra and Coordinate Geometry");
        coursesDay.put("CSE122", "Programming and Problem Solving");
        coursesDay.put("CSE123", "Problem Solving Lab");
        coursesDay.put("ENG123", "Writing and Comprehension");
        coursesDay.put("CSE124", "Business Application Design");
        coursesDay.put("GED121", "Bangladesh Studies");

        coursesDay.put("CSE131", "Discrete Mathematics");
        coursesDay.put("CSE132", "Electrical Circuits");
        coursesDay.put("CSE133", "Electrical Circuits Lab");
        coursesDay.put("CSE134", "Data Structure");
        coursesDay.put("CSE135", "Data Structure Lab");
        coursesDay.put("CSE136", "Software Project I");
        coursesDay.put("GED131", "Art of Living");


        coursesDay.put("MAT211", "Engineering Mathematics");
        coursesDay.put("CSE212", "Basic Electronics");
        coursesDay.put("CSE213", "Basic Electronics Lab");
        coursesDay.put("CSE221-0", "Object Oriented Programming");
        coursesDay.put("CSE222-0", "Object Oriented Programming Lab");
        coursesDay.put("CSE216", "Software Project II");
        coursesDay.put("ACT211", "Financial and managerial Accounting ");

        coursesDay.put("CSE221", "Algorithms");
        coursesDay.put("CSE222", "Algorithms Lab");
        coursesDay.put("STA133", "Statistics and Probability");
        coursesDay.put("CSE224", "Electronic Devices and Circuits");
        coursesDay.put("CSE225", "Electronic Devices and Circuits Lab");

        coursesDay.put("CSE231", "Microprocessor and Assembly Language");
        coursesDay.put("CSE232", "Microprocessor and Assembly Language Lab");
        coursesDay.put("CSE233", "Data Communication");
        coursesDay.put("CSE234", "Numerical Methods");
        coursesDay.put("CSE235", "Introduction to Bio-Informatics");

        coursesDay.put("CSE311", "Database Management System");
        coursesDay.put("CSE312", "Database Management System Lab");
        coursesDay.put("CSE313", "Computer Networks");
        coursesDay.put("CSE314", "Computer Networks Lab");
        coursesDay.put("GED321", "Art of Living");

        coursesDay.put("CSE321", "System Analysis and Design");
        coursesDay.put("CSE322", "Computer Architecture and Organization");
        coursesDay.put("CSE323", "Operating Systems");
        coursesDay.put("CSE324", "Operating Systems Lab");
        coursesDay.put("ECO314", "Economics");

        coursesDay.put("CSE331", "Compiler Design");
        coursesDay.put("CSE332", "Compiler Design Lab");
        coursesDay.put("CSE333", "Software Engineering");
        coursesDay.put("CSE334", "Wireless Programming");
        coursesDay.put("ACT301", "Financial and Managerial Accounting");

        coursesDay.put("CSE412", "Artificial Intelligence");
        coursesDay.put("CSE413", "Artificial Intelligence Lab");
        coursesDay.put("CSE414", "Simulation and Modelling");
        coursesDay.put("CSE415", "Simulation and Modelling Lab");
        coursesDay.put("CSE417", "Web Engineering");
        coursesDay.put("CSE418", "Web Engineering Lab");

        coursesDay.put("CSE421", "Computer Graphics");
        coursesDay.put("CSE422", "Computer Graphics Lab");
        coursesDay.put("CSE423", "Embedded Systems");
        coursesDay.put("CSE450", "Data Mining");

        coursesDay.put("CSE444", "Introduction to Robotics");
        coursesDay.put("CSE498", "Social and Professional Issues in Computing");

        coursesDay.put("CSE499", "Project / Internship (Phase I, to be completed in Level-4 Term-3");

        return coursesDay;
    }

    public static LinkedHashMap<String, String> getCoursesEveningBsc() {

        LinkedHashMap<String, String> coursesEveningBsc = new LinkedHashMap<>();

        coursesEveningBsc.put("MAT121", "Mathematics II: Linear algebra and Coordinate Geometry");
        coursesEveningBsc.put("CSE131", "Discrete Mathematics");
        coursesEveningBsc.put("ENG113", "English Language-I");
        coursesEveningBsc.put("PHY123", "Physics- II: Electricity, Magnetism and Modern Physics");
        coursesEveningBsc.put("PHY123L", "Physics-II Lab");
        coursesEveningBsc.put("CSE213", "Algorithms");
        coursesEveningBsc.put("CSE213L", "Algorithm Lab");
        coursesEveningBsc.put("ACC214", "Accounting");
        coursesEveningBsc.put("ECO314", "Economics");
        coursesEveningBsc.put("MAT134", "Mathematics III : Ordinary and PartialDifferential Equations");
        coursesEveningBsc.put("CSE221", "Theory of Computing");
        coursesEveningBsc.put("CSE222", "Object Oriented Programming");
        coursesEveningBsc.put("CSE222L", "Object Oriented Programming Lab");
        coursesEveningBsc.put("MAT211", "Mathematics-IV : Engineering Mathematics");
        coursesEveningBsc.put("STA223", "Statistics");
        coursesEveningBsc.put("CSE231", "Microprocessor and Assembly Language");
        coursesEveningBsc.put("CSE231L", "Microprocessor and assembly Language Lab");
        coursesEveningBsc.put("CSE224", " Electronic Devices and Circuits");
        coursesEveningBsc.put("CSE224L", "Electronic Devices and Circuits Lab");
        coursesEveningBsc.put("CSE232", "Instrumentation and Control");
        coursesEveningBsc.put("CSE233", "Data Communication");
        coursesEveningBsc.put("CSE322", "Computer Architecture and Organization");
        coursesEveningBsc.put("CSE313", "Computer Networks");
        coursesEveningBsc.put("CSE313L", "Computer Networks Lab");
        coursesEveningBsc.put("CSE311", "Database Management System");
        coursesEveningBsc.put("CSE311L", "Database Management System Lab");
        coursesEveningBsc.put("CSE312", "Numerical Methods");
        coursesEveningBsc.put("CSE331", "Compiler Design");
        coursesEveningBsc.put("CSE331L", "Compiler Design Lab");
        coursesEveningBsc.put("CSE413", "Simulation and Modeling");
        coursesEveningBsc.put("CSE413L", "Simulation and Modeling Lab");
        coursesEveningBsc.put("CSE323", "Operating System");
        coursesEveningBsc.put("CSE323L", "Operating System Lab");
        coursesEveningBsc.put("CSE321", "System Analysis and Design");
        coursesEveningBsc.put("CSE321L", "System Analysis and Design Lab");
        coursesEveningBsc.put("CSE421", "Computer Graphics");
        coursesEveningBsc.put("CSE421L", "Computer Graphics Lab");
        coursesEveningBsc.put("CSE431", "E-Commerce & Web Application");
        coursesEveningBsc.put("MGT414", "Industrial Management");
        coursesEveningBsc.put("CSE412", "Artificial Intelligence");
        coursesEveningBsc.put("CSE412L", "Artificial Intelligence Lab");
        coursesEveningBsc.put("CSE411", "Communication Engineering");
        coursesEveningBsc.put("CSE332", "Software Engineering");
        coursesEveningBsc.put("CSE333", "Peripherals & Interfacing");
        coursesEveningBsc.put("CSE432", "Computer and Network Security");

        return coursesEveningBsc;
    }

    /*public static LinkedHashMap<String, String> getCoursesEveningMsc() {

        LinkedHashMap<String, String> coursesEveningMsc = new LinkedHashMap<>();

        coursesEveningMsc.put("CSE131", "Discrete Mathematics");
        coursesEveningMsc.put("CSE133", "Data Structures with Lab");
        coursesEveningMsc.put("CSE212", "Digital Logic Design with Lab");
        coursesEveningMsc.put("CSE221", "Theory of Computing");
        coursesEveningMsc.put("CSE222", "Object-oriented Programming with Lab");
        coursesEveningMsc.put("CSE233", "Data Communication");
        coursesEveningMsc.put("CSE311", "Database Management System with Lab");
        coursesEveningMsc.put("CSE321", "Systems Analysis and Design");
        coursesEveningMsc.put("CSE322", "Computer Architecture and Organization with Lab");
        coursesEveningMsc.put("CSE323", "Operating Systems with Lab");
        coursesEveningMsc.put("CSE331", "Compiler Design with Lab");
        coursesEveningMsc.put("CSE501", "Advanced DBMS");
        coursesEveningMsc.put("CSE502", "Advanced Artificial Intelligence");
        coursesEveningMsc.put("CSE503", "Advanced Computer Architecture");
        coursesEveningMsc.put("CSE504", "Software Development Methodology");
        coursesEveningMsc.put("CSE505", "High-speed Computer Networks");
        coursesEveningMsc.put("CSE506", "Microprocessor and Microcomputers");
        coursesEveningMsc.put("CSE507", "Advanced Graph Theory");
        coursesEveningMsc.put("CSE601", "Computational Geometry");
        coursesEveningMsc.put("CSE602", "Parallel and Distributed Systems");
        coursesEveningMsc.put("CSE603", "Object Oriented Analysis and Design");
        coursesEveningMsc.put("CSE604", "Speech and Language Processing");
        coursesEveningMsc.put("CSE605", "Machine Translation");
        coursesEveningMsc.put("CSE606", "Cryptography and Information Security");
        coursesEveningMsc.put("CSE607", "Distributed Database System");
        coursesEveningMsc.put("CSE608", "Wireless and Mobile Systems");
        coursesEveningMsc.put("CSE609", "Computer Graphics & Visualization");
        coursesEveningMsc.put("CSE610", "Electronic Commerce");
        coursesEveningMsc.put("CSE611", "Web Programming");
        coursesEveningMsc.put("CSE612", "Image Processing");
        coursesEveningMsc.put("CSE613", "Embedded System Design");
        coursesEveningMsc.put("CSE614", "Parallel Algorithms");
        coursesEveningMsc.put("CSE615", "Advanced Digital Signal Processing");
        coursesEveningMsc.put("CSE616", "Software Analysis and Design");
        coursesEveningMsc.put("CSE617", "Advanced Optical Communication Systems");
        coursesEveningMsc.put("CSE618", "Software Engineering Research Method");
        coursesEveningMsc.put("CSE619", "Computer Systems Verification");
        coursesEveningMsc.put("CSE620", "Software Project Management");
        coursesEveningMsc.put("CSE621", "Machine Learning Technique");
        coursesEveningMsc.put("CSE622", "Interactive Multimedia Design and Development");

        return coursesEveningMsc;
    }*/

    public static String getCourseNameFromCourseCode(String shift, String courseCode) {

        if (shift.equals(SHIFT_DAY)) {

            switch (courseCode)
            {
                case "CSE112" :
                    return "Computer Fundamentals";
                case "MAT111" :
                    return "Basic Mathematics";
                case "ENG113" :
                    return "Basic Functional English and English Spoken";
                case "PHY113" :
                    return "Basic Physics";
                case "PHY114" :
                    return "Basic Physics Lab";
                case "GED111" :
                    return "History of Bangladesh and Bangla Language";
                case "MAT121" :
                    return "Complex Variable, linear Algebra and Coordinate Geometry";
                case "CSE122" :
                    return "Programming and Problem Solving";
                case "CSE123" :
                    return "Problem Solving Lab";
                case "ENG123" :
                    return "Writing and Comprehension";
                case "CSE124" :
                    return "Business Application Design";
                case "GED121" :
                    return "Bangladesh Studies";
                case "CSE131" :
                    return "Discrete Mathematics";
                case "CSE132" :
                    return "Electrical Circuits";
                case "CSE133" :
                    return "Electrical Circuits Lab";
                case "CSE134" :
                    return "Data Structure";
                case "CSE135" :
                    return "Data Structure Lab";
                case "CSE136" :
                    return "Software Project I";
                case "GED131" :
                    return "Art of Living";
                case "MAT211" :
                    return "Engineering Mathematics";
                case "CSE212" :
                    return "Basic Electronics";
                case "CSE213" :
                    return "Basic Electronics Lab";
                case "CSE221-0" :
                    return "Object Oriented Programming";
                case "CSE222-0" :
                    return "Object Oriented Programming Lab";
                case "CSE216" :
                    return "Software Project II";
                case "ACT211" :
                    return "Financial and managerial Accounting ";
                case "CSE221" :
                    return "Algorithms";
                case "CSE222" :
                    return "Algorithms Lab";
                case "STA133" :
                    return "Statistics and Probability";
                case "CSE224" :
                    return "Electronic Devices and Circuits";
                case "CSE225" :
                    return "Electronic Devices and Circuits Lab";
                case "CSE231" :
                    return "Microprocessor and Assembly Language";
                case "CSE232" :
                    return "Microprocessor and Assembly Language Lab";
                case "CSE233" :
                    return "Data Communication";
                case "CSE234" :
                    return "Numerical Methods";
                case "CSE235" :
                    return "Introduction to Bio-Informatics";
                case "CSE311" :
                    return "Database Management System";
                case "CSE312" :
                    return "Database Management System Lab";
                case "CSE313" :
                    return "Computer Networks";
                case "CSE314" :
                    return "Computer Networks Lab";
                case "GED321" :
                    return "Art of Living";
                case "CSE321" :
                    return "System Analysis and Design";
                case "CSE322" :
                    return "Computer Architecture and Organization";
                case "CSE323" :
                    return "Operating Systems";
                case "CSE324" :
                    return "Operating Systems Lab";
                case "ECO314" :
                    return "Economics";
                case "CSE331" :
                    return "Compiler Design";
                case "CSE332" :
                    return "Compiler Design Lab";
                case "CSE333" :
                    return "Software Engineering";
                case "CSE334" :
                    return "Wireless Programming";
                case "ACT301" :
                    return "Financial and Managerial Accounting";
                case "CSE412" :
                    return "Artificial Intelligence";
                case "CSE413" :
                    return "Artificial Intelligence Lab";
                case "CSE414" :
                    return "Simulation and Modelling";
                case "CSE415" :
                    return "Simulation and Modelling Lab";
                case "CSE417" :
                    return "Web Engineering";
                case "CSE418" :
                    return "Web Engineering Lab";
                case "CSE421" :
                    return "Computer Graphics";
                case "CSE422" :
                    return "Computer Graphics Lab";
                case "CSE423" :
                    return "Embedded Systems";
                case "CSE450" :
                    return "Data Mining";
                case "CSE444" :
                    return "Introduction to Robotics";
                case "CSE498" :
                    return "Social and Professional Issues in Computing";
                case "CSE499" :
                    return "Project / Internship (Phase I, to be completed in Level-4 Term-3";
            }

        } else if (shift.equals(SHIFT_EVENING)) {

            switch (courseCode) {
                case "MAT121":
                    return "Mathematics II: Linear algebraand Coordinate Geometry";
                case "CSE131":
                    return "Discrete Mathematics";
                case "ENG113":
                    return "English Language-I";
                case "PHY123":
                    return "Physics- II: Electricity, Magnetism and Modern Physics";
                case "PHY123L":
                    return "Physics-II Lab";
                case "CSE213":
                    return "Algorithms";
                case "CSE213L":
                    return "Algorithm Lab";
                case "ACC214":
                    return "Accounting";
                case "ECO314":
                    return "Economics";
                case "MAT134":
                    return "Mathematics III : Ordinary and PartialDifferential Equations";
                case "CSE221":
                    return "Theory of Computing";
                case "CSE222":
                    return "Object Oriented Programming";
                case "CSE222L":
                    return "Object Oriented Programming Lab";
                case "MAT211":
                    return "Mathematics-IV : Engineering Mathematics";
                case "STA223":
                    return "Statistics";
                case "CSE231":
                    return "Microprocessor and Assembly Language";
                case "CSE231L":
                    return "Microprocessor and assembly Language Lab";
                case "CSE224":
                    return " Electronic Devices and Circuits";
                case "CSE224L":
                    return "Electronic Devices and Circuits Lab";
                case "CSE232":
                    return "Instrumentation and Control";
                case "CSE233":
                    return "Data Communication";
                case "CSE322":
                    return "Computer Architecture and Organization";
                case "CSE313":
                    return "Computer Networks";
                case "CSE313L":
                    return "Computer Networks Lab";
                case "CSE311":
                    return "Database Management System";
                case "CSE311L":
                    return "Database Management System Lab";
                case "CSE312":
                    return "Numerical Methods";
                case "CSE331":
                    return "Compiler Design";
                case "CSE331L":
                    return "Compiler Design Lab";
                case "CSE413":
                    return "Simulation and Modeling";
                case "CSE413L":
                    return "Simulation and Modeling Lab";
                case "CSE323":
                    return "Operating System";
                case "CSE323L":
                    return "Operating System Lab";
                case "CSE321":
                    return "System Analysis and Design";
                case "CSE321L":
                    return "System Analysis and Design Lab";
                case "CSE421":
                    return "Computer Graphics";
                case "CSE421L":
                    return "Computer Graphics Lab";
                case "CSE431":
                    return "E-Commerce & Web Application";
                case "MGT414":
                    return "Industrial Management";
                case "CSE412":
                    return "Artificial Intelligence";
                case "CSE412L":
                    return "Artificial Intelligence Lab";
                case "CSE411":
                    return "Communication Engineering";
                case "CSE332":
                    return "Software Engineering";
                case "CSE333":
                    return "Peripherals & Interfacing";
                case "CSE432":
                    return "Computer and Network Security";
            }
        }
        return "";
    }

}
