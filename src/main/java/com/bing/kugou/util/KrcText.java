// package com.bing.kugou.util;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
//
// public class KrcText {
//     private static final char[] miarry = {'@', 'G', 'a', 'w', '^', '2', 't', 'G', 'Q', '6', '1', '-', 'Î', 'Ò', 'n', 'i'};
//
//     public static void main(String[] args) throws IOException {
//         String filenm = "F:\\OneDrive\\音乐\\下载\\庞龙 - 你是我的玫瑰花.krc";//krc文件的全路径加文件名
//         String krcText = getKrcText(filenm);
//         // System.out.println(krcText);
//         String s = ZLibUtil.Krc2Lrc(krcText);
//         System.out.println(s);
//     }
//
//     /**
//      * @param filenm krc文件路径加文件名
//      * @return krc文件处理后的文本
//      * @throws IOException
//      */
//     public static String getKrcText(String filenm) throws IOException {
//         File krcfile = new File(filenm);
//
//         FileInputStream inputStream = new FileInputStream(krcfile);
//         byte[] zip_byte = new byte[(int) krcfile.length()];
//
//         byte[] top = new byte[4];
//         inputStream.read(top);
//         inputStream.read(zip_byte);
//         int j = zip_byte.length;
//         for (int k = 0; k < j; k++) {
//             int l = k % 16;
//             zip_byte[k] = (byte) (zip_byte[k] ^ miarry[l]);
//         }
//
//         return new String(ZLibUtil.decompress(zip_byte), StandardCharsets.UTF_8);
//     }
// }