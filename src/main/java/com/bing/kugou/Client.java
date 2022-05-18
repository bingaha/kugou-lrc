package com.bing.kugou;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bing.kugou.util.ZLibUtil;

import java.io.*;
import java.util.Base64;
import java.util.Scanner;

/**
 * 酷狗音乐歌词下载
 *
 * @author bing
 * Created on 2022/01/05
 */
public class Client {

    public static final String KRC_PATH = "F:\\OneDrive\\音乐\\下载";

    public static void main(String[] args) throws IOException {

        Scanner input1 = new Scanner(System.in);
        System.out.print("需要下载歌词的歌名：");
        String keyword = input1.nextLine();
        keyword = URLUtil.encode(keyword);
        String url1 = "http://mobileservice.kugou.com/api/v3/lyric/search?version=9108&highlight=1&keyword=" + keyword + "&plat=0&pagesize=20&area_code=1&page=1&with_res_tag=1";

        String result1 = HttpUtil.get(url1).replace("<!--KG_TAG_RES_START-->", "");
        String result1JsonString = result1.substring(result1.indexOf("{"), result1.lastIndexOf("}") + 1);

        JSONObject result1JsonObject = JSONUtil.parseObj(result1JsonString).getJSONObject("data");
        JSONArray result1JsonArray = result1JsonObject.getJSONArray("info");
        for (int i = 0; i < result1JsonArray.size(); i++) {
            String byPath = (String) result1JsonArray.getByPath("[" + i + "].filename");
            System.out.println(i + 1 + ":" + byPath);
        }
        Scanner input2 = new Scanner(System.in);
        System.out.print("需要下载第几首歌的歌词：");
        int serialNumber = input2.nextInt();

        String hash = (String) result1JsonArray.getByPath("[" + (serialNumber - 1) + "].320hash");
        System.out.println(hash);

        String url2 = "http://krcs.kugou.com/search?ver=1&man=yes&client=mobi&keyword=&duration=&hash=" + hash + "&album_audio_id=";

        String result2 = HttpUtil.get(url2);

        JSONObject result2JsonObject = JSONUtil.parseObj(result2);
        JSONArray result2JsonArray = result2JsonObject.getJSONArray("candidates");
        for (int i = 0; i < result2JsonArray.size(); i++) {
            JSONObject jsonObject = result2JsonArray.getJSONObject(i);
            String singer = (String) jsonObject.getByPath("singer");
            String song = (String) jsonObject.getByPath("song");
            Integer score = (Integer) jsonObject.getByPath("score");
            Integer duration = (Integer) jsonObject.getByPath("duration");

            System.out.println((i + 1) + ": " + singer + " - " + song + " \t " + ZLibUtil.millisToTimeStr(duration) + " \t评分：" + score);
        }
        Scanner scan3 = new Scanner(System.in);
        System.out.print("需要下载第几个歌词：");
        int Serial = scan3.nextInt();

        // 单文件下载
        if (Serial != 0) {
            String id = (String) result2JsonArray.getByPath("[" + (Serial - 1) + "].id");
            String accesskey = (String) result2JsonArray.getByPath("[" + (Serial - 1) + "].accesskey");
            String singer = FileUtil.cleanInvalid((String) result2JsonArray.getByPath("[" + (Serial - 1) + "].singer"));
            String song = FileUtil.cleanInvalid((String) result2JsonArray.getByPath("[" + (Serial - 1) + "].song"));

            String url3 = "http://lyrics.kugou.com/download?ver=1&client=pc&id=" + id + "&accesskey=" + accesskey + "&fmt=krc&charset=utf8";
            String result3 = HttpUtil.get(url3);
            // String result3 = "{\"info\":\"OK\",\"fmt\":\"krc\",\"charset\":\"\",\"content\":\"a3JjMTjbDC8TvWGAROt257EiTKL2E74iY9jEMPWaEwiY\\/kFxTydNFZmzUCVW9wE9wlENlVWHOU96Nq8jc1VWtcce8YiN+ziScdboPPqMKqAXglPGH5Lc0JAPg5umiazwGZzAxLyDtQjxzYwA\\/ivM8AQM84JH8E9ZwA+WtMilGlZZ7RbXs7KfKIHpqznp7syjsY9T0oK4v0skyLlSuulpFtgA9Fh+4t3BJA7uN40hLs8UeKEPDTFcUrGtSj6zW2iRNXySHqbaUo+aJ2cktcUK6kFNUB8mooQnpkaer4oRT1I6Ft\\/8IyAegYiY\\/6GGibvLW2UQ0rqtTokmQJlw7grKq\\/Un6bzT6MXo4VgKjX5u2peiH4XqqQVt6mewOEH\\/lPQuvMjM4B2zGTcoHXl2x\\/jRzKuPKMfRq\\/H7oC8tjAuIv9FJxhWQQAepLWAyUHtqDVoWWSisit2mQAxBX3oblNKJ9hhnT86YoqUYs18pWz1CmN7oB7MEselK\\/tFJq3ktP6oXXWCDFfYx115c1j745u6JoWqoijK4s4BAHa1GdZgKQdYcsi1nHdemsqOVTOWJeOMWfIqRCzDmleorvJVqP0v\\/Pq06HY66l5IzU0eFIzbWe5\\/xYBaWI6uLe+ygSGmOLl2ikymqNNcw3+CeeXufLLpwL5kaobZe\\/xMFa8lrOmcG\\/b3GOoyfQ069vDn5qYhX3azARMfaxuxIrw3VQmyvo\\/DiLDUpQAA0XL6UHr1oVOfYjA\\/rCbKSKqerMnxHhGLptvK\\/7BdCxaWa+3EhQHEeFZe77SFhqfZAOP2XAe7JbaGN+TpZTQJlxHm44dsHtAYCam2WDuauIJkzkJNvjC017LjWFjlTEXbI+IUGuyj6S+fR7EtdV6sbE\\/sQ2eJWwTii96EAR\\/DZ38kM7O4XGLOJsLjwh6pP0hJXIJl1JPbu4nEhnHETa1VqMIRcHjjwFyNSMJNVA\\/6J5MfV0to5Xnv\\/COgf2jI0ZKtxsoeBsa5XRRn8aa8wLod+jJ5V94iFc9NZvxSFkQ2q4JHfLWm6a5HH3bxDJIwPsyLDcLCj9e6v\\/WBOwxKCXYrvz7WTjYsTLMy7Io8D+T2f+dBWz7QgAQrXGdK2XDsgbJ8M7qh3THtBjLTAZgnzyvdxnrW4ZLBreClU35R0LuWRX+sJ90v14V9EfstUBedkXsafWsWDFGDd\\/d4bohyulho3S\\/k3tPkxIz2s\\/c5RZbDLrLigZtNvGCof\\/AL\\/PLVYCrScJqRSlKCkvV+XcfTaVIOKNIEDhIFA8hG7xOASe+QD6uLwxRoB2WLjH2unam4UQ+Iz2Sk\\/q2e82W8kqwVJq89wmBbhHMIcwa1SqrLgV975Jr+8SIEaO3RsxouRNsuT\\/ORreuATsfMQoof3yCLnetjjn6WDYgI2vnCaz8r2dQ5d7WTuxv5bycPoB6Oh+TGjcGzdLP0MKV000REa7D\\/LXsZA9v4ff4t1QE2C2\\/oR78nSZup\\/zKj46jV\\/QF1i5Gh3kJWJEo0eFT+Sp8Lx7Ymq3UNlaoU3dYWJgSvhlK9RFA23efItRbwhD2oPnazzw\\/Tr\\/3NJzxT4VGGKW421NxUm57B5gTkReXkk48R0BlcXX06OI4NhRt8qSJalgDNxMiVM5cT\\/dOhpJP9n0I6HnXx3OjO5LZ7TkVALlA1qJhLGY307FbDgTjZycyMPoc\\/meXyCRSDBbjFtWqY46jNhKj2Z2E6syYdJNNYOT5DViuWZ+OBvT\\/t2e4Mq1UkTn9OYgO2bKS+rzvSuxdXfR1u2JrOM0hPKnYFm27SkRFPfzNZLP5LhGvsCi0a7Av6hbenLVT789NgD+a53\\/HEV44XBIkgH18vFKxvp3gSyyZwGNluFh9t5SwRBZuEkM\\/QgbTQwKUFl80bSnhosPJEmr5D+43aDiLZnb4T0Sdel5RQFth5\\/3YrL0EEcoU9qM30QnDoVlCGXhgLPWXC29Phkmx2+nO0KH7bPsqUHCyj\\/8pv2TvI55o\\/E8sGTgJPwi22sISy\\/548YP6zbMefcbuttcbiSjGki3n6m4o20uQZl6GBD\\/yzb\\/WdDuo8DyxOyqOs2ZA7AHs\\/ANJuoID0egHWuFxepGntw+BCvOii4XdKDJ76B5kGIgeZa\\/X4qOeXZ+dJdGZcn52f5HSrDePF7tryR9\\/Lnc45tgy3wh3szBFYGQFOR3I2FSe8e4HMEYgUdalIE0oposGaLUUpTKbz4PrvXRgnis8emCq2dDDZVy04\\/3H3Lbny59EiO02iaKWvTtV+Qm57\\/MfT4CbDZrcrSVx3cAfh6UrqvgcBMDAwC5urEHTJlprj3GToGa\\/RUR9tIjmie8poLOVuGA\\/xG8yHZPmCRhZXlxeBiWecgEpMwaOd9gEl+9e31lzqFxCFOIkCxu1oA\\/EFBZCH5u7JDYD1M3KxSoO\\/HP7PIv9WGBGf73h8smDGfAgoLjljFGJWA6dxPObZkuFyV8Bp4zsThy8pIDmkXppoXAd1nm7dFDMTOlvQYYTtcQxpFKiZbPdR3LDAyhzFvA0s8UVtBqr2X5z8fLZpI9JlfXfXTMLqtMuL3AdmUniU9mrxYBIINKmJFXiMMZoLN8iJWruRio8O23rlDF3WU9ks+QBsLSXn1zYrnkJ5IU1oELdiVPEf+jmTzTf0Ln7mPaG4q+dPqef0zpQwbqPeBk+6vLHi9McXo4f7Amg7XJzSCges7vUGz4qYhlU08XFiFmHCZU7box2pIAIcT9A\\/ZqTcC8f8Km695eaSVxPdkLp1awDdyZyR+nyZl1MNqNO6qNVm7gkzymK\\/jIVFYlG\\/9+tCnUfIp9lzCY+ann\\/X3h9H36dNCshEolA3tc9eg9OInUs28enQXH7chp8JqcQQglkK1Up5jiEtvZ1IyZvDq1Haodl3Br1XN3+zKPhYqJujVelvZXeKc8ggTg4D\\/BuCVQ967mcxt7phP45zCRFqPIBJE7lHsiKpNf4eRl9lIddKbhWO67ieuABiQzxTUZEDurR\\/y09WeCMF3IjYNaFz6JzXYsTh8kQ4HNAndVUpQyC5X8hC1JVKg3IHYP31suXEbJ\\/iUMKYCW6ko2Ye1+gqc6RdJ1xq4m\\/4MmhV1ZPqvYlhhM51pFTjXLS\\/ojagwdCT5VnMgk18taiiGEYIaZdUG\\/kJF7JHfz4jB8sZtdykgUj0KffawgbSe34nYdHL8zCGxhd5U0HGE\\/CH6EFtGVlCzXhpjP7y9nf6qmecWTevDT3zinWMueOJOhbi5fmuuVz8ZuzhwZjzFWzqtgk3OiwYivHkCGrsYfVB+B3QWNsaWhyHXSkJG+GRZNgn8lT482r+IfhMlZ5yOb2\\/ydM3kdGGhJIWsJgRv+Sf\\/g30CTavV8WQ8BCz07zeGFF4Nj8cuV5zW3pX05PKopnTLpOyM1gbWFu1xA10dWLU4B7rmEN\\/XN2EqE2n2DBFfjXkMu0Vhqm8tPP26coi6gIQkbC\\/cDWGITZINI7r7m4dCJRV+dHxIuL6GdSCchSrlplOJtxtP1vQ9aw0JT3oKEM+BO5PbiThO\\/FUijxng21B8cQU1HNttI15ltAClK4A\\/F5VpV8LcaM\\/SUK4xm6oUmg==\",\"status\":200,\"error_code\":0}";
            String content = (String) JSONUtil.parseObj(result3).getByPath("content");

            byte[] bytes = Base64.getDecoder().decode(content);
            String fileName = KRC_PATH + "\\" + singer + " - " + song;
            Base64ToFile(bytes, fileName + ".krc");
            System.out.println("krc文件已保存在：" + fileName + ".krc");

            // 获取krc文件中文本内容
            String krcText = ZLibUtil.getKrcText(fileName + ".krc");
            String lrcText = ZLibUtil.Krc2Lrc(krcText);

            FileWriter writer = new FileWriter(fileName + ".lrc");
            System.out.println("lrc文件已保存在：" + fileName + ".lrc");
            writer.write(lrcText);
            writer.close();

            Scanner scan4 = new Scanner(System.in);
            System.out.print("是否保存krc文件？(输入1保留，其他操作都会删除)");
            String s = scan4.nextLine();
            if (!"1".equals(s)) {
                File file = new File(fileName + ".krc");
                file.delete();
            }
        }
    }

    static void Base64ToFile(byte[] bytes, String filePath) throws IOException {

        File file = new File(filePath);
        if (file.exists()) {
            file.createNewFile();
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        byte[] buffer = new byte[1024];

        OutputStream outputStream = new FileOutputStream(file);

        int byteread;
        while ((byteread = inputStream.read(buffer)) != -1) {
            // 文件写操作
            outputStream.write(buffer, 0, byteread);
        }
        inputStream.close();
        outputStream.close();
    }

}
