package ru.ra66it.updaterforspotify.utils;

import ru.ra66it.updaterforspotify.MyApplication;

/**
 * Created by 2Rabbit on 08.03.2018.
 */

public class StringService {

    public static String getById(int id) {
        if (MyApplication.getContext() != null) {
            return MyApplication.getContext().getResources().getString(id);
        } else {
            throw new NullPointerException();
        }
    }
}
