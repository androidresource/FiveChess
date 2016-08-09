package com.example.fivechess;

import com.example.fivechess.widget.fivechessPanel;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * <p>
 * <b>Description:</b> 五子棋
 * </p>
 * <p>
 * <b>ClassName:</b> FiveChess
 * </p>
 * 
 * @author NingFeifei
 *         <p>
 *         <b>date</b> 2016-8-8 下午12:00:20
 *         </p>
 */
public class FiveChessActivity extends Activity {
	private fivechessPanel panel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_five_chess);
		panel = (fivechessPanel) findViewById(R.id.panel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			panel.start();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
