package ru.ra66it.updaterforspotify.ui.fragment.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ra66it.updaterforspotify.R;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroHelloFragment extends Fragment {

    public static IntroHelloFragment newInstance() {
        return new IntroHelloFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_hello_fragment, container, false);
        return v;
    }
}
