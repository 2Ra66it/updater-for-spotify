package ru.ra66it.updaterforspotify.presentation.utils;

import ru.ra66it.updaterforspotify.UpdaterApp;

/**
 * Created by 2Rabbit on 08.03.2018.
 */

public class StringService {

    public static String getById(int id) {
        if (UpdaterApp.getContext() != null) {
            return UpdaterApp.getContext().getResources().getString(id);
        } else {
            throw new NullPointerException();
        }
    }
}
