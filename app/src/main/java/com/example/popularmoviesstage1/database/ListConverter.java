package com.example.popularmoviesstage1.database;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.List;

public class ListConverter {

    @TypeConverter
    public static List<Integer> toIntList(String stringOfIntegers) {

        List<Integer> result = new ArrayList<>();

        stringOfIntegers = stringOfIntegers.substring(1, stringOfIntegers.length() - 1);
        String[] splits = stringOfIntegers.split(",");

        for (int i = 0; i < splits.length; i++) {
            String currentNum = splits[i].trim();
            int intForm = Integer.parseInt(currentNum);
            result.add(intForm);
        }
        return result;
    }

    @TypeConverter
    public static String toStringList(List<Integer> listOfIntegers) {
        return listOfIntegers.toString();
    }
}
