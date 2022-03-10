# kugou-lrc

## 介绍

此项目可以从酷狗音乐api中直接下载lrc格式的歌词，并且保留ktv时间戳。

## 如何使用

直接运行`com.bing.kugou.Client`的main方法即可。注意可能需要修改一下`KRC_PATH`参数，这个参数是下载路径。

关于酷狗音乐krc解析为krc文本的方法，是从网上搜索到的，但时间太长具体出处已找不到了，在此提一下。


## 使用的API介绍

api也不是我自己扒的，同样来自网络

### 根据关键词查询歌曲hash，url编码可以用中文直接在浏览器地址栏输入

get http://mobileservice.kugou.com/api/v3/lyric/search?version=9108&highlight=1&keyword=%E9%BA%BB%E9%9B%80&plat=0&pagesize=20&area_code=1&page=1&with_res_tag=1

响应结果如下

```
"data": {
    "timestamp": 1640675792,
    "info": [
        {
            "320hash": "E5FC1410EA754BB6A7F87D35668FC237",
            "hash": "23FEA80D47ED659C8A7C6CFCBD217DD0",
            "sqfilesize": 31702684,
            "sqprivilege": 10,
            "old_cpy": 0,
            "bitrate": 128,
            "ownercount": 15480885,
            "320filesize": 10112712,
            "paytype": 3,
            "filename": "李荣浩 - 麻雀",
        }
     ]
    ……
}
```

关键内容是两个hash值，经测试上面两个hash调用下面的接口都能拿到数据


### 根据歌曲hash查询歌词列表

get http://krcs.kugou.com/search?ver=1&man=yes&client=mobi&keyword=&duration=&hash=12B61B0BB78242B75713B723FA08EBA2&album_audio_id=

```
{
    …
    "status": 200,
    "candidates": [
        {
            "soundname": "",
            "krctype": 1,
            "id": "42544516",
            "originame": "",
            "accesskey": "85F1E1198C2B9280A1D9A344F04C538B",
            "parinfo": [
                [
                    405461003,
                    "是Perper鸦",
                    "2019/12/09 12:58",
                    "http://imge.kugou
                    .com/kugouicon/165/20200312/20200312174016589163.jpg"
                ]
            ],
            ……
        }
    }
}
```

同样只列举关键信息，`candidates`是歌词文件列表，关键字段是`id`和`accesskey`，将这两个字段拼入下面的地址

### 获取歌词

GET http://lyrics.kugou.com/download?ver=1&client=pc&id=53377741&accesskey=B3C702F30836AA17B59964CDEE88B441&fmt=krc&charset=utf8

### 使用下方工具进行转码

[Base64文件在线编解码工具 (hitoy.org)](https://www.hitoy.org/tool/file_base64.php)

即可得到酷狗的krc后缀的歌词文件。

