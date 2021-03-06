package com.baoyz.swipemenuexpandablelistview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuLayout;
import com.baoyz.swipemenulistview.SwipeMenuView;

/**
 * 可以侧滑的ExpandableListView
 * 复制SwipeMenuListView修改
 */
public class SwipeMenuExpandableListView extends ExpandableListView {

	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;

	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;
	private int mDirection = 1;//swipe from right to left by default

	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	private int mTouchPosition;
	private SwipeMenuLayout mTouchView;
	private OnSwipeListener mOnSwipeListener;

	private SwipeMenuCreator mMenuCreator;
	private OnExpandableMenuItemClickListener mOnExpandableMenuItemClickListener;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeMenuExpandableListView(Context context) {
		super(context);
		init();
	}

	public SwipeMenuExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SwipeMenuExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(new SwipeMenuExpandableAdapter(adapter, getContext()) {
			@Override
			public void createMenu(SwipeMenu menu) {
				if (mMenuCreator != null) {
					mMenuCreator.create(menu);
				}
			}

			@Override
			public void onExpandableItemClick(SwipeMenuView view,
					SwipeMenu menu, int index) {
				boolean flag = false;
				if (mOnExpandableMenuItemClickListener != null) {
					flag = mOnExpandableMenuItemClickListener
							.onExpandableMenuItemClick(view.getGroupPostion(),
									view.getChildPosition(), menu, index);
				}
				if (mTouchView != null && !flag) {
					mTouchView.smoothCloseMenu();
				}
			}
		});
	}

	public void setCloseInterpolator(Interpolator interpolator) {
		mCloseInterpolator = interpolator;
	}

	public void setOpenInterpolator(Interpolator interpolator) {
		mOpenInterpolator = interpolator;
	}

	public Interpolator getOpenInterpolator() {
		return mOpenInterpolator;
	}

	public Interpolator getCloseInterpolator() {
		return mCloseInterpolator;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//在拦截处处理，在滑动设置了点击事件的地方也能swip，点击时又不能影响原来的点击事件
		int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mDownX = ev.getX();
				mDownY = ev.getY();
				boolean handled = super.onInterceptTouchEvent(ev);
				mTouchState = TOUCH_STATE_NONE;
				mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
				View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

				//只在空的时候赋值 以免每次触摸都赋值，会有多个open状态
				if (view instanceof SwipeMenuLayout) {
					//如果有打开了 就拦截.
					if (mTouchView != null && mTouchView.isOpen() && !inRangeOfView(mTouchView.getMenuView(), ev)) {
						return true;
					}
					mTouchView = (SwipeMenuLayout) view;
					mTouchView.setSwipeDirection(mDirection);
				}
				//如果摸在另外个view
				if (mTouchView != null && mTouchView.isOpen() && view != mTouchView) {
					handled = true;
				}

				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				return handled;
			case MotionEvent.ACTION_MOVE:
				float dy = Math.abs((ev.getY() - mDownY));
				float dx = Math.abs((ev.getX() - mDownX));
				if (Math.abs(dy) > MAX_Y || Math.abs(dx) > MAX_X) {
					//每次拦截的down都把触摸状态设置成了TOUCH_STATE_NONE 只有返回true才会走onTouchEvent 所以写在这里就够了
					if (mTouchState == TOUCH_STATE_NONE) {
						if (Math.abs(dy) > MAX_Y) {
							mTouchState = TOUCH_STATE_Y;
						} else if (dx > MAX_X) {
							mTouchState = TOUCH_STATE_X;
							if (mOnSwipeListener != null) {
								mOnSwipeListener.onSwipeStart(mTouchPosition);
							}
						}
					}
					return true;
				}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
			return super.onTouchEvent(ev);
		int action = MotionEventCompat.getActionMasked(ev);
		action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int oldPos = mTouchPosition;
			mDownX = ev.getX();
			mDownY = ev.getY();
			mTouchState = TOUCH_STATE_NONE;

			mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

			if (mTouchPosition == oldPos && mTouchView != null
					&& mTouchView.isOpen()) {
				mTouchState = TOUCH_STATE_X;
				mTouchView.onSwipe(ev);
				return true;
			}

			View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

			if (mTouchView != null && mTouchView.isOpen()) {
				mTouchView.smoothCloseMenu();
				mTouchView = null;
				return super.onTouchEvent(ev);
			}
			if (view instanceof SwipeMenuLayout) {
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.setSwipeDirection(mDirection);
			}
			if (mTouchView != null) {
				mTouchView.onSwipe(ev);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = Math.abs((ev.getY() - mDownY));
			float dx = Math.abs((ev.getX() - mDownX));
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				getSelector().setState(new int[] { 0 });
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			} else if (mTouchState == TOUCH_STATE_NONE) {
				if (Math.abs(dy) > MAX_Y) {
					mTouchState = TOUCH_STATE_Y;
				} else if (dx > MAX_X) {
					mTouchState = TOUCH_STATE_X;
					if (mOnSwipeListener != null) {
						mOnSwipeListener.onSwipeStart(mTouchPosition);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
					if (!mTouchView.isOpen()) {
						mTouchPosition = -1;
						mTouchView = null;
					}
				}
				if (mOnSwipeListener != null) {
					mOnSwipeListener.onSwipeEnd(mTouchPosition);
				}
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeMenuLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeMenuLayout) view;
				mTouchView.setSwipeDirection(mDirection);
				mTouchView.smoothOpenMenu();
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}

	public void setOnExpandableMenuItemClickListener(
			OnExpandableMenuItemClickListener onExpandableMenuItemClickListener) {
		this.mOnExpandableMenuItemClickListener = onExpandableMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	public static interface OnExpandableMenuItemClickListener {
		boolean onExpandableMenuItemClick(int groupPosition, int childPosition,
									   SwipeMenu menu, int index);
	}

	public static interface OnSwipeListener {
		void onSwipeStart(int position);

		void onSwipeEnd(int position);
	}

	/**
	 * 判断点击事件是否在某个view内
	 *
	 * @param view
	 * @param ev
	 * @return
	 */
	public static boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if (ev.getRawX() < x || ev.getRawX() > (x + view.getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}
}