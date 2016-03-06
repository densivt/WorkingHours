package com.densivt;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

public class Main {
    private static final String FILENAME = "days.json";

    public static void main(String[] args) throws ParseException, IOException {
        // Переменные
        int workingHours = 0;
        DateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat dateFormatOutput = new SimpleDateFormat("dd.MM.yyyy");
	    Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        JSONArray workingDays = new JSONArray();
        JSONArray weekends = new JSONArray();
        JSONArray celebrateDays = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        // Подбираем значения
        date1.setTime(dateFormatInput.parse(args[0]));
        date2.setTime(dateFormatInput.parse(args[1]));
        // Округляем даты
        {
            date1.set(Calendar.SECOND, 0);
            date1.set(Calendar.MINUTE, 0);
            date1.set(Calendar.HOUR, 0);
            date2.set(Calendar.SECOND, 0);
            date2.set(Calendar.MINUTE, 0);
            date2.set(Calendar.HOUR, 0);
            date2.add(Calendar.DATE, 1);
        }
        // Перебираем
        for (Date date = date1.getTime(); date1.before(date2); date1.add(Calendar.DATE, 1), date = date1.getTime()){
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(date);
            if (isHoliday(tmp)){
                hashMap.put("date", dateFormatOutput.format(tmp.getTime()));
                hashMap.put("workingHours", 0);
                celebrateDays.add(new LinkedHashMap<String, Object>(hashMap));
                hashMap.clear();
                continue;
            }
            if (isWeekend(tmp)){
                hashMap.put("date", dateFormatOutput.format(tmp.getTime()));
                hashMap.put("workingHours", 0);
                weekends.add(new LinkedHashMap<String, Object>(hashMap));
                hashMap.clear();
                continue;
            }
            if (isFriday(tmp)){
                hashMap.put("date", dateFormatOutput.format(tmp.getTime()));
                hashMap.put("workingHours", 7);
                workingDays.add(new LinkedHashMap<String, Object>(hashMap));
                hashMap.clear();
                workingHours += 7;
                continue;
            }
            hashMap.put("date", dateFormatOutput.format(tmp.getTime()));
            hashMap.put("workingHours", 8);
            workingDays.add(new LinkedHashMap<String, Object>(hashMap));
            hashMap.clear();
            workingHours += 8;
        }
        jsonObject.put("workingDays", workingDays);
        jsonObject.put("weekends", weekends);
        jsonObject.put("celebrateDays", celebrateDays);
        // Выводим кол-во рабочих часов
        System.out.println(workingHours);
        // Записываем в файл
        FileWriter writer = new FileWriter(FILENAME);
        writer.write(JSONValue.toJSONString(jsonObject));
        writer.flush();
        writer.close();
    }
    // Проверка на пятницу
    private static boolean isFriday(Calendar date) {
        if (date.get(Calendar.DAY_OF_WEEK) == 6)
            return true;
        else return false;
    }
    // Проверка на выходные
    private static boolean isWeekend(Calendar date) {
        if ((date.get(Calendar.DAY_OF_WEEK) == 7) || (date.get(Calendar.DAY_OF_WEEK) == 1))
            return true;
        else return false;
    }
    // Проверка на праздники
    private static boolean isHoliday(Calendar date){
        String[] holidays = {"01.01", "02.01", "03.01", "04.01", "05.01", "06.01", "07.01", "08.01", "09.01", "10.01", "23.02", "08.03", "01.05", "09.05", "04.11"};
        DateFormat dateFormat = new SimpleDateFormat("dd.MM");
        for (String day : holidays) {
            if (day.equals(dateFormat.format(date.getTime())))
                return true;
        }
        return false;
    }
}
