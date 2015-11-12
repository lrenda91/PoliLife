package it.polito.mad.polilife.db;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import it.polito.mad.polilife.db.classes.*;

/**
 * Created by luigi on 29/05/15.
 */
public class DBCallbacks {

    private DBCallbacks(){}

    public interface UserLoginCallback {
        void onStudentLoginSuccess(ParseUser student);
        void onLoginError(ParseException exception);
    }

    public interface UserLogoutCallback {
        void onLogoutSuccess();
        void onLogoutError(ParseException exception);
    }

    public interface StudentSignUpCallback {
        void onStudentSignUpSuccess(Student student);
        void onStudentSignUpException(ParseException exception);
    }

    public interface UpdateCallback<T extends ParseObject> {
        void onUpdateSuccess(T updated);
        void onUpdateError(ParseException exception);
    }

    public interface DeleteCallback<T extends ParseObject> {
        void onDeleteSuccess();
        void onDeleteError(ParseException exception);
    }

    public interface DownloadCallback<T extends ParseObject> {
        void onNewDataReturned(List<T> newData);
        void onDownloadError();
    }

    public interface FilterCallback<T extends ParseObject> {
        void onDataFiltered(List<T> result);
        void onFilterError(ParseException exception);
    }

    public interface SortCallback<T extends ParseObject> {
        void done(List<T> result);
        void onSortError(ParseException exception);
    }

    public interface SingleFetchCallback<T extends ParseObject> {
        void onFetchSuccess(T result);
        void onFetchError(ParseException exception);
    }

    public interface MultipleFetchCallback<T extends ParseObject> {
        void onFetchSuccess(List<T> result);
        void onFetchError(ParseException exception);
    }

    public interface NoticeFlagCallback {
        void onFlagSuccess();
        void onFlagError(ParseException exception);
    }

    public interface SetFavoriteCallback {
        void onSetFavoriteSuccess(boolean added);
        void onSetFavoriteError(ParseException exception);
    }

    public interface NewGroupCallback {
        void onNewGroupCreated(Group group);
        void onError(ParseException exception);
    }

}
