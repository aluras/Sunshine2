package app.com.example.andre.sunshine2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.andre.sunshine2.data.WeatherContract;

/**
 * Created by sn1007071 on 30/09/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    private static final int DETAIL_LOADER = 0;
    private TextView weekday_text;
    private TextView date_text;
    private TextView high_text;
    private TextView low_text;
    private TextView weather_text;
    private TextView humidity_text;
    private TextView wind_text;
    private TextView pressure_text;
    private ImageView icon_image;
    private Uri mUri;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged(String newLocation){
        Uri uri = mUri;
        if (null != uri){
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }


    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    static final int COL_WEATHER_HUMIDITY = 9;
    static final int COL_WEATHER_WIND_SPEED = 10;
    static final int COL_WEATHER_PRESSURE = 11;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        weekday_text = (TextView) rootView.findViewById(R.id.weekday_text_id);
        date_text = (TextView) rootView.findViewById(R.id.date_text_id);
        high_text = (TextView) rootView.findViewById(R.id.high_text_id);
        low_text = (TextView) rootView.findViewById(R.id.low_text_id);
        weather_text = (TextView) rootView.findViewById(R.id.weather_text_id);
        humidity_text = (TextView) rootView.findViewById(R.id.humidity_text_id);
        wind_text = (TextView) rootView.findViewById(R.id.wind_text_id);
        pressure_text = (TextView) rootView.findViewById(R.id.pressure_text_id);
        icon_image = (ImageView) rootView.findViewById((R.id.icon_image_id));


        return rootView;
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }else {
            Log.d(LOG_TAG, "ShareActionProvider is null");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri){
            return new CursorLoader(getActivity(),mUri,FORECAST_COLUMNS,null,null,null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){return;}

        int idx_max_temp = COL_WEATHER_MAX_TEMP;
        int idx_min_temp = COL_WEATHER_MIN_TEMP;
        int idx_date = COL_WEATHER_DATE;
        int idx_short_desc = COL_WEATHER_DESC;

        float humidity;

        boolean isMetric = app.com.example.andre.sunshine2.Utility.isMetric(getActivity());
        String highLowStr = app.com.example.andre.sunshine2.Utility.formatTemperature(getActivity(), data.getDouble(idx_max_temp), isMetric) + "/" + app.com.example.andre.sunshine2.Utility.formatTemperature(getActivity(), data.getDouble(idx_min_temp), isMetric);

        weekday_text.setText(Utility.getDayName(getActivity(), data.getLong(idx_date)));
        date_text.setText(Utility.getFormattedMonthDay(getActivity(), data.getLong(idx_date)));
        high_text.setText(Utility.formatTemperature(getActivity(), data.getDouble(idx_max_temp), isMetric));
        low_text.setText(Utility.formatTemperature(getActivity(), data.getDouble(idx_min_temp), isMetric));
        weather_text.setText(data.getString(idx_short_desc));
        humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        humidity_text.setText(getActivity().getString(R.string.format_humidity, humidity) );
        wind_text.setText(getActivity().getString(R.string.format_wind, data.getDouble(COL_WEATHER_WIND_SPEED)));
        pressure_text.setText(getActivity().getString(R.string.format_pressure, data.getDouble(COL_WEATHER_PRESSURE)));
        icon_image.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID)));


        mForecastStr = app.com.example.andre.sunshine2.Utility.formatDate(data.getLong(idx_date)) +
                " - " + data.getString(idx_short_desc) +
                " - " + highLowStr;


        //text.setText(mForecastStr);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
