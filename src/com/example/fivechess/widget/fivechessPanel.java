/**  
 * <p><b>Description:</b> 五子棋自定义View </p>  
 * <p><b>Title:</b> fivechessPanel.java </p>
 * <p><b>Package</b> com.example.fivechess.widget</p> 
 * @author NingFeifei
 * <p><b>date</b> 2016-8-8 下午1:31:13</p> 
 * @version v1.0.0
 */
package com.example.fivechess.widget;

import java.util.ArrayList;

import com.example.fivechess.R;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * <p>
 * <b>Description:</b>
 * </p>
 * <p>
 * <b>ClassName:</b> fivechessPanel
 * </p>
 * 
 * @author NingFeifei
 *         <p>
 *         <b>date</b> 2016-8-8 下午1:31:13
 *         </p>
 */
public class fivechessPanel extends View {
	private int mPanelWidth;
	private float mLineHeight;
	private int MAX_LINE = 10;
	private int MAX_COUNT_IN_LINE=5;

	private Paint mPaint = new Paint();
	
	private Bitmap mWhitePiece;
	private Bitmap mBlackPiece;
	
	private float ratioPieceLineHeight = 3*1.0f/4;
	
	/** 当前为白棋*/ 
	private boolean mIsWhite = true;
	private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
	private ArrayList<Point> mBlackArray = new ArrayList<Point>();
	
	private boolean mIsGameOver;
	private boolean mIsWhiteWinner;
	

