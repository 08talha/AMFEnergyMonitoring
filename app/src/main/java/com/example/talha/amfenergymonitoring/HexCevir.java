package com.example.talha.amfenergymonitoring;

/**
 * Created by talha on 26.05.2017.
 */

public class HexCevir {
    public static int hxCevir(String s){
        int deger=0;
        if(s.equals("E")){
            deger=14;
        }else if(s.equals("A")){
            deger=10;
        }else if(s.equals("B")){
            deger=11;
        }else if(s.equals("C")){
            deger=12;
        }else if(s.equals("D")){
            deger=13;
        }else if(s.equals("F")){
            deger=15;
        }else{
            deger=Integer.parseInt(s);
        }

        /*switch(s){
            case "E":deger=14;
            case "A":deger=10;
            case "B":deger=11;
            case "C":deger=12;
            case "D":deger=13;
            case "F":deger=15;
            default:deger=Integer.parseInt(s);
        }*/

        return deger;

    }
}
