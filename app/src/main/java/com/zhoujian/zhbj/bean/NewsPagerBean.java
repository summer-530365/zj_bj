package com.zhoujian.zhbj.bean;

import java.util.List;

public class NewsPagerBean {
    public NewsPagerDataBean data;
    public int retcode;

    public class NewsPagerDataBean{
        public String countcommenturl;
        public String more;
        public List<NewsPagerNewsBean> news;
        public String title;
        public List<NewsPagerTopicBean> topic;
        public List<NewsPagerTopnewsBean>  topnews;
    }
    public class NewsPagerNewsBean{
        public boolean comment;
        public String commentlist;
        public String commenturl;
        public int id;
        public String listimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;


        public String largeimage;
        public String smallimage;


    }
    public class NewsPagerTopicBean{
        public String description;
        public Long id;
        public String listimage;
        public int sort;
        public String title;
        public String url;

    }
    public class NewsPagerTopnewsBean{
        public boolean comment;
        public String commentlist;
        public String commenturl;
        public Long id;
        public String pubdate;
        public String title;
        public String topimage;
        public String type;
        public String url;

    }
}
