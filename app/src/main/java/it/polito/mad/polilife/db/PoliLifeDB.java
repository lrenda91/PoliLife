package it.polito.mad.polilife.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.parse.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Filter;

import it.polito.mad.polilife.Utility;
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

    public static <T extends ParseObject> void getAllObjects(
            Class<T> klass, final MultipleFetchCallback<T> listener){
        ParseQuery<T> query = ParseQuery.getQuery(klass);
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
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

    public static void advancedNoticeFilter(final Notice.FilterData filterData,
                                            final boolean fromLocalDataStore,
                                            final MultipleFetchCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        ParseQuery<Notice> finalQuery = null;
        if (filterData != null) {
            query.whereLessThanOrEqualTo(Notice.PRICE, filterData.maxPrice)
                    .whereGreaterThanOrEqualTo(Notice.PRICE, filterData.minPrice)
                    .whereLessThanOrEqualTo(Notice.SIZE, filterData.maxSize)
                    .whereGreaterThanOrEqualTo(Notice.SIZE, filterData.minSize);
            if (filterData.daysAgo > 0) {
                long DAY_IN_MS = 1000 * 60 * 60 * 24;
                Date limit = new Date(System.currentTimeMillis() - (filterData.daysAgo * DAY_IN_MS));
                query.whereGreaterThanOrEqualTo(Notice.PUBLICATED_AT, limit);
            }
            if (filterData.type != null){
                query.whereEqualTo(Notice.TYPE, filterData.type);
            }
            if (filterData.title != null){
                query.whereContains(Notice.TITLE, filterData.title);
            }
            if (filterData.location != null) {
                query.whereContains(Notice.LOCATION_STRING, filterData.location);
            }
            if (filterData.latitude != null && filterData.longitude != null){
                ParseGeoPoint point = new ParseGeoPoint(filterData.latitude, filterData.longitude);
                query.whereWithinKilometers(Notice.LOCATION_POINT, point, filterData.within);
            }
            if (filterData.contractType != null) {
                query.whereEqualTo(Notice.CONTRACT_TYPE, filterData.contractType);
            }
            if (filterData.propertyType != null) {
                query.whereEqualTo(Notice.PROPERTY_TYPE, filterData.propertyType);
            }
            if (filterData.tags != null && !filterData.tags.isEmpty()) {
                query.whereContainsAll(Notice.TAGS, filterData.tags);
            }
            List<ParseQuery<Notice>> res = new LinkedList<>();
            res.add(query);
            for (String tag : filterData.tags) {
                List<ParseQuery<Notice>> queries = new LinkedList<>();
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.TITLE, tag));
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.DESCRIPTION, tag));
                ParseQuery<Notice> union = ParseQuery.or(queries);
                res.add(union);
            }
            finalQuery = ParseQuery.or(res);
        }
        else{
            finalQuery = query;
        }
        if (fromLocalDataStore){
            finalQuery.fromLocalDatastore();
        }
        finalQuery.addDescendingOrder(Notice.PUBLICATED_AT);
        finalQuery.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (!fromLocalDataStore) {
                    ParseObject.unpinAllInBackground("cachedNotices");
                    ParseObject.pinAllInBackground("cachedNotices", list);
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void getRecentNoticesAndPositions(final int daysAgo,
            final boolean fromLocalDataStore, final MultipleFetchCallback<ParseObject> listener){
        final List<ParseObject> objs = new LinkedList<>();
        Notice.FilterData filter = new Notice.FilterData();
        filter.daysAgo = daysAgo;
        advancedNoticeFilter(filter, fromLocalDataStore, new MultipleFetchCallback<Notice>() {
            @Override
            public void onFetchSuccess(List<Notice> result) {
                objs.addAll(result);
                ParseQuery<Position> query = ParseQuery.getQuery(Position.class);
                if (daysAgo > 0) {
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    Date limit = new Date(System.currentTimeMillis() - (daysAgo * DAY_IN_MS));
                    query.whereGreaterThanOrEqualTo(Position.START_DATE, limit);
                }
                if (fromLocalDataStore){
                    query.fromLocalDatastore();
                }
                query.findInBackground(new FindCallback<Position>() {
                    @Override
                    public void done(List<Position> list, ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onFetchError(e);
                            return;
                        }
                        objs.addAll(list);
                        if (listener != null) listener.onFetchSuccess(objs);
                    }
                });
            }

            @Override
            public void onFetchError(Exception exception) {
                if (listener != null) listener.onFetchError(exception);
            }
        });
    }

    public static <T extends ParseObject> void retrieveObject(String id, Class<T> klass,
            boolean fromLocalDataStore, final SingleFetchCallback<T> listener){
        T obj = ParseObject.createWithoutData(klass, id);
        if (fromLocalDataStore){
            obj.fetchFromLocalDatastoreInBackground(new GetCallback<T>() {
                @Override
                public void done(T parseObject, ParseException e) {
                    if (e != null){
                        if (listener != null) listener.onFetchError(e);
                        return;
                    }
                    if (listener != null) listener.onFetchSuccess(parseObject);
                }
            });
        }
        else {
            obj.fetchInBackground(new GetCallback<T>() {
                @Override
                public void done(T parseObject, ParseException e) {
                    if (e != null) {
                        if (listener != null) listener.onFetchError(e);
                        return;
                    }
                    if (listener != null) listener.onFetchSuccess(parseObject);
                }
            });
        }
    }

}
