package com.minimalist.basic.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtil {

    /** <img src="" /> 数据 */
    private static Pattern p_image = Pattern.compile("<img.*src\\s*=\\s*(.*?)[^>]*?>", Pattern.CASE_INSENSITIVE);

    /** <img src=""></img> 数据 */
    private static Pattern r_image = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)");

    /**
     * 从富文本中获取图片URL
     * @param richText 富文本内容
     * @return 图片URL列表
     */
    public static List<String> getImgUrlByRichText(String richText) {
        List<String> urlList = CollectionUtil.list(false);
        Matcher pMatcher = p_image.matcher(richText);
        while (pMatcher.find()) {
            //得到<img />数据
            String img = pMatcher.group();
            //匹配<img>中的src数据
            Matcher rMatcher = r_image.matcher(img);
            while (rMatcher.find()) {
                urlList.add(rMatcher.group(1));
            }
        }
        return urlList;
    }

    /**
     * 内容编码
     * @param text 内容
     * @return 编码后的内容
     */
    public static String encode(String text) {
        return HtmlUtil.escape(text);
    }

    /**
     * 内容解码
     * @param text 内容
     * @return 解码后的内容
     */
    public static String decode(String text) {
        return HtmlUtil.unescape(text);
    }

    /**
     * 分割字符串，并使List<String>转为List<Long>
     * @param str 字符串，逗号分割
     * @return List<Long>
     */
    public static List<Long> splitAndListStrToListLong(String str) {
        if (StrUtil.isBlank(str)) { return CollectionUtil.list(false); }
        List<String> split = StrUtil.split(str, ",");
        return split.stream().map(Long::parseLong).toList();
    }

    //url匹配正则
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b((?:https?|ftp|file)://|www\\.|ftp\\.)"  // 协议头匹配
                    + "[^\\s\"'<>(){}]+"  // 核心内容匹配（排除常见边界符）
                    + "(?=[\\s\"'<>(){}]|$)",  // 前瞻确保在边界处停止
            Pattern.CASE_INSENSITIVE);

    /**
     * 从富文本内容中提取url
     * @param content 富文本内容
     * @return url列表
     */
    public static Set<String> extractFileUrl(String content) {
        Set<String> urls = new HashSet<>();
        Matcher matcher = URL_PATTERN.matcher(decode(content));
        while (matcher.find()) {
            String url = matcher.group();
            urls.add(url);
        }
        return urls;
    }

}
