package bd.edu.daffodilvarsity.classmanager.otherclasses;

import java.util.ArrayList;

public class HelperClass {

    public static final String SHARED_PREFERENCE_TAG = "shared_preferences";

    public static final String PROGRAM_BSC = "B.Sc. in CSE";

    public static final String PROGRAM_MSC = "M.Sc. in CSE";

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
                            return L2T3DayBsc();
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

}
