package com.com.zhoujian.zhbj.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhbj.R;
import com.zhoujian.zhbj.activity.MainUI;
import com.zhoujian.zhbj.bean.NewsCenterBean;

import java.util.List;

/**
 * 菜单的的Fragment加载
 */
public class MenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private List<NewsCenterBean.NewsCenterMenuBean> mMenuDatas;
    private MenuAdapter mAdapter;
    private int mCurrentItem = -1;//当前条目的标记

    @Override
    protected View initView() {
        mListView = new ListView(mActivity);
        mListView.setBackgroundColor(Color.BLACK);
        mListView.setDividerHeight(0);//去掉分割线
        mListView.setPadding(0, 40, 0, 40);
        return mListView;
    }

    /**
     * 设置数据
     *
     * @param Datas
     */
    public void setDatas(List<NewsCenterBean.NewsCenterMenuBean> Datas) {
        this.mMenuDatas = Datas;
        mAdapter = new MenuAdapter();
        mListView.setAdapter(mAdapter);
        mCurrentItem = 0;

        mListView.setOnItemClickListener(this);
    }

    /**
     * listview条目的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mCurrentItem==position){
            return;
        }
        mCurrentItem=position;
        //更新UI
        mAdapter.notifyDataSetChanged();
        //切换右边菜单页面对应的controller
        //先拿到右边对应的controller
        ContentFragment contentFragment = ((MainUI) mActivity).getContentFragment();
        ///切换菜单对应的内容条目
        contentFragment.switchMenuItem(mCurrentItem);
        //切换后自动关闭slidingmenu
        ((MainUI)mActivity).getSlidingMenu().toggle();

    }

    //设置mListView的数据
    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mMenuDatas != null) {
                return mMenuDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mMenuDatas != null) {
                return mMenuDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //没有复用
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_menu, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                //找到那些控件
                holder.tv = convertView.findViewById(R.id.item_menu_tv);

            } else {
                //有复用
                holder = (ViewHolder) convertView.getTag();

            }
            //设置控件的数据

            String title = mMenuDatas.get(position).title;
            holder.tv.setText(title);
            //如果标记和当前一样，就设置为选中，否则不选中。
            holder.tv.setEnabled(mCurrentItem == position);

            return convertView;
        }
    }

    class ViewHolder {
        TextView tv;
    }

}
