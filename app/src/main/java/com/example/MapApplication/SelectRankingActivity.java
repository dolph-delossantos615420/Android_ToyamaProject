package com.example.MapApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SelectRankingActivity extends ActionBarActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_ranking);

		findViewById(R.id.btnLikeRanking).setOnClickListener(this);
		findViewById(R.id.btnCommentRanking).setOnClickListener(this);
		findViewById(R.id.btnCreatetimeRanking).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_ranking, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO 遷移先を変更する
		Intent intent = new Intent(SelectRankingActivity.this, RankingActivity.class);

		int id = v.getId();
		switch (id) {
			case R.id.btnLikeRanking:
				System.out.println("=====  Ranking Like  =====");
				intent.putExtra("SearchTag", getString(R.string.SearchTag_RankingLike));
				intent.putExtra("ButtonTag", id);
				startActivity(intent);
				break;
			case R.id.btnCommentRanking:
				System.out.println("=====  Ranking Comment  =====");
				intent.putExtra("SearchTag", getString(R.string.SearchTag_RankingComment));
				intent.putExtra("ButtonTag", id);
				startActivity(intent);
				break;
			case R.id.btnCreatetimeRanking:
				System.out.println("=====  Ranking Date  =====");
				intent.putExtra("SearchTag", getString(R.string.SearchTag_RankingDate));
				intent.putExtra("ButtonTag", id);
				startActivity(intent);
				break;
		}

	}
}
