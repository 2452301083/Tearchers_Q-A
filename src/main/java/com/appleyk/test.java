package com.appleyk;/*
@author lss
@date 2020/5/24-23:34
*/

public class test {
    public static void main(String args[]){
        String s="https://tutors.eol.cn/app/upload/image/36/d66aba69e80f84ad6f32be7733aa1a09_thumb.jpg";
        if(s.contains("http")){
            System.out.println(s);
        }
        else {
            System.out.println("error");
        }
    }
}
