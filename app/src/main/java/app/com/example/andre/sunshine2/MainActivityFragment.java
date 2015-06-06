package app.com.example.andre.sunshine2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> dados = new ArrayList<String>();
        dados.add("Hoje - Ensolarado - 27/21");
        dados.add("Amanhã - Ensolarado - 27/21");
        dados.add("Segunda - Ensolarado - 27/21");
        dados.add("Terça - Ensolarado - 27/21");
        dados.add("Quarta - Ensolarado - 27/21");
        dados.add("Quinta - Ensolarado - 27/21");
        dados.add("Sexta - Ensolarado - 27/21");

        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_text, dados);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        listView.setAdapter(mForecastAdapter);

        return rootView;
    }
}
