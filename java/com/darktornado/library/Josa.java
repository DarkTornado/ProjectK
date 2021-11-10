package com.darktornado.library;
/*
Josa.java
© 2021 Dark Tornado, All rights reserved.
*/
public class Josa {

    public static final int TYPE_은는 = 0;
    public static final int TYPE_이가 = 1;
    public static final int TYPE_을를 = 2;
    public static final int TYPE_이 = 3;
    public static final int TYPE_으 = 4;

    public static String getJosa(String str, int type) {
        char ch = str.charAt(str.length() - 1);
        if (ch < 0xAC00) return "";
        ch = (char) (ch - 0xAC00);
        char jong = (char) (ch % 28);
        char[][] josa = {
                {'은', '는'},
                {'이', '가'},
                {'을', '를'}
        };
        if (type == TYPE_이) {
            return jong == 0 ? "" : "이";
        } else if (type == TYPE_으) {
            return jong == 0 ? "" : "으";
        } else {
            int index = jong == 0 ? 1 : 0;
            return String.valueOf(josa[type][index]);
        }
    }

}
