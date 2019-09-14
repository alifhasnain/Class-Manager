package bd.edu.daffodilvarsity.classmanager.otherclasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {

    public String getUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.USER_TYPE, "");
    }

    public String getTeacherInitialFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.TEACHER_INITIAL, "");
    }

    public void saveTeacherInitialToSharedPref(Context context, String initial) {

        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);

            sharedPreferences.edit().putString(HelperClass.TEACHER_INITIAL, initial).apply();
        }

    }

    public void saveRoutineVersionToSharedPreferences(Context context, String version) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(HelperClass.ROUTINE_VERSION, version);
            editor.apply();
        }
    }

    public String getRoutineVersionFromSharedPreferences(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

            return sharedPreferences.getString(HelperClass.ROUTINE_VERSION, "00000000");
        } else {
            return "00000000";
        }
    }

    public HashMap<String, String> getCoursesAndSectionMapFromSharedPreferences(Context context) {

        HashMap<String, String> courseHashMap;

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String coursesInJson = sharedPreferences.getString(HelperClass.COURSES_HASH_MAP, null);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        courseHashMap = gson.fromJson(coursesInJson, type);

        return courseHashMap;
    }

    public void addNewCourseOnSharedPreference(Context context, String courseCode, String section) {

        HashMap<String, String> courseHashMap = getCoursesAndSectionMapFromSharedPreferences(context);

        courseHashMap.put(courseCode, section);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

    }

    public String getShiftFromSharedPreferences(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);

        return sharedPreferences.getString(HelperClass.SHIFT, "");
    }

    public void deleteCourseFromSharedPref(Context context, String courseCode) {

        HashMap<String, String> courseHashMap = getCoursesAndSectionMapFromSharedPreferences(context);

        courseHashMap.remove(courseCode);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

    }

    public static void saveCourseWithSharedPreference(Context context,String program, String shift, String level, String term, String section) {

        if(context==null)   {
            return;
        }

        HelperClass helperClass = HelperClass.getInstance();

        ArrayList<String> coursesList = helperClass.getCourseList(program, shift, level, term);

        HashMap<String, String> coursesMap = new HashMap<>();

        for (String courseCode : coursesList) {
            coursesMap.put(courseCode, section);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(coursesMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();
    }
}
