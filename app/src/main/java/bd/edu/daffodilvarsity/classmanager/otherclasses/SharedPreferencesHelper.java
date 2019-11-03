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

    public SharedPreferencesHelper(Context context) {

    }

    public static void setUserType(Context context, String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HelperClass.USER_TYPE, type).apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.USER_TYPE, "");
    }

    public static void removeUserTypeFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        sharedPreferences.edit().remove(HelperClass.USER_TYPE).apply();
    }

    public static String getTeacherInitialFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.TEACHER_INITIAL, "");
    }

    public static void saveTeacherInitialToSharedPref(Context context, String initial) {

        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);

            sharedPreferences.edit().putString(HelperClass.TEACHER_INITIAL, initial).apply();
        }

    }

    public static void saveRoutineVersionToSharedPreferences(Context context, String version) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(HelperClass.ROUTINE_VERSION_UPDATE, version);
            editor.apply();
        }
    }

    public static String getRoutineVersionFromSharedPreferences(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

            return sharedPreferences.getString(HelperClass.ROUTINE_VERSION_UPDATE, "00000000");
        } else {
            return "00000";
        }
    }

    public static HashMap<String, String> getCoursesAndSectionMapFromSharedPreferences(Context context) {

        HashMap<String, String> courseHashMap;

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String coursesInJson = sharedPreferences.getString(HelperClass.COURSES_HASH_MAP, null);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        courseHashMap = gson.fromJson(coursesInJson, type);

        return courseHashMap;
    }

    public static void addNewCourseOnSharedPreference(Context context, String courseCode, String section) {

        HashMap<String, String> courseHashMap = getCoursesAndSectionMapFromSharedPreferences(context);

        courseHashMap.put(courseCode, section);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

    }

    public static String getShiftFromSharedPreferences(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);

        return sharedPreferences.getString(HelperClass.SHIFT, "");
    }

    public static void deleteCourseFromSharedPref(Context context, String courseCode) {

        HashMap<String, String> courseHashMap = getCoursesAndSectionMapFromSharedPreferences(context);

        courseHashMap.remove(courseCode);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

    }

    public static void saveCourseWithSharedPreference(Context context, String program, String shift, String level, String term, String section) {

        if (context == null) {
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

    public static void removeCoursesFromSharedPref(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(HelperClass.COURSES_HASH_MAP).apply();

    }

    public static void saveUserEmail(Context context, String email) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(HelperClass.USER_EMAIL, email).apply();

    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.USER_EMAIL, null);
    }

    public static void saveStudentProfileOffline(Context context, ProfileObjectStudent profile) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String profileJsonString = gson.toJson(profile);

        sharedPreferences.edit().putString(HelperClass.STUDENT_PROFILE, profileJsonString).apply();

    }

    public static ProfileObjectStudent getStudentOfflineProfile(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        String profileJsonString = sharedPreferences.getString(HelperClass.STUDENT_PROFILE, null);

        Gson gson = new Gson();
        Type type = new TypeToken<ProfileObjectStudent>() {
        }.getType();

        ProfileObjectStudent studentProfile = gson.fromJson(profileJsonString, type);

        return studentProfile;

    }

    public static void removeStudentProfileFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        sharedPreferences.edit().remove(HelperClass.STUDENT_PROFILE).apply();
    }

    public static ProfileObjectTeacher getTeacherOfflineProfile(Context context) {

        Gson gson = new Gson();
        Type type = new TypeToken<ProfileObjectTeacher>() {
        }.getType();

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        String teacherProfileJson = sharedPreferences.getString(HelperClass.TEACHER_PROFILE, null);

        if (teacherProfileJson == null) {
            return null;
        } else {
            ProfileObjectTeacher profile = gson.fromJson(teacherProfileJson,type);
            return profile;
        }
    }

    public static void removeTeacherOfflineProfile(Context context)  {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        sharedPreferences.edit().remove(HelperClass.TEACHER_PROFILE).apply();
    }

    public static void saveTeacherProfileToSharedPref(Context context , ProfileObjectTeacher profile)    {
        if(context!=null)   {
            Gson gson = new Gson();

            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);
            sharedPreferences.edit().putString(HelperClass.TEACHER_PROFILE,gson.toJson(profile)).apply();
        }
    }

    public static void removeTeacherProfileFromSharedPref(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);
        sharedPreferences.edit().remove(HelperClass.TEACHER_PROFILE).apply();

    }

    public static void enableStudentNotification(Context context , boolean status) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(HelperClass.STUDENT_NOTIFICATION_ENABLED , status).apply();
        }
    }

    public static boolean getStudentNotificatinStatus(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);
            return sharedPreferences.getBoolean(HelperClass.STUDENT_NOTIFICATION_ENABLED , false);
        }
        return false;
    }
}
