package com.example.minhtien.androidwatermusical;

/**
 * Created by Minh Tien on 26/04/2018.
 */

public class untils {
    public static String getNameSong(String input){
        String tmp="";
        tmp = input.split(":")[1];
        return tmp;
    }
}
