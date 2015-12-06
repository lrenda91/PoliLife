package it.polito.mad.polilife.noticeboard;

import java.util.List;

import it.polito.mad.polilife.db.classes.Notice;

/**
 * Created by luigi on 06/12/15.
 */
public interface NoticesListener {

    void update(List<Notice> notices);

}
