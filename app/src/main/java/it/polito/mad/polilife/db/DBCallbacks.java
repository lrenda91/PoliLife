package it.polito.mad.polilife.db;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import it.polito.mad.polilife.db.classes.*;

/**
 * Created by luigi onSelectAppliedJobs 29/05/15.
 */
public class DBCallbacks {

    private DBCallbacks(){}

    public interface UserLoginCallback {
        void onStudentLoginSuccess(ParseUser student);
        void onLoginError(Exception exception);
    }

    public interface UserLogoutCallback {
        void onLogoutSuccess();
        void onLogoutError(Exception exception);
    }

    public interface StudentSignUpCallback {
        void onStudentSignUpSuccess(Student student);
        void onStudentSignUpException(Exception exception);
    }

    public interface UpdateCallback<T extends ParseObject> {
        void onUpdateSuccess(T updated);
        void onUpdateError(Exception exception);
    }

    public interface DeleteCallback<T extends ParseObject> {
        void onDeleteSuccess(T obj);
        void onDeleteError(Exception exception);
    }

    public interface DownloadCallback<T extends ParseObject> {
        void onNewDataReturned(List<T> newData);
        void onDownloadError();
    }

    public interface SortCallback<T extends ParseObject> {
        void done(List<T> result);
        void onSortError(Exception exception);
    }

    public interface GetOneCallback<T extends ParseObject> {
        void onFetchSuccess(T result);
        void onFetchError(Exception exception);
    }

    public interface GetListCallback<T extends ParseObject> {
        void onFetchSuccess(List<T> result);
        void onFetchError(Exception exception);
    }

    public interface NoticeFlagCallback {
        void onFlagSuccess();
        void onFlagError(Exception exception);
    }

    public interface SetFavoriteCallback {
        void onSetFavoriteSuccess(boolean added);
        void onSetFavoriteError(Exception exception);
    }
    
    public interface ClassroomSearchCallback {
        void onClassroomsFound(List<Classroom> result);
        void onClassroomSearchError(Exception exception);
    }

}
