package com.example.doancuoikycoffeeorder;

import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

public class LocalVariablesAndMethods {
    public static String userLogin;
    public static boolean flagTableList = false; /*TRUE = INVOICE, FALSE = ORDER*/
    public static String tableOrder;
    public static String tableNameOrder;
    public static String tableNameInvoice;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String STATUS_ORDER = "order";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_FREE = "free";
    public static final String PER_ADMIN = "admin";
    public static final String PER_CHEF = "chef";
    public static final String PASS_DEFAULT = getASCIICodeFromText("123", 2);
    public static final String STATUS_FINISH = " SẴN SÀNG !!!";
    public static final String DBNAME = "COFFEE";
    public static final String STORAGE = "gs://doancuoikycoffeeorder.appspot.com/";
    public static final String API = "https://firebasestorage.googleapis.com/v0/b/doancuoikycoffeeorder.appspot.com/o/images%2F";

    /*Phương thức chuyển đổi số thành định dạng tiền: EX: 1,000,000*/
    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = ".";
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }
    }

    /*Mã hóa mật khẩu với key = iDecode*/
    public static String getASCIICodeFromText(String text, int iDecode) {
        try {
            StringBuilder result = new StringBuilder();
            if (!TextUtils.isEmpty(text)) {
                /*Mảng chars lưu từng phần tử của mật khẩu*/
                char[] chars = text.toCharArray();
                for (char ascii : chars) {
                    /*Chuyển từng phần tử của mật khẩu thành dạng (int) rồi cộng thêm iDecode đơn vị*/
                    int convert = (int) ascii + iDecode;
                    /*Chuyển về String để append vào result, lấy 3 chữ số, nếu không đủ thì thêm số 0 vào*/
                    result.append(StringUtils.leftPad(String.valueOf(convert), 3, "0"));
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*Giải mã mật khẩu với key = iDecode*/
    public static String getTextFromASCIICode(String ascii, int iDecode) {
        try {
            StringBuilder res = new StringBuilder();
            if (!TextUtils.isEmpty(ascii)) {
                int charCount = ascii.length() / 3;
                for (int i = 0; i < charCount; i++) {
                    /*Cắt chuỗi*/
                    String sub = ascii.substring(0, 3);
                    /*chuyển về Integer*/
                    int temp = Integer.parseInt(sub) - iDecode;
                    /*Chuyển về kiểu Char*/
                    char ch = (char) (temp);
                    /*Gán kết quả*/
                    res.append(ch);
                    ascii = StringUtils.right(ascii, (ascii.length() - 3));
                }
            }
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
