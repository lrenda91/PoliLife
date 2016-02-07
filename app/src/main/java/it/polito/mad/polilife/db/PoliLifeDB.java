package it.polito.mad.polilife.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.*;
import com.parse.DeleteCallback;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import it.polito.mad.polilife.MainActivity;
import it.polito.mad.polilife.db.classes.*;
import it.polito.mad.polilife.db.DBCallbacks.*;
import it.polito.mad.polilife.db.parcel.PFileData;
import it.polito.mad.polilife.db.parcel.PNoticeData;
import it.polito.mad.polilife.db.parcel.PStudentData;
import it.polito.mad.polilife.db.parcel.PUserData;

/**
 * Created by Luigi onSelectAppliedJobs 27/10/2015.
 */
public class PoliLifeDB {

    private PoliLifeDB(){}

    private static final String TAG = "ParseDB";

    private static final String APPLICATION_ID = "UWm5ltXVvvea4XrTKFFYX63GbuOX90WS4ec8hZg5";
    private static final String CLIENT_KEY = "4HPCLcygPRGkVEImYdsDYUFi8A0zXeZF80pPEYtf";

    public static final String STUDENT_KEY = "student";

    public static final String NOTICES = "notices";

    public static void initialize(final Context context, Class<? extends ParseObject>... classesToRegister) {
        if (context == null) {
            throw new RuntimeException("context must be non null");
        }
        for (Class cl : classesToRegister) {
            ParseObject.registerSubclass(cl);
        }
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void signUpStudent(final PUserData userData,
                                     final PStudentData studentData,
                                     final StudentSignUpCallback listener) {
        final Student newUser = userData.build();
        newUser.addChannel("public");
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
        student.setLastLogin(new Date());
        student.saveEventually();
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
                student.setLastLogin(new Date());
                student.saveEventually();
                ParseObject studentInfo = student.getStudentInfo();
                //prova
                ParseInstallation.getCurrentInstallation().put("user", student);
                ParseInstallation.getCurrentInstallation().saveEventually();
                //fine prova
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
                        ParseInstallation.getCurrentInstallation().put("user", JSONObject.NULL);
                        ParseInstallation.getCurrentInstallation().saveInBackground();
                        listener.onLogoutSuccess();
                    }
                }
            }
        });
    }


    /**

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
    */

    public static void searchClassrooms(String param, boolean exactMatch, boolean fromCache,
                                        final GetListCallback<Classroom> listener){
        ParseQuery<Classroom> query = ParseQuery.getQuery(Classroom.class);
        if (param != null) {
            if (exactMatch) query.whereEqualTo(Classroom.NAME, "Aula " + param);
            else query.whereContains(Classroom.NAME, param);
        }
        if (fromCache) query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Classroom>() {
            @Override
            public void done(List<Classroom> list, ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onFetchError(e);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onFetchSuccess(list);
                }
            }
        });
    }

    public static <T extends ParseObject> void getAllObjects(
            Class<T> klass, final GetListCallback<T> listener){
        ParseQuery<T> query = ParseQuery.getQuery(klass);
        if (klass == Student.class){
            query.whereNotEqualTo(Student.STUDENT_KEY, null);
        }
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

    public static void publishNewHomeNotice(final PNoticeData data,
                                        final UpdateCallback<Notice> listener){
        publishNewNotice(data, Notice.HOME_TYPE, listener);
    }

    public static void publishNewBookNotice(final PNoticeData data,
                                            final UpdateCallback<Notice> listener){
        publishNewNotice(data, Notice.BOOK_TYPE, listener);
    }

    private static void publishNewNotice(final PNoticeData data, final String type,
                                        final UpdateCallback<Notice> listener){
        new AsyncTask<Void,Void,List<ParseFile>>() {
            @Override
            protected List<ParseFile> doInBackground(Void[] params) {
                List<ParseFile> photoFiles = new LinkedList<>();
                for (PFileData fileData : data.getPhotos()) {
                    ParseFile file = fileData.build();
                    try {
                        file.save();
                    } catch (ParseException e) {
                        Log.e("pe", e.getMessage());
                        return null;
                    }
                    photoFiles.add(file);
                }
                return photoFiles;
            }
            @Override
            protected void onPostExecute(List<ParseFile> photoFiles) {
                if (photoFiles == null){
                    if (listener != null) listener.onUpdateError(new Exception("Error saving photos"));
                    return;
                }
                final Notice built = data.build();
                built.addPhotos(photoFiles);
                built.setOwner(ParseUser.getCurrentUser());
                if (Notice.HOME_TYPE.equals(type)){
                    built.setHomeType();
                }
                else if (Notice.BOOK_TYPE.equals(type)){
                    built.setBookType();
                }
                else if (Notice.DIDACTICAL_TYPE.equals(type)){
                    built.setDidacticalType();
                }
                built.saveInBackground(new SaveCallback() {
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
        }.execute();
    }


    public static void deleteNotice(final Notice notice, final DBCallbacks.DeleteCallback<Notice> listener){
        notice.deleteEventually(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onDeleteError(e);
                    return;
                }
                if (listener != null) listener.onDeleteSuccess(notice);
            }
        });
    }

    public static void advancedNoticeFilter(final Notice.Filter filter,
                                            final GetListCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        ParseQuery<Notice> finalQuery = query;
        if (filter != null) {
            List<ParseQuery<Notice>> typeBasedQueries = new LinkedList<>();
            StringTokenizer st = new StringTokenizer(filter.type, ",");
            while (st.hasMoreTokens()) {
                String type = st.nextToken();
                typeBasedQueries.add(ParseQuery.getQuery(Notice.class).whereEqualTo(Notice.TYPE, type));
            }
            if (!typeBasedQueries.isEmpty()) {
                query = ParseQuery.or(typeBasedQueries);
            }

            if (filter.daysAgo > 0) {
                long DAY_IN_MS = 1000 * 60 * 60 * 24;
                Date limit = new Date(System.currentTimeMillis() - (filter.daysAgo * DAY_IN_MS));
                query.whereGreaterThanOrEqualTo(Notice.PUBLICATED_AT, limit);
            }
            if (filter.title != null){
                query.whereContains(Notice.TITLE, filter.title);
            }
            //DIDACTICAL NOTICES FILTERS
            if (filter.type.equals(Notice.DIDACTICAL_TYPE)){

            }
            else {

                //HOME NOTICES FILTERS
                if (filter.type.equals(Notice.HOME_TYPE)) {
                    if (filter.contractType != null) {
                        query.whereEqualTo(Notice.CONTRACT_TYPE, filter.contractType);
                    }
                    if (filter.propertyType != null) {
                        query.whereEqualTo(Notice.PROPERTY_TYPE, filter.propertyType);
                    }
                    query.whereLessThanOrEqualTo(Notice.SIZE, filter.maxSize)
                            .whereGreaterThanOrEqualTo(Notice.SIZE, filter.minSize);
                }
                //BOOK NOTICES FILTERS
                if (filter.type.equals(Notice.BOOK_TYPE)) {

                }

                //BOTH HOME AND BOOK NOTICES FILTERS
                query.whereLessThanOrEqualTo(Notice.PRICE, filter.maxPrice)
                        .whereGreaterThanOrEqualTo(Notice.PRICE, filter.minPrice);
                if (filter.tags != null && !filter.tags.isEmpty()) {
                    query.whereContainsAll(Notice.TAGS, filter.tags);
                }
                if (filter.location != null) {
                    query.whereContains(Notice.LOCATION_STRING, filter.location);
                }
                if (filter.latitude != null && filter.longitude != null){
                    ParseGeoPoint point = new ParseGeoPoint(filter.latitude, filter.longitude);
                    query.whereWithinKilometers(Notice.LOCATION_POINT, point, filter.within);
                }
                List<ParseQuery<Notice>> tagBasedQueries = new LinkedList<>();
                tagBasedQueries.add(query);
                for (String tag : filter.tags) {
                    List<ParseQuery<Notice>> queries = new LinkedList<>();
                    queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.TITLE, tag));
                    queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.DESCRIPTION, tag));
                    ParseQuery<Notice> union = ParseQuery.or(queries);
                    tagBasedQueries.add(union);
                }
                finalQuery = ParseQuery.or(tagBasedQueries);

            }

        }
        /*if (fromLocalDataStore){
            finalQuery.fromLocalDatastore();
        }*/
        finalQuery.addDescendingOrder(Notice.PUBLICATED_AT);
        finalQuery.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                /*if (!fromLocalDataStore) {
                    ParseObject.unpinAllInBackground("cachedNotices");
                    ParseObject.pinAllInBackground("cachedNotices", list);
                }
                */
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void getNewestDidacticalNotices(final GetListCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        query.whereEqualTo(Notice.TYPE, Notice.DIDACTICAL_TYPE);
        Date lastLogin = ((Student) ParseUser.getCurrentUser()).getLastLogin();
        query.whereGreaterThanOrEqualTo("createdAt", lastLogin);
        query.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void advancedJobsFilter(final Job.Filter filter,
                                          final boolean fromLocalDataStore,
                                          final GetListCallback<Job> listener) {
        ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
        if (filter != null){
            if (filter.name != null){
                query.whereContains(Job.NAME, filter.name);
            }
            if (filter.typeOfContract != null){
                query.whereEqualTo(Job.TYPE_OF_CONTRACT, filter.typeOfContract);
            }
            if (filter.typeOfDegree != null){
                query.whereEqualTo(Job.TYPE_OF_DEGREE, filter.typeOfDegree);
            }
            if (filter.city != null){
                query.whereEqualTo(Job.CITY, filter.city);
            }
            if (filter.startDate != null){
                query.whereGreaterThanOrEqualTo(Job.START_DATE, filter.startDate);
            }
        }
        query.addDescendingOrder(Job.START_DATE);
        if (fromLocalDataStore){
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (!fromLocalDataStore) {
                    ParseObject.unpinAllInBackground(getPinName(Job.class));
                    ParseObject.pinAllInBackground(getPinName(Job.class), list);
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void getRecentNoticesAndPositions(final int daysAgo,
            //final boolean fromLocalDataStore,
                                                    final GetListCallback<ParseObject> listener){
        final List<ParseObject> objs = new LinkedList<>();
        Notice.Filter filter = new Notice.Filter(Notice.HOME_TYPE);
        filter.daysAgo = daysAgo;
        advancedNoticeFilter(filter, new GetListCallback<Notice>() {
            @Override
            public void onFetchSuccess(List<Notice> result) {
                objs.addAll(result);
                ParseQuery<Job> query = ParseQuery.getQuery(Job.class);
                if (daysAgo > 0) {
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    Date limit = new Date(System.currentTimeMillis() - (daysAgo * DAY_IN_MS));
                    query.whereGreaterThanOrEqualTo(Job.START_DATE, limit);
                }
                /*if (fromLocalDataStore) {
                    query.fromLocalDatastore();
                }*/
                query.findInBackground(new FindCallback<Job>() {
                    @Override
                    public void done(List<Job> list, ParseException e) {
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

    public static <T extends ParseObject> void retrieveObject(final String id, final Class<T> klass,
            final GetOneCallback<T> listener){
        final T obj = ParseObject.createWithoutData(klass, id);
        obj.fetchFromLocalDatastoreInBackground(new GetCallback<T>() {
            @Override
            public void done(T parseObject, ParseException e) {
                if (e != null) {
                    obj.fetchInBackground(new GetCallback<T>() {
                        @Override
                        public void done(T parseObject, ParseException e) {
                            if (e != null) {
                                if (listener != null) listener.onFetchError(e);
                                return;
                            }
                            Log.d(TAG, "Found " + klass.getSimpleName() + "[" + id + "] online");
                            parseObject.pinInBackground(getPinName(klass));
                            if (listener != null) listener.onFetchSuccess(parseObject);
                        }
                    });
                    return;
                }
                Log.d(TAG, "Found " + klass.getSimpleName() + "[" + id + "] in local data store");
                if (listener != null) listener.onFetchSuccess(parseObject);
            }
        });
    }

    public static void getChatMembers(String channel, final GetListCallback<Student> listener){
        ParseQuery<Student> query = ParseQuery.getQuery(Student.class);
        query.whereContainsAll("channels", Arrays.asList(channel));
        query.findInBackground(new FindCallback<Student>() {
            @Override
            public void done(List<Student> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void getAllChatMembers(List<String> channels, final GetListCallback<Student> listener){
        List<ParseQuery<Student>> queriesBasedOnSingleChannels = new LinkedList<>();
        for (String ch : channels){
            queriesBasedOnSingleChannels.add(
                    new ParseQuery(Student.class).whereContainsAll("channels", Arrays.asList(ch))
            );
        }
        /**
         * This query deals with all students who have at least one of the param 'channels' values
         * inside their array named 'channels' (including myself)
         */
        ParseQuery<Student> users = ParseQuery.or(queriesBasedOnSingleChannels)
                .whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        users.findInBackground(new FindCallback<Student>() {
            @Override
            public void done(List<Student> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void apply(final Job job, final UpdateCallback<StudentInfo> listener) {
        final Student student = (Student) ParseUser.getCurrentUser();
        final StudentInfo info = student.getStudentInfo();
        info.addAppliedJob(job);
        info.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onUpdateError(e);
                    return;
                }
                /*Map<String, Object> map = new HashMap<>();
                map.put("job", job.getObjectId());
                ParseCloud.callFunctionInBackground("apply", map, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object response, ParseException exc) {
                        if (exc == null) {
                            job.pinInBackground(getPinName(Job.class));
                            if (listener != null) listener.onUpdateSuccess(info);
                        }
                    }
                });
                */
                ApplicationStatus newStatus = new ApplicationStatus();
                newStatus.setApplicant(student);
                newStatus.setJob(job);
                newStatus.setStatus(ApplicationStatus.STATUS_APPLIED);
                newStatus.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onUpdateError(e);
                            return;
                        }
                        job.pinInBackground(getPinName(Job.class));
                        if (listener != null) listener.onUpdateSuccess(info);
                    }
                });
            }
        });
    }

    public static void checkApplicationStatus(final Job job, final GetOneCallback<ApplicationStatus> listener) {
        final Student student = (Student) ParseUser.getCurrentUser();
        new ParseQuery(ApplicationStatus.class)
                .whereEqualTo(ApplicationStatus.JOB, job)
                .whereEqualTo(ApplicationStatus.APPLICANT, student)
                .findInBackground(new FindCallback<ApplicationStatus>() {
                    @Override
                    public void done(List<ApplicationStatus> list, ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onFetchError(e);
                            return;
                        }
                        if (list.isEmpty()){
                            if (listener != null) listener.onFetchError(e);
                            return;
                        }
                        if (listener != null) listener.onFetchSuccess(list.get(0));
                    }
                });
    }

    public static void getAppliedJobs(final GetListCallback<Job> listener){
        StudentInfo info = ((Student) ParseUser.getCurrentUser()).getStudentInfo();
        info.getAppliedJobs().getQuery().findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                ParseObject.pinAllInBackground(getPinName(Job.class), list);
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    public static void getCachedJobs(final GetListCallback<Job> listener){
        ParseQuery.getQuery(Job.class).fromPin(getPinName(Job.class))
                .findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }

    private static <T extends ParseObject> String getPinName(Class<T> klass){
        return "cache" + klass.getSimpleName(); 
    }

}
