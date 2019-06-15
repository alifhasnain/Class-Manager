package bd.edu.daffodilvarsity.classmanager.otherclasses;

import java.util.ArrayList;

public class HelperClass {

    public static final String SHARED_PREFERENCE_TAG = "shared_preferences";

    public static final String LEVEL = "level";

    public static final String TERM = "term";

    public static final String COURSES_HASH_MAP = "course_list";

    public static final String USER_TYPE = "user_type";

    public static final String USER_TYPE_STUDENT = "user_type_student";

    public static final String USER_TYPE_TEACHER = "user_type_teacher";

    public static final String USER_TYPE_ADMIN = "user_type_admin";

    public ArrayList<String> getCourseList(String level, String term) {
        switch (level) {
            case "Level 1":
                switch (term) {
                    case "Term 1":
                        return L1T1();
                    case "Term 2":
                        return L1T2();
                    case "Term 3":
                        return L1T3();
                }
                break;
            case "Level 2":
                switch (term) {
                    case "Term 1":
                        return L2T1();
                    case "Term 2":
                        return L2T2();
                    case "Term 3":
                        return L2T3();
                }
                break;
            case "Level 3":
                switch (term) {
                    case "Term 1":
                        return L3T1();
                    case "Term 2":
                        return L3T2();
                    case "Term 3":
                        return L3T3();
                }
                break;
            case "Level 4":
                switch (term) {
                    case "Term 1":
                        return L4T1();
                    case "Term 2":
                        return L4T2();
                    case "Term 3":
                        return L4T3();
                }
                break;
        }
        return null;
    }

    private ArrayList<String> L1T1()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE112");
        courseCodes.add("MAT111");
        courseCodes.add("ENG113");
        courseCodes.add("PHY113");

        return courseCodes;
    }

    private ArrayList<String> L1T2()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT121");
        courseCodes.add("CSE122");
        courseCodes.add("CSE123");
        courseCodes.add("PHY123");
        courseCodes.add("PHY124");
        courseCodes.add("ENG123");

        return courseCodes;
    }

    private ArrayList<String> L1T3()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE131");
        courseCodes.add("CSE132");
        courseCodes.add("CSE133");
        courseCodes.add("CSE134");
        courseCodes.add("CSE135");
        courseCodes.add("MAT131");

        return courseCodes;
    }

    private ArrayList<String> L2T1()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("MAT211");
        courseCodes.add("CSE212");
        courseCodes.add("CSE213");
        courseCodes.add("CSE214");
        courseCodes.add("CSE215");
        courseCodes.add("ED201");

        return courseCodes;
    }

    private ArrayList<String> L2T2()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE221");
        courseCodes.add("CSE222");
        courseCodes.add("STA133");
        courseCodes.add("CSE224");
        courseCodes.add("CSE225");

        return courseCodes;
    }

    private ArrayList<String> L2T3()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE231");
        courseCodes.add("CSE232");
        courseCodes.add("CSE233");
        courseCodes.add("CSE234");
        courseCodes.add("CSE235");

        return courseCodes;
    }

    private ArrayList<String> L3T1()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE311");
        courseCodes.add("CSE312");
        courseCodes.add("CSE313");
        courseCodes.add("CSE314");
        courseCodes.add("GED321");

        return courseCodes;
    }

    private ArrayList<String> L3T2()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE321");
        courseCodes.add("CSE322");
        courseCodes.add("CSE323");
        courseCodes.add("CSE324");
        courseCodes.add("ECO314");

        return courseCodes;
    }

    private ArrayList<String> L3T3()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE331");
        courseCodes.add("CSE332");
        courseCodes.add("CSE333");
        courseCodes.add("CSE334");
        courseCodes.add("ACT301");

        return courseCodes;
    }

    private ArrayList<String> L4T1()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE412");
        courseCodes.add("CSE413");
        courseCodes.add("CSE414");
        courseCodes.add("CSE415");
        courseCodes.add("CSE417");
        courseCodes.add("CSE418");

        return courseCodes;
    }

    private ArrayList<String> L4T2()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE421");
        courseCodes.add("CSE422");
        courseCodes.add("CSE423");

        return courseCodes;
    }

    private ArrayList<String> L4T3()    {

        ArrayList<String> courseCodes = new ArrayList<>();

        courseCodes.add("CSE498");

        return courseCodes;
    }

}
