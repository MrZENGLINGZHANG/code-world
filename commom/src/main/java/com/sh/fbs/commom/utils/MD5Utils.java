package com.sh.fbs.commom.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class MD5Utils {

        public static String encrypt(String input) {
            try {
                // 获取 MD5 算法实例
                MessageDigest md = MessageDigest.getInstance("MD5");
                // 将输入字符串转换为字节数组，并使用 UTF-8 编码
                byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                // 计算哈希值
                byte[] digest = md.digest(inputBytes);

                // 将字节数组转换为十六进制字符串
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    // 将字节转换为无符号整数
                    int unsignedByte = b & 0xFF;
                    if (unsignedByte < 16) {
                        sb.append("0");
                    }
                    sb.append(Integer.toHexString(unsignedByte));
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
}