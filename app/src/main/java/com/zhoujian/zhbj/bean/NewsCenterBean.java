package com.zhoujian.zhbj.bean;

import java.util.List;

public class NewsCenterBean {
    public List<NewsCenterMenuBean> data;
    public List<Long> extend;
    public int retcode;

    public class NewsCenterMenuBean {
        public List<NewsCenterNewsBean> children;
        public int id;
        public String title;
        public int type;


        public String url;
        public String url1;

        public String dayurl;
        public String excurl;
        public String weekurl;

        public class NewsCenterNewsBean {
            public int id;
            public String title;
            public int type;
            public String url;
        }

    }


}
