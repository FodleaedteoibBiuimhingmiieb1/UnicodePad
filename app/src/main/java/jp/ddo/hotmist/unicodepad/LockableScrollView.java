package jp.ddo.hotmist.unicodepad;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView
{
	boolean inmove = false;
	PageAdapter adapter;
	View lockview;
	boolean over;

	public LockableScrollView(Context context)
	{
		super(context);
	}

	public LockableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public LockableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (!over)
			return false;

		if (inmove && ev.getActionMasked() == MotionEvent.ACTION_UP)
		{
			inmove = false;
			return true;
		}

		boolean hit = false;
		View v = adapter.getGridView();
		if (v != null && v.getVisibility() == VISIBLE)
		{
			Rect rc = new Rect();
			v.getGlobalVisibleRect(rc);
			if (rc.contains((int)ev.getRawX(), (int)ev.getRawY()))
				hit = true;
		}

		if (!inmove && !hit)
			return inmove = super.onInterceptTouchEvent(ev);

		inmove = super.onInterceptTouchEvent(ev);

		return false;
	}

	public void setAdapter(PageAdapter adapter)
	{
		this.adapter = adapter;
	}

	public void setLockView(View lockview, boolean over)
	{
		this.lockview = lockview;
		this.over = over;
		if (lockview == null || getHeight() == 0)
			return;
		if (!over)
			scrollTo(0, 0);
		lockview.getLayoutParams().height = over ? getHeight() : getHeight() - lockview.getTop();
		lockview.requestLayout();
	}

	@Override
	protected void onSizeChanged(int w, final int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		final int pos = getScrollY();
		post(new Runnable()
		{
			@Override
			public void run()
			{
				if (lockview == null)
					return;
				lockview.getLayoutParams().height = over ? h : h - lockview.getTop();
				lockview.requestLayout();
				lockview.post(new Runnable()
				{
					@Override
					public void run()
					{
						scrollTo(0, pos);
					}
				});
			}
		});
	}

	boolean scroll = false;

	@Override
	public void requestChildFocus(View child, View focused)
	{
		scroll = false;
		super.requestChildFocus(child, focused);
		scroll = true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		scroll = false;
		super.onLayout(changed, l, t, r, b);
		scroll = true;
	}

	@Override
	public void scrollTo(int x, int y)
	{
		if (scroll)
			super.scrollTo(x, y);
	}
}
