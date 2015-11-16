package it.polito.mad.polilife.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.*;

import java.util.LinkedList;
import java.util.List;

import it.polito.mad.polilife.db.classes.*;
import it.polito.mad.polilife.db.parcel.*;
import it.polito.mad.polilife.db.DBCallbacks.*;

/**
 * Created by Luigi on 27/10/2015.
 */
public class PoliLifeDB {

    private PoliLifeDB(){}

    private static final String APPLICATION_ID = "UWm5ltXVvvea4XrTKFFYX63GbuOX90WS4ec8hZg5";
    private static final String CLIENT_KEY = "4HPCLcygPRGkVEImYdsDYUFi8A0zXeZF80pPEYtf";

    public static final String STUDENT_KEY = "student";

    public static final String NOTICES = "notices";

    public static void initialize(Context context, Class<? extends ParseObject>... classesToRegister) {
        if (context == null) {
            throw new RuntimeException("context must be non null");
        }
        for (Class cl : classesToRegister) {
            ParseObject.registerSubclass(cl);
        }
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }

    public static void signUpStudent(final PUserData userData,
                                     final PStudentData studentData,
                                     final StudentSignUpCallback listener) {
        final Student newUser = userData.build();
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onStudentSignUpException(e);
                    return;
                }
                if (studentData == null) {
                    if (listener != null) {
                        listener.onStudentSignUpSuccess(newUser);
                    }
                    return;
                }
                final StudentInfo info = studentData.build();
                info.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) {
                                listener.onStudentSignUpException(e);
                            }
                            return;
                        }
                        newUser.setStudentInfo(info);
                        newUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    if (listener != null) {
                                        listener.onStudentSignUpException(e);
                                    }
                                    return;
                                }
                                listener.onStudentSignUpSuccess(newUser);
                            }
                        });
                    }
                });
            }
        });
    }


    public static ParseUser tryLocalLogin() {
        ParseUser current = ParseUser.getCurrentUser();
        if (current == null || !(current instanceof Student)){
            return null;
        }
        Student student = (Student) current;
        if (student.getStudentInfo() == null){
            return null;
        }
        StudentInfo info = student.getStudentInfo();
        try {
            info.fetchFromLocalDatastore();
        } catch (ParseException e) {
            return null;
        }
        return current;
    }

    public static void remoteLogIn(final String username, final String password,
                                   final UserLoginCallback listener) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null || !(parseUser instanceof Student) ||
                        ((Student) parseUser).getStudentInfo() == null) {
                    if (listener != null) {
                        listener.onLoginError((e != null) ? e : new ParseException(-1, "No login"));
                    }
                    return;
                }
                Student student = (Student) parseUser;
                ParseObject studentInfo = student.getStudentInfo();
                try {
                    student.fetchIfNeeded();
                    studentInfo.fetchIfNeeded();
                    student.pin();
                } catch (ParseException ex) {
                    if (listener != null) {
                        listener.onLoginError(ex);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onStudentLoginSuccess(parseUser);
                }
            }
        });
    }

    public static void logOut(final UserLogoutCallback listener) {
        ParseUser current = ParseUser.getCurrentUser();
        if (current == null){
            if (listener != null){
                listener.onLogoutError(new ParseException(-1, "No current parse user"));
            }
            return;
        }
        try {
            current.unpin();
        }
        catch (ParseException e) {
            if (listener != null) {
                listener.onLogoutError(e);
            }
        }
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (listener != null) {
                    if (e != null) {
                        listener.onLogoutError(e);
                    } else {
                        listener.onLogoutSuccess();
                    }
                }
            }
        });
    }


    /**
     * Updates student data
     * @param data The parcelable user data. For each field you must change, its content will be
     *             not null
     * @param listener
     */
    public static void updateStudentData(PStudentData data, final UpdateCallback<StudentInfo> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(STUDENT_KEY);
        if (po == null || !(po instanceof StudentInfo)) throw new AssertionError();
        final StudentInfo currentStudent = (StudentInfo) po;
        if (data.getAvailableFrom() != null) currentStudent.setAvailableStart(data.getAvailableFrom());
        if (data.getAvailableTo() != null) currentStudent.setAvailableEnd(data.getAvailableTo());
        currentStudent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onUpdateError(e);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onUpdateSuccess(currentStudent);
                }
            }
        });
    }


    public static void searchClassrooms(String param, final ClassroomSearchCallback listener){
        ParseQuery<Classroom> query = ParseQuery.getQuery(Classroom.class);
        query.whereContains(Classroom.NAME, param);
        query.findInBackground(new FindCallback<Classroom>() {
            @Override
            public void done(List<Classroom> list, ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onClassroomSearchError(e);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onClassroomsFound(list);
                }
            }
        });
    }

    public static void downloadProfessorsInfo(final MultipleFetchCallback<Professor> listener){
        ParseQuery<Professor> query = ParseQuery.getQuery(Professor.class);
        query.findInBackground(new FindCallback<Professor>() {
            @Override
            public void done(List<Professor> list, ParseException e) {
                if (e != null){
                    if (listener != null) listener.onFetchSuccess(list);
                    return;
                }
                if (listener != null) listener.onFetchError(e);
            }
        });
    }


    public static void publishNewNotice(final PNoticeData data,
                                        final UpdateCallback<Notice> listener){
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject po = (ParseObject) currentUser.get(STUDENT_KEY);
        if (po == null || !(po instanceof StudentInfo)) throw new AssertionError();
        final Notice built = data.build();

        new AsyncTask<Void,Void,ParseException>() {
            @Override
            protected ParseException doInBackground(Void[] params) {
                List<ParseFile> photoFiles = new LinkedList<>();
                for (PFileData fileData : data.getPhotos()){
                    ParseFile file = fileData.build();
                    try{
                        file.save();
                    }catch(ParseException e){
                        Log.e("pe", e.getMessage());
                        return e;
                    }
                    photoFiles.add(file);
                }
                built.addPhotos(photoFiles);
                return null;
            }
            @Override
            protected void onPostExecute(ParseException e) {
                super.onPostExecute(e);
                if (e == null){
                    built.setOwner(currentUser);
                    built.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                if (listener != null) listener.onUpdateError(e);
                                return;
                            }
                            currentUser.getRelation(NOTICES).add(built);
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null){
                                        if (listener != null) listener.onUpdateError(e);
                                        return;
                                    }
                                    if (listener != null) listener.onUpdateSuccess(built);
                                }
                            });
                        }
                    });
                }
            }
        }.execute();
    }

}
