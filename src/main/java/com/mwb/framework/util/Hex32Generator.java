package com.mwb.framework.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// 32进制数转换器
public class Hex32Generator {

    private final static String CODE_SET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private final static Random RANDOM = new Random();

    private static int getSize() {
        return CODE_SET.length();
    }

    private static char getChar(int index) {
        return CODE_SET.charAt(index);
    }
    
    private static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; ++i) {
            sb.append(getChar(RANDOM.nextInt(getSize())));
        }
        
        return sb.toString();
    }

    /**
     * @param decimalNumber - 十进制数
     * @param length - 32进制数长度，位数不足的左边补0
     * @return 固定长度的32进制数
     */
    public static String getHex32Number(long decimalNumber, int length) {
        StringBuilder sb = new StringBuilder();

        while (decimalNumber != 0) {
            sb.append(getChar(new Long(decimalNumber % getSize()).intValue()));

            decimalNumber /= getSize();
        }

        sb = sb.reverse();

        if (sb.length() < length) {
            int charNum2Add = length - sb.length();

            StringBuilder prefix2Add = new StringBuilder();

            for (int i = 0; i < charNum2Add; ++i) {
                prefix2Add.append(getChar(0));
            }

            sb.insert(0, prefix2Add.toString());
        }

        return sb.toString();
    }
    
    /**
     * @param numberLength - 随机32进制数的长度
     * @param count - 随机32进制数的数量
     * @return 返回count个numberLength长度的32进制数
     */
    public static Set<String> getRandomHex32Numbers(int numberLength, int count) {
        Set<String> generatedNumberSet = new HashSet<String>();
        
        while (generatedNumberSet.size() < count) {
            generatedNumberSet.add(getRandomString(numberLength));            
        }
        
        return generatedNumberSet;
    }

}
