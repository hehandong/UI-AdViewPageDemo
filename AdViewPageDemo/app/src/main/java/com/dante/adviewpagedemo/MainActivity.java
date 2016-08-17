package com.dante.adviewpagedemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ViewPager mViewPager;
	private TextView tvTitle;
	private LinearLayout llContainer;// 填充小圆点的父控件

	private int mLastPointPos;// 记录上一次圆点的位置

	// 要展示的图片id集合
	private final int[] mImageIds = new int[] { R.drawable.img_qinglong, R.drawable.img_zhuque,
			R.drawable.img_xiaowu, R.drawable.img_hunter, R.drawable.img_landscape};

	// 图片标题集合
	private final String[] mImageDes = { "我是青龙", "我是朱雀",
			"我是玄武", "我是怪物猎人", "风景" };

	// 此Handler专门处理轮播条的自动切换
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int currentItem = mViewPager.getCurrentItem();
			mViewPager.setCurrentItem(++currentItem);// 设置当前页面为下一页

			mHandler.sendEmptyMessageDelayed(0, 3000);// 延时3秒后发送消息,自动更新轮播条位置
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mViewPager = (ViewPager) findViewById(R.id.vp);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		llContainer = (LinearLayout) findViewById(R.id.ll_container);

		MyPagerAdapter adapter = new MyPagerAdapter();
		mViewPager.setAdapter(adapter);

		// 初始化第一页的位置, 跳到了第5000的位置, 保证了一开始就可以向左滑动
		mViewPager.setCurrentItem(mImageIds.length * 1000);
		tvTitle.setText(mImageDes[0]);

		// 设置滑动监听
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			// 某一页被选中时调用
			@Override
			public void onPageSelected(int position) {
				int pos = position % mImageDes.length;// 获取图片位置

				// 更新标题内容
				tvTitle.setText(mImageDes[pos]);

				// 将当前圆点设置为选中状态
				ImageView ivPoint = (ImageView) llContainer.getChildAt(pos);
				ivPoint.setEnabled(true);

				// 将上一个圆点设置为不选中状态
				llContainer.getChildAt(mLastPointPos).setEnabled(false);

				// 重新设置上次圆点的位置
				mLastPointPos = pos;
			}

			// 滑动时调用
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			// 滑动状态发生变化
			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		// 初始化小圆点
		for (int i = 0; i < mImageIds.length; i++) {
			ImageView ivPoint = new ImageView(this);
			ivPoint.setImageResource(R.drawable.point_selecter);

			// 默认第一个圆点选中,其他不选中
			if (i != 0) {
				ivPoint.setEnabled(false);
			} else {
				ivPoint.setEnabled(true);
			}

			// 初始化布局参数
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			if (i != 0) {
				params.leftMargin = 5;// 增加圆点之间的边距
			}

			ivPoint.setLayoutParams(params);// 设置布局参数
			llContainer.addView(ivPoint);// 给线性布局添加孩子
		}

		// 延时3秒后发送消息,自动更新轮播条位置
		mHandler.sendEmptyMessageDelayed(0, 3000);

		// 设置触摸监听
		mViewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					System.out.println("ACTION_DOWN");
					mHandler.removeCallbacksAndMessages(null);// 删除所有消息,停止广告条自动切换
					break;
				case MotionEvent.ACTION_UP:
					System.out.println("ACTION_UP");
					mHandler.sendEmptyMessageDelayed(0, 3000);// 继续自动切换广告条
					break;

				default:
					break;
				}

				return false;// 这里需要返回false, 不能消耗掉事件,
								// 这样的话ViewPager才能够响应触摸滑动的事件,页面跟随手指移动
			}
		});
	}

	/**
	 * ViewPager的数据适配器
	 * 
	 * @author Kevin
	 * 
	 */
	class MyPagerAdapter extends PagerAdapter {

		/**
		 * item的数量
		 */
		@Override
		public int getCount() {
//			 return mImageIds.length;
			return Integer.MAX_VALUE;
		}

		/**
		 * 判断要绘制的view和object之间的联系 object: 就是instantiateItem返回的object
		 */
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		/**
		 * 初始化布局
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// System.out.println("instantiateItem:" + position);

			// 初始化ImageView
			ImageView view = new ImageView(getApplicationContext());
			view.setImageResource(mImageIds[position % mImageIds.length]);
			view.setScaleType(ScaleType.FIT_XY);// 图片宽高填充父窗体

			// 将view对象添加到容器中
			container.addView(view);
			return view;
		}

		/**
		 * 销毁特定位置上的view object: 就是instantiateItem返回的object
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// System.out.println("destroyItem:" + position);
			container.removeView((View) object);// 从容器中删除view对象
		}
	}
}
