package com.sh.fbs.basic;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;


public class ObjectReflectFillUtils {
    public static <T> void fillObject(T obj) throws IllegalAccessException, InstantiationException {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            // 字符型
            if (fieldType == String.class) {
                field.set(obj, (field.getName()+ RandomStringUtils.randomAlphabetic(20)).substring(0,20));
            }else if (fieldType == byte.class || fieldType == Byte.class) {
                field.set(obj, RandomStringUtils.randomAlphabetic(5).getBytes(StandardCharsets.UTF_8));
            }else if (fieldType == char.class || fieldType == Character.class) {
                field.set(obj, RandomStringUtils.randomAlphabetic(5).getBytes(StandardCharsets.UTF_8));
            }
            // Number
            else if (fieldType == short.class || fieldType == Short.class) {
                field.set(obj,(short)1);
            }
            else if (fieldType == int.class || fieldType == Integer.class) {
                field.set(obj, RandomUtils.nextInt());
            }else if (fieldType == long.class || fieldType == Long.class) {
                field.set(obj, RandomUtils.nextLong());
            }else if (fieldType == boolean.class || fieldType == Boolean.class) {
                field.set(obj, RandomUtils.nextBoolean());
            }else if (fieldType == float.class || fieldType == Float.class) {
                field.set(obj, RandomUtils.nextFloat());
            }else if (fieldType == double.class || fieldType == Double.class) {
                field.set(obj, RandomUtils.nextDouble());
            }
            // Date& Time
            else if (fieldType == Date.class) {
                field.set(obj, new Date());
            } else if (fieldType == Timestamp.class ) {
                field.set(obj, new Timestamp(new Date().getTime()));
            } else {
                // 如果是自定义类型，递归填充
                Object nestedObj = field.get(obj);
                if (nestedObj == null) {
                    nestedObj = fieldType.newInstance();
                    field.set(obj, nestedObj);
                }
                fillObject(nestedObj);
            }
        }
    }


}
