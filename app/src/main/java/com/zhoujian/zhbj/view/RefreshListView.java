package com.zhoujian.zhbj.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static String TAG = "RefreshListView";
    private static final long DURATION = 400;
    private LinearLayout mHeadLayout;//头布局
    private View mHeaderView;//自定义的部分
    private View mRefreshView;//刷新部分
    private float mDownX;
    private float mDownY;
    private int mRefreshHeight;
    private int mFooterViewHeight;
    private static final int STATE_PULL_DOWN = 0;
    private static final int STATE_RELEASE_REFRESH = 1;
    private static final int STATE_REFRESHING = 2;

    private int mCurrentState = STATE_PULL_DOWN;//状态的标记
    private ProgressBar mPbloading;
    private ImageView mIvArrow;
    private TextView mTvState;
    private TextView mTvDate;
    private RotateAnimation down2upAni;
    private RotateAnimation up2downAni;
    private OnRefreshListener mListener;
    private View mFooterView;
    private boolean isLoadingMore;
    public RefreshListView(Context context) {
        this(context, null);
    }
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initAnimation();
    }
    /**
     * 加载动画
     */
    private void initAnimation() {
        down2upAni = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        down2upAni.setDuration(400);
        down2upAni.setFillAfter(true);
        up2downAni = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        up2downAni.setDuration(400);
        up2downAni.setFillAfter(true);

    }

    /**
     * 初始化
     */
    private void initView() {
        initHeaderView();
        initFooterView();

    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.refresh_footer_layout, null);
        this.addFooterView(mFooterView);
        mFooterView.post(new Runnable() {
            @Override
            public void run() {
//                mHeadLayout.setVisibility(View.VISIBLE);
                mFooterViewHeight = mFooterView.getHeight();
                mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
            }
        });
        this.setOnScrollListener(this);
    }

    private void initHeaderView() {
        mHeadLayout = (LinearLayout) View.inflate(getContext(), R.layout.refresh_header_layout, null);
        this.addHeaderView(mHeadLayout);
        mRefreshView = mHeadLayout.findViewById(R.id.refresh_header_refresh);
//        mHeadLayout.setVisibility(View.GONE);
        //隐藏刷新部分
        mPbloading = mHeadLayout.findViewById(R.id.refresh_header_pb);//进度条
        mIvArrow = mHeadLayout.findViewById(R.id.refresh_header_arrow);//箭头
        mTvState = mHeadLayout.findViewById(R.id.refresh_header_tv_state);
        mTvDate = mHeadLayout.findViewById(R.id.refresh_header_tv_date);
//        mRefreshView.measure(0, 0);
//        //padding top的值
//        measuredHeight = mRefreshView.getMeasuredHeight();
//        int top = -measuredHeight;
//        mHeadLayout.setPadding(0, top, 0, 0);//隐藏刷新部分

        //方法二去获取刷新头的高度，之前的那种方法获取到的数据有问题
        mRefreshView.post(new Runnable() {
            @Override
            public void run() {
//                mHeadLayout.setVisibility(View.VISIBLE);
                mRefreshHeight = mRefreshView.getHeight();
                mHeadLayout.setPadding(0, -mRefreshHeight, 0, 0);
            }
        });
    }

    /**
     * 添加用户自定义的头
     *
     * @param headView
     */
    public void addCustomHeaderView(View headView) {
        this.mHeaderView = headView;
        mHeadLayout.addView(headView);
    }

    /**
     * 实时的显示刷新部分
     * 1,当用户滑动的时候去改变padding top的值
     */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveX = ev.getX();
                float mMoveY = ev.getY();
                float diffX = mMoveX - mDownX;
                float diffY = mMoveY - mDownY;
                //处理和listview滑动冲突的bug，三个方面。
                //1，如果是正在刷新状态也不去响应，也让listview自己去实现滑动的操作
                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }
                //2，如果listview左上角的坐标大于自定义头左上角的坐标，也不去响应这个ontouchevnet的逻辑，让listview自己去实现滑动操作
                if (mHeaderView != null) {


                    int[] headerLocation = new int[2];

                    mHeaderView.getLocationInWindow(headerLocation);


                    int[] lvLocaction = new int[2];
                    this.getLocationInWindow(lvLocaction);


                    if (lvLocaction[1] > headerLocation[1]) {
                        // 不响应下拉刷新,跳出去让listview去响应滑动操作
                        break;
                    }
                }


                //3，获取第一个可见的listview条目，如果不是第0个条目，那就跳出去，走super.touchevent让listview自己去实现滑动的操作
                int position = getFirstVisiblePosition();
                if (position != 0) {
                    break;
                }

                if (diffY > 0) {//当它是从上往下滑动的时候
                    int top = (int) (-mRefreshHeight + diffY);
                    mHeadLayout.setPadding(0, top, 0, 0);
                    if (top >= 0 && mCurrentState != STATE_RELEASE_REFRESH) {//当完全拉出隐藏的控件的时候，变为松开刷新
                        //下拉刷新--》松开刷新
                        //标记改成松开刷新
                        mCurrentState = STATE_RELEASE_REFRESH;
//                        Log.d(TAG, "松开刷新");
                        //更新UI，设置文本，进度条，箭头的状态
                        refreshUI();

                    } else if (top < 0 && mCurrentState != STATE_PULL_DOWN) {
                        mCurrentState = STATE_PULL_DOWN;
                        Log.d(TAG, "下拉刷新");
                        refreshUI();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //当手抬起的时候，如果标记是松开刷新,那就让他状态变为正在刷新
                if (mCurrentState == STATE_RELEASE_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    //pading top的处理
//                    mHeadLayout.setPadding(0, 0, 0, 0);
                    int start = mHeadLayout.getPaddingTop();
                    int end = 0;
                    doHeaderAnimator(start, end);
                    refreshUI();
                    //想办法告诉另一个需要加载数据的类，我现在变成了正在刷新，你更新一下数据
                    //怎么告诉另一个类这是个问题
                    //拿到另外一个类传过来的实现了接口带回调方法的类，然后调用它的方法就行,
                    if (mListener != null) {
                        mListener.onRefreshing();
                    }
                }
                //当手抬起的时候，如果标记是下拉刷新
                if (mCurrentState == STATE_PULL_DOWN) {
                    //pading top的处理
                    int top = -mRefreshHeight;
//                    mHeadLayout.setPadding(0, top, 0, 0);
                    int start = mHeadLayout.getPaddingTop();
                    int end = top;
                    doHeaderAnimator(start, end);
                    refreshUI();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //定义一个方法，让别人去调用，用来实现完成刷新后，更新ui的操作
    public void refreshFinish() {
        if (isLoadingMore) {//上拉加载刷新完成
            Log.d(TAG, "隐藏上拉加载的UI");
            //隐藏UI
            doFooterAnimation(mFooterView.getPaddingTop(), -mFooterViewHeight);
            //隐藏完UI后，说明一次上拉加载完成了，就把是否处于上拉加载这个标记改为false。然后等待用户下一次滑动
            //滑动屏幕去上拉加载
            isLoadingMore = false;
        } else {//下拉加载刷新完成
            //1，设置刷新头的paddingtop值隐藏刷新头
            int start = mHeadLayout.getPaddingTop();
            int end = -mRefreshHeight;
            doHeaderAnimator(start, end);
//        mHeadLayout.setPadding(0, -mRefreshHeight, 0, 0);
            //2，把正在刷新的标记，更改为下拉刷新
            mCurrentState = STATE_PULL_DOWN;
            refreshUI();
            //3，设置刷新时间
            Log.d(TAG, "更新下拉刷新的时间");
            mTvDate.setText("刷新时间" + getTimeString(System.currentTimeMillis()));
        }
    }

    //滑动监听
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        获取最后一个可见的条目，如果这个条目是listview的最后一个条目，并且这时候listview处于滑动状态的话
        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {//获取可见的最后一个条目
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE//处于闲置状态
                    || scrollState == OnScrollListener.SCROLL_STATE_FLING) {//处于飞速滑动状态
                if (isLoadingMore) {//加了这个if后，说明一旦加载过一次数据，标记就变为了true，第二次就执行不到后面的内容了，
                    //就执行不到mListener.onLoadingMore();也就不会打印里面的log日志了。
                    return;
                }
                Log.d(TAG, "加载更多");
                int start = mFooterView.getPaddingTop();
                int end = 0;
                doFooterAnimation(start, end);//慢慢显示上拉加载的UI
                isLoadingMore = true;
                //把拿到的数据添加到listview后面


                if (mListener != null) {
                    mListener.onLoadingMore();
                }


            }
        }

    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
    /**
     * 做底部动画的操作
     *
     * @param start
     * @param end
     */
    private void doFooterAnimation(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mFooterView.setPadding(0, value, 0, 0);
                if (isLoadingMore) {
                    setSelection(getAdapter().getCount());
                }

            }
        });

    }
    /**
     * 刷新头做动画的方法
     *
     * @param start
     * @param end
     */
    private void doHeaderAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mHeadLayout.setPadding(0, value, 0, 0);
            }
        });
    }

    /**
     * 定义一个格式化时间的方法
     *
     * @param time
     * @return
     */
    public String getTimeString(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    //定义一个监听的方法，只要另外一个类调用了这个方法，就会把一个实现了这个接口带方法的类，传给这个类，
// 这个类拿到这个接口对象后，就能选择在什么时候调用他的这个回调方法。
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }
    /**
     * 定义一个回调接口，接口里面有一个正在刷新时应该执行的操作，比如重新去获取数据之类的
     */
    public interface OnRefreshListener {

        void onRefreshing();

        void onLoadingMore();
    }

    /**
     * 更新UI的方法
     */
    private void refreshUI() {
        switch (mCurrentState) {//根据所处的状态来改变UI
            case STATE_PULL_DOWN:
                //1,文本设置
                mTvState.setText("下拉刷新");
                //2，进度条设置
                mPbloading.setVisibility(INVISIBLE);
                //3，箭头设置
                mIvArrow.setVisibility(VISIBLE);
                //箭头动画
                mIvArrow.startAnimation(up2downAni);
                break;
            case STATE_RELEASE_REFRESH:
                //1,文本设置
                mTvState.setText("松开刷新");
                //2，进度条设置
                mPbloading.setVisibility(INVISIBLE);
                //3，箭头设置
                mIvArrow.setVisibility(VISIBLE);
                //箭头动画
                mIvArrow.startAnimation(down2upAni);

                break;
            case STATE_REFRESHING:
                mIvArrow.clearAnimation();//让箭头隐藏
                //1,文本设置
                mTvState.setText("正在刷新");
                //2，进度条设置
                mPbloading.setVisibility(VISIBLE);//圆形进度条显示出来
                //3，箭头设置
                mIvArrow.setVisibility(INVISIBLE);//箭头隐藏


                break;
            default:
                break;

        }

    }
}