	public fivechessPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public fivechessPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		//setBackgroundColor(0x33ff0000);
		init();
	}

	private void init() {
		mPaint.setColor(0x88000000);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		
		mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.wp);
		mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.bp);
	}

	public fivechessPanel(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int width = Math.min(widthSize, heightSize);

		if (widthMode == MeasureSpec.UNSPECIFIED) {// 适配外层是ScrollView的情况
			width = heightSize;
		} else if (heightMode == MeasureSpec.UNSPECIFIED) {
			width = widthSize;
		}
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mPanelWidth = w;
		mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
		
		int pieceWidth = (int) (mLineHeight*ratioPieceLineHeight);
		
		mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
		mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
	}
	
	double downX =0, downY = 0,upX = 0,upY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mIsGameOver){
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
		case MotionEvent.ACTION_MOVE:
			return true;
		case MotionEvent.ACTION_UP:
			upX = event.getX();
			upY = event.getY();
			//当在屏幕上面有拖动的时候，拖动距离大于mLineHeight不下棋子
			if(Math.sqrt((downX-upX)*(downX-upX)+(downY-upY)*(downY-upY))>mLineHeight/2){
				return false;
			}else{
			}
			
			int x = (int)upX;
			int y = (int)upY;
			Point p = getValidPoint(x,y);
			
			if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
				return false;
			}
			
			if(mIsWhite){
				mWhiteArray.add(p);
			}else{
				mBlackArray.add(p);
			}
			invalidate();//重绘
			mIsWhite = !mIsWhite;
			return true;
		}
		return false;
	}

	private Point getValidPoint(int x, int y) {
		return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBorad(canvas);
		drawPieces(canvas);
		checkGameOver();
	}

	private void checkGameOver() {
		boolean whiteWin = checkFiveInLine(mWhiteArray);
		boolean blackWin = checkFiveInLine(mBlackArray);
		if(whiteWin||blackWin){
			mIsGameOver = true;
			mIsWhiteWinner = whiteWin;
			
			String text = mIsWhiteWinner?"白棋胜利":"黑棋胜利";
			tipsGameOver(text);
		}
		if((mWhiteArray.size()+mBlackArray.size())==MAX_LINE*MAX_LINE){
			tipsGameOver("平局");
		}
	}

	private void tipsGameOver(String text) {
		Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
		new AlertDialog.Builder(getContext())
		.setTitle("棋局结束")
		.setMessage(text)
		.setCancelable(false)
		.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				start();
			}
		})
		.setNeutralButton("取消", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
	}

	private boolean checkFiveInLine(ArrayList<Point> points) {
		for (Point p : points) {
			int x = p.x;
			int y = p.y;
			
			boolean win = checkHorizontal(x,y,points);
			if(win){
				return true;
			}
			win = checkVertical(x,y,points);
			if(win){
				return true;
			}
			win = checkLeftDiagonal(x,y,points);
			if(win){
				return true;
			}
			win = checkRightDiagonal(x, y, points);
			if(win){
				return true;
			}
		}
		return false;
	}

	/** 
	 * <p><b>Description:</b> 水平方向</p>
	 * <p><b>Title:</b> checkHorizontal </p>
	 * @param x
	 * @param y
	 * @param points
	 * @return  
	 */
	private boolean checkHorizontal(int x, int y, ArrayList<Point> points) {
		int count = 1;
		//左
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x-i, y))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		//右
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x+i, y))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		return false;
	}
	/** 
	 * <p><b>Description:</b> 垂直方向</p>
	 * <p><b>Title:</b> checkHorizontal </p>
	 * @param x
	 * @param y
	 * @param points
	 * @return  
	 */
	private boolean checkVertical(int x, int y, ArrayList<Point> points) {
		int count = 1;
		//上
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x, y-i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		//下
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x, y+i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		return false;
	}
	/** 
	 * <p><b>Description:</b> 左斜方向</p>
	 * <p><b>Title:</b> checkHorizontal </p>
	 * @param x
	 * @param y
	 * @param points
	 * @return  
	 */
	private boolean checkLeftDiagonal(int x, int y, ArrayList<Point> points) {
		int count = 1;
		//下
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x-i, y+i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		//上
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x+i, y-i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		return false;
	}
	/** 
	 * <p><b>Description:</b> 右斜方向</p>
	 * <p><b>Title:</b> checkHorizontal </p>
	 * @param x
	 * @param y
	 * @param points
	 * @return  
	 */
	private boolean checkRightDiagonal(int x, int y, ArrayList<Point> points) {
		int count = 1;
		//上
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x-i, y-i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		//下
		for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
			if(points.contains(new Point(x+i, y+i))){
				count ++;
			}else{
				break;
			}
		}
		if(count == MAX_COUNT_IN_LINE){
			return true;
		}
		
		return false;
	}

	private void drawPieces(Canvas canvas) {
//		canvas.drawBitmap(mBlackPiece, 65f, 65f, null);
//		canvas.drawBitmap(mWhitePiece, 3f, 3f, null);
		for (int i = 0,n = mWhiteArray.size(); i < n; i++) {
			Point whitePoint = mWhiteArray.get(i);
			canvas.drawBitmap(
					mWhitePiece, 
					(whitePoint.x+(1-ratioPieceLineHeight)/2)*mLineHeight, (whitePoint.y+(1-ratioPieceLineHeight)/2)*mLineHeight, null);
		}
		for (int i = 0,n = mBlackArray.size(); i < n; i++) {
			Point blackPoint = mBlackArray.get(i);
			canvas.drawBitmap(
					mBlackPiece, 
					(blackPoint.x+(1-ratioPieceLineHeight)/2)*mLineHeight, (blackPoint.y+(1-ratioPieceLineHeight)/2)*mLineHeight, null);
		}
	}

	private void drawBorad(Canvas canvas) {
		int w = mPanelWidth;
		float lineHeight = mLineHeight;

		for (int i = 0; i < MAX_LINE; i++) {
			int startX = (int) (lineHeight / 2);
			int endX = (int) (w - lineHeight / 2);

			int y = (int) ((0.5 + i) * lineHeight);
			canvas.drawLine(startX, y, endX, y, mPaint);
			canvas.drawLine(y, startX, y, endX, mPaint);
		}
	}
	
	public void start(){
		mWhiteArray.clear();
		mBlackArray.clear();
		mIsGameOver = false;
		mIsWhiteWinner = false;
		mIsWhite = true;
		invalidate();
	}
	
	private static final String INSTANCE = "instance";
	private static final String INSTANCE_GAME_OVER = "instance_game_over";
	private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
	private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
		bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
		bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
		bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
		return bundle;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if(state instanceof Bundle){
			Bundle bundle = (Bundle) state;
			mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
			mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
			mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
		}
		super.onRestoreInstanceState(state);
	}

}
