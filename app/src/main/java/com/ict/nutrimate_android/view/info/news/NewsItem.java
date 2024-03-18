package com.ict.nutrimate_android.view.info.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {
    private String title;
    private String content;
    private String imglink;
    private String newslink;
    private String keyword;
}

