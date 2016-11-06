package com.myapp.refreshlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myapp.refreshlistview.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RefreshListView extends ListView {

	private static final String TAG = "RefreshListView";
	private SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private int mHeaderViewHeight;//头布局的高度  72 像素
	private int mDownY;//按下去的点的y的值
	private View mHeaderView;//头布局对象
	
	private static final int DOWN_PULL=1;//下拉刷新
	private static final int RELEASE_REFRESH=2;//释放刷新
	private static final int REFRESHING=3;//正在刷新
	private int mCurrentRefreshState=DOWN_PULL;//当前刷新状态
	private TextView mDesTv;
	private TextView mlastTimeTv;
	private ImageView mArrowIv;
	private RotateAnimation mUpRotateAnimation;
	private RotateAnimation mDownRotateAnimation;
	private ProgressBar mProgressBar;
	
	
	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		
		initAnimaiton();
		
	}
 

	public RefreshListView(Context context) {
		super(context);
		
		initHeaderView();
		initAnimaiton();
	}
	
	//初始化动画
	private void initAnimaiton() {
		mUpRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mUpRotateAnimation.setDuration(500);
		mUpRotateAnimation.setFillAfter(true);
		
		
		mDownRotateAnimation = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mDownRotateAnimation.setDuration(500);
		mDownRotateAnimation.setFillAfter(true);
		
	}

    //初始化ListView的头部
	private void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.layout_header_view, null);
		mArrowIv = (ImageView) mHeaderView.findViewById(R.id.arrow_iv);
		mProgressBar = (ProgressBar)mHeaderView.findViewById(R.id.refresh_pb);
		mDesTv = (TextView)mHeaderView.findViewById(R.id.des_tv);
		mlastTimeTv = (TextView)mHeaderView.findViewById(R.id.last_refresh_time_tv);
		//1.3对头布局赋初值
		mProgressBar.setVisibility(View.INVISIBLE);
		mlastTimeTv.setText(getCurrentTime());
		
		//1.4 往ListView设置头布局
		this.addHeaderView(mHeaderView);//往ListView中添加头布局
		
		/**
		 * 1.5  ：头布局不可见
		 *  方式1： ListView移除头布局
		 *  方式2：设置padding的方式
		 */
//		this.removeHeaderView(mHeaderView);
		
		//取得头布局的高度
		/**测量控件
		 * widthMeasureSpec:宽度的测量规格  
		 *   UNSPECIFIED:没有约束的规格
		 *   AT_MOST:最多分配多少空间
		 *   EXACTLY:具体的值
		 *   
		 * heightMeasureSpec：高度的测量规格
		 */
		mHeaderView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
	}
    //取得当前的时间
	private String getCurrentTime() {
		return dateFormat.format(new Date());
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN://按下去事件
			mDownY = (int) ev.getY();
			
			break;
		case MotionEvent.ACTION_MOVE://移动事件
			int moveY=(int) ev.getY();
			int dy=moveY-mDownY;//y方向的偏移量
			int paddingTop=-mHeaderViewHeight+dy;  // -72 + 20  = -52
			int firstVisiblePosition = getFirstVisiblePosition();
			/**显示头布局的条件
			 * 1. 第一个可见的列表项的位置为0
			 * 2. dy必须往下拉，即dy>0
			 * 
			 */
			if(paddingTop>-mHeaderViewHeight&&firstVisiblePosition==0){
				 // 假如完全显示头布局，且当前的状态为下拉则转换为 释放刷新状态 ，否则为 下拉刷新
				if(paddingTop>0&&mCurrentRefreshState==DOWN_PULL){
					Log.i(TAG, "释放刷新");
					mCurrentRefreshState=RELEASE_REFRESH;
					refreshHeaderViewState();
				}else if (paddingTop<0&&mCurrentRefreshState==RELEASE_REFRESH){
					Log.i(TAG, "下拉刷新");
					mCurrentRefreshState=DOWN_PULL;
					refreshHeaderViewState();
				}
				mHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP://抬起事件
			if(mCurrentRefreshState==DOWN_PULL){
				mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
			}else if(mCurrentRefreshState==RELEASE_REFRESH){
				//转为正在刷新状态
				mCurrentRefreshState=REFRESHING;
				//完全显示头布局
				mHeaderView.setPadding(0, 0, 0, 0);
				refreshHeaderViewState();
			}
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);//采用listview默认的样式来滚动列表项
	}
   //头布局的状态刷新
	private void refreshHeaderViewState() {
		switch (mCurrentRefreshState) {
		case DOWN_PULL://下拉刷新状态
			mDesTv.setText("下拉刷新");
			mArrowIv.startAnimation(mDownRotateAnimation);
			
			break;
		case RELEASE_REFRESH://释放刷新状态
			mDesTv.setText("释放刷新");
			mArrowIv.startAnimation(mUpRotateAnimation);
			
			break;
		case REFRESHING://正在刷新状态
			mDesTv.setText("正在刷新...");
			//隐藏箭头
			mArrowIv.clearAnimation();//清除动画
			mArrowIv.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mlastTimeTv.setText(getCurrentTime());//设置最后刷新的时间
			if(mOnRefreshListener!=null){
				mOnRefreshListener.refresh();//调用刷新方法
			}
			break;

		default:
			break;
		}
		
	}
	
	public void refreshFinish(){
		//todo 
		mDesTv.setText("下拉刷新");
		mCurrentRefreshState=DOWN_PULL;
		mArrowIv.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

	}
	
	
	/******************自定义接口，实现回调方法*******************/
	public static interface OnRefreshListener{
		public void refresh();
	}
	
	private OnRefreshListener mOnRefreshListener;
	
	public void setOnRefreshListener(OnRefreshListener listener){
		mOnRefreshListener=listener;
	}
	
	
	

}
