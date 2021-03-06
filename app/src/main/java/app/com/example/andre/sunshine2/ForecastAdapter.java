package app.com.example.andre.sunshine2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout = true;
    /**
     -        Remember that these views are reused as needed.		+     * Cache of the children views for a forecast list item.
     */
     public static class ViewHolder {
            public final ImageView iconView;
            public final TextView dateView;
            public final TextView descriptionView;
            public final TextView highTempView;
            public final TextView lowTempView;

            public ViewHolder(View view) {
                    iconView = (ImageView) view.findViewById(R.id.list_item_icon);
                    dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
                    descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
                    highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
                    lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            }
     }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = app.com.example.andre.sunshine2.Utility.isMetric(mContext);
        String highLowStr = app.com.example.andre.sunshine2.Utility.formatTemperature(mContext, high) + "/" + app.com.example.andre.sunshine2.Utility.formatTemperature(mContext, low);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_max_temp = ForecastFragment.COL_WEATHER_MAX_TEMP;
        int idx_min_temp = ForecastFragment.COL_WEATHER_MIN_TEMP;
        int idx_date = ForecastFragment.COL_WEATHER_DATE;
        int idx_short_desc = ForecastFragment.COL_WEATHER_DESC;

        String highAndLow = formatHighLows(
                cursor.getDouble(idx_max_temp),
                cursor.getDouble(idx_min_temp));

        return app.com.example.andre.sunshine2.Utility.formatDate(cursor.getLong(idx_date)) +
                " - " + cursor.getString(idx_short_desc) +
                " - " + highAndLow;
    }

    public void setuseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /*
                Remember that these views are reused as needed.
             */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        layoutId = viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int viewType = getItemViewType(cursor.getPosition());
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);

        // Read date from cursor
        Long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,date));

        // Read weather forecast from cursor
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low));

        int weather_condition_id = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int id_img_src = viewType == VIEW_TYPE_TODAY ? Utility.getArtResourceForWeatherCondition(weather_condition_id) : Utility.getIconResourceForWeatherCondition(weather_condition_id);
        viewHolder.iconView.setImageResource(id_img_src);

    }
}