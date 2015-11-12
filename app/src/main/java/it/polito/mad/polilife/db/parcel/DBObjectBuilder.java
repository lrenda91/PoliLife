package it.polito.mad.polilife.db.parcel;

/**
 * Created by luigi on 09/06/15.
 */
public interface DBObjectBuilder<T> {

    T build();
    void fillFrom(T obj);

}
