package com.mort.kafka.test;

//import java.util.Scanner;

import java.util.Scanner;

//chuangpu@whitehouse.com
//cMhAuSaKnMgApSu@whitehouse.com
public class Main {

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String s=sc.nextLine();
        int index=s.indexOf("@");
        String sub=s.substring(0,index);
        String right=s.substring(index);
        String left = addMask(sub);
        String result=left+right;
        System.out.println(result);
    }

    public static String addMask(String sub){
        StringBuilder sb=new StringBuilder(sub);
        for (int i = 1; i < sb.length(); i+=5) {
            sb.insert(i,'M');
        }
        for (int i = 3; i <sb.length() ; i+=6) {
            sb.insert(i,'A');
        }
        for (int i = 5; i <sb.length() ; i+=7) {
           sb.insert(i,'S');
        }
        for (int i = 7; i <sb.length() ; i+=8) {
            sb.insert(i,'K');
        }
        String left = sb.toString();
        return left;
    }


}
