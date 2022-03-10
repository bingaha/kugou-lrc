package com.bing.kugou.util;

import cn.hutool.core.text.StrBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public abstract class ZLibUtil {
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];
        Deflater compresser = new Deflater();
        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);
        try {
            dos.write(data, 0, data.length);
            dos.finish();
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析krc文件，转成文本
     *
     * @param data krc文件的data数组
     * @return krc解码后的文本data数组
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        decompresser.end();
        return output;
    }

    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
        try {
            int i = 1024;
            byte[] buf = new byte[i];
            while ((i = iis.read(buf, 0, i)) > 0) {
                o.write(buf, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return o.toByteArray();
    }

    /**
     * krc文本转lrc文本
     *
     * @param krcText 待转换的krc文本
     * @return 转换完成的lrc文本
     * @throws IOException 如果读取失败
     */
    public static String Krc2Lrc(String krcText) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(krcText.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {

            String regex = "^\\[([0-9]+),([0-9]+)\\]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String regex1 = "<([0-9]+),([0-9]+),([0-9]+)>(.*?)(?=(<|$))";
                Pattern pattern1 = Pattern.compile(regex1);
                // 本句开始时间
                long startTime = Long.parseLong(matcher.group(1));
                sb.append("[").append(millisToTimeStr(startTime)).append("]");

                Matcher matcher1 = pattern1.matcher(line);
                while (matcher1.find()) {
                    // 本句中单字持续时间
                    long duration = Long.parseLong(matcher1.group(2));

                    startTime += duration;
                    sb.append(matcher1.group(4));
                    sb.append("[").append(millisToTimeStr(startTime)).append("]");
                }
                sb.append("\r\n");
            } /*else {
                sb.append(line).append("\r\n");
            }*/
            // break;
        }

        return sb.toString();
    }

    public static String millisToTimeStr(long millis) {
        StrBuilder strBuilder = new StrBuilder();
        long h = 60 * 60 * 1000;
        long m = 60 * 1000;
        long s = 1000;
        if ((millis / h) != 0) {
            if ((millis / h) < 10) {
                strBuilder.append("0");
            }
            strBuilder.append(millis / h).append(":");
        }
        millis = millis % h;
        if ((millis / m) < 10) {
            strBuilder.append("0");
        }
        strBuilder.append(millis / m).append(":");
        millis = millis % m;
        if ((millis / s) < 10) {
            strBuilder.append("0");
        }
        strBuilder.append(millis / s).append(".");
        strBuilder.append(millis % s).append("");
        return strBuilder.toString();
    }

    private static final char[] miarry = {'@', 'G', 'a', 'w', '^', '2', 't', 'G', 'Q', '6', '1', '-', 'Î', 'Ò', 'n', 'i'};

    /**
     * @param filenm krc文件路径加文件名
     * @return krc文件处理后的文本
     * @throws IOException
     */
    public static String getKrcText(String filenm) throws IOException {
        File krcfile = new File(filenm);

        FileInputStream inputStream = new FileInputStream(krcfile);
        byte[] zip_byte = new byte[(int) krcfile.length()];

        byte[] top = new byte[4];
        inputStream.read(top);
        inputStream.read(zip_byte);
        int j = zip_byte.length;
        for (int k = 0; k < j; k++) {
            int l = k % 16;
            zip_byte[k] = (byte) (zip_byte[k] ^ miarry[l]);
        }
        inputStream.close();
        return new String(ZLibUtil.decompress(zip_byte), StandardCharsets.UTF_8);
    }
}