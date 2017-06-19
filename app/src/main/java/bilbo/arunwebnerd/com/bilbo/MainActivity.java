package bilbo.arunwebnerd.com.bilbo;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements FirstFragment.OnInputUpdateListener{
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
  //  private SectionsPagerAdapter mSectionsPagerAdapter;
	private MyPagerAdapter mPageAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
	
	private static final String TAG = "MainActivity";
	
	private Bundle secondFragBundle;
	
	private int numPeople;
	private int tipPercent;
	private float billTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPageAdapter = new MyPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPageAdapter);
		

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
		
		secondFragBundle = new Bundle();
		
		tipPercent = getResources().getInteger(R.integer.tip_default);
		billTotal = Float.parseFloat( getResources().getString(R.string.total_default));
		numPeople = getResources().getInteger(R.integer.numpeople_default);
		// Initialise bundle before second fragment is created
		onInputUpdate(numPeople, tipPercent, billTotal);
		

    }
	private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

				case 0: return FirstFragment.newInstance("FirstFragment, Instance 1");
				case 1:SecondFragment newFragment = SecondFragment.newInstance("SecondFragment, Instance 1");
					Log.d(TAG, "secondFragBundled");
					
					//secondFragBundle = getSupportFragmentManager().findFragmentById(R.id.pageview)
					newFragment.setArguments(secondFragBundle); 
					return newFragment;
				default: return FirstFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }       
    }

	@Override
	public void onInputUpdate(int numPeeps, int tip, float total)
	{
		// TODO: Need to pass the new data to the second fragment
		Log.d(TAG, "onInputUpdate - bill: " + total + " - tip: " + tip + " - Peeps: " + numPeeps);
		secondFragBundle.putInt("tip", tip);
		secondFragBundle.putInt("people", numPeeps);
		secondFragBundle.putFloat("total", total);
	}


	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
