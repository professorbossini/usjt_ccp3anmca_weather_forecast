package br.com.bossini.usjt_ccp3anmca_weather_forecast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {

    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    public Weather (long dt, String description, double minTemp, double maxTemp,
                    double humidity, String iconURL){
        this.description = description;
        NumberFormat numberFormat =
                NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);
        NumberFormat percentFormat =
                NumberFormat.getPercentInstance();
        this.minTemp = numberFormat.format(minTemp);
        this.maxTemp = numberFormat.format(maxTemp);
        this.humidity = percentFormat.format(humidity);
        this.iconURL = "http://openweathermap.org/img/w/" + iconURL + ".png";

        Calendar agora = Calendar.getInstance();
        agora.setTimeInMillis(dt * 1000);
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE HH:mm");
        TimeZone tz = TimeZone.getDefault();
        agora.add(Calendar.MILLISECOND, tz.getOffset(agora.getTimeInMillis()));
        this.dayOfWeek = sdf.format(agora.getTime());
    }
}
