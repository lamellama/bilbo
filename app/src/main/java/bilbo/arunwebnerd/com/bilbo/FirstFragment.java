package bilbo.arunwebnerd.com.bilbo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import android.widget.LinearLayout;

public class FirstFragment extends Fragment implements  View.OnTouchListener, SeekBar.OnSeekBarChangeListener
{
	private static final String TAG = "FirstFragment";
	private OnInputUpdateListener mCallback;
	private int tipPercent;
	private BigDecimal billTotal;
	private int numPeople;

	private TextView tvNumPeopleSeekDisplay;
	private Button btnNumPeopleMinus;
	private Button btnNumPeoplePlus;
	private TextView tipSeekDisplay;
	private Button btnTipMinus;
	private Button btnTipPlus;
	private Button btnContinue;
	private EditText tvTotal;
	private TextView tvTip;
	private TextView tvBill;
	private TextView tvBillTotal;
	private SeekBar sbNumberPeople;
	private SeekBar spinTipPercent;
	
	private LinearLayout remainderLayout;
	private LinearLayout remainderWarningLayout;
	private TextView remainderTotal;
	
	//private static int decimalPlaces = 20;
	//private static int formattedDecimalPlaces = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View v = inflater.inflate(R.layout.first_frag, container, false);
		
		

		tipPercent = getResources().getInteger(R.integer.tip_default);
		billTotal = new BigDecimal(getContext().getResources().getString(R.string.total_default));
		numPeople = getContext().getResources().getInteger(R.integer.numpeople_default);
		//numPeople++;

		tvTip = (TextView) v.findViewById(R.id.tvPPTipDisplay);
		tvBill = (TextView) v.findViewById(R.id.tvPPBillDisplay);
		tvBillTotal = (TextView) v.findViewById(R.id.tvPPTotalDisplay);
        tvTotal = (EditText) v.findViewById(R.id.tvTotal);
		
		remainderLayout = (LinearLayout) v.findViewById(R.id.remainder_layout);
		remainderWarningLayout = (LinearLayout) v.findViewById(R.id.remainder_warning_layout);
		remainderTotal = (TextView) v.findViewById(R.id.remainderTotal);
		tvTotal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus)
				{
					if (!hasFocus)
					{

						String tote = tvTotal.getText().toString();
						if (tote.length() > 0)
							setBillTotal(new BigDecimal(tote));
						else
							setBillTotal(new BigDecimal(0));	

						updateTotals();
					}
				}
			});

		tvTotal.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s)
				{}
				public void  beforeTextChanged(CharSequence s, int start, int count, int after)
				{}
				public void  onTextChanged(CharSequence s, int start, int before, int count)
				{

					String tote = tvTotal.getText().toString();
					if (tote.length() > 0)
						setBillTotal(new BigDecimal(tote));
					else
						setBillTotal(new BigDecimal(0));	
					updateTotals();
				}

			});

        sbNumberPeople = (SeekBar) v.findViewById(R.id.sbNumPeople);
		sbNumberPeople.setProgress(numPeople);
		tvNumPeopleSeekDisplay = (TextView) v.findViewById(R.id.numPeopleSeekbarDisplay);
		btnNumPeopleMinus = (Button) v.findViewById(R.id.btnNumPeepMinus);
		btnNumPeoplePlus = (Button) v.findViewById(R.id.btnNumPeepPlus);
		tvNumPeopleSeekDisplay.setText(Integer.toString(numPeople));
		btnNumPeoplePlus.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
													@Override
													public void onClick(View view)
													{
														// the code to execute repeatedly
														sbNumberPeople.setProgress(sbNumberPeople.getProgress() + 1);
													}
												}));
		btnNumPeopleMinus.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
													 @Override
													 public void onClick(View view)
													 {
														 // the code to execute repeatedly
														 sbNumberPeople.setProgress(sbNumberPeople.getProgress() - 1);
													 }
												 }));
		sbNumberPeople.setOnSeekBarChangeListener(this);

        spinTipPercent = (SeekBar) v.findViewById(R.id.sbTipPercent);
		tipSeekDisplay = (TextView) v.findViewById(R.id.tipSeekbarDisplay);
		btnTipMinus = (Button) v.findViewById(R.id.btnTipMinus);
		btnTipPlus = (Button) v.findViewById(R.id.btnTipPlus);

		btnTipPlus.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
											  @Override
											  public void onClick(View view)
											  {
												  // the code to execute repeatedly
												  spinTipPercent.setProgress(spinTipPercent.getProgress() + 1);
											  }
										  }));
		btnTipMinus.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
											   @Override
											   public void onClick(View view)
											   {
												   // the code to execute repeatedly
												   spinTipPercent.setProgress(spinTipPercent.getProgress() - 1);
											   }
										   }));
		spinTipPercent.setOnSeekBarChangeListener(this);

		btnContinue = (Button) v.findViewById(R.id.btnContinue);
		btnContinue.setOnTouchListener(this);
		
		updateTotals();
		
        return v;
    }
	
	NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.getDefault());

	//Set and format total variable to local currency
	private BigDecimal setBillTotal(BigDecimal total)
	{
		fmt.format(total);
		billTotal = total;
		return billTotal;
	} 

	private BigDecimal getBillTotal()
	{
		return billTotal;
	} 

	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		switch (p1.getId())
		{
			case R.id.btnContinue:
				mCallback.onInputUpdate(numPeople, tipPercent, getBillTotal());
				break;
		}
		return false;
	}


    public void onNothingSelected(AdapterView<?> parent)
	{}

	//Update textViews values
	private void updateTotals()
	{
		BigDecimal total = new BigDecimal(0.0);
		total = getBillTotal();
		BigDecimal remainder = new BigDecimal(0);
		if ((total.compareTo(new BigDecimal(0)) > 0) && (numPeople > 0)){
			total = total.divide(new BigDecimal(numPeople), PerPersonValue.formattedDecimalPlaces, RoundingMode.DOWN);
			remainder = getBillTotal().subtract(total.multiply(new BigDecimal(numPeople)));
			}
		if(remainder.compareTo(new BigDecimal(0.0)) > 0.0){
			remainderTotal.setText(remainder.toPlainString());
			remainderWarningLayout.setVisibility(View.VISIBLE);
			remainderLayout.setVisibility(View.VISIBLE);
		}
		else{
			remainderWarningLayout.setVisibility(View.INVISIBLE);
			remainderLayout.setVisibility(View.INVISIBLE);
		}
		tvBill.setText(fmt.format(total) + "pp");
		tvTip.setText(Integer.toString(tipPercent) + "%");
		tvBillTotal.setText(fmt.format(total.add( (total.multiply(new BigDecimal(tipPercent))).divide(new BigDecimal(100)))));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		// An item was selected. You can retrieve the selected item using
		//Spinner inputSpinner = (Spinner)parent.getItemAtPosition(pos);
		switch (seekBar.getId())
		{
			case R.id.sbNumPeople:
				Log.d(TAG, "Seekbar numPeople selected: " + seekBar.getProgress());
				if(seekBar.getProgress() < 1)
					seekBar.setProgress(1);
				numPeople = seekBar.getProgress();
				if (tvNumPeopleSeekDisplay != null)
					tvNumPeopleSeekDisplay.setText(Integer.toString(numPeople));
				break;
			case R.id.sbTipPercent:
				Log.d(TAG, "Spinner Tip selected: " + seekBar.getProgress());
				tipPercent = seekBar.getProgress();
				if (tipSeekDisplay != null)
					tipSeekDisplay.setText(Integer.toString(tipPercent));
				break;

		}
		updateTotals();

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{}

	// Container Activity must implement this interface
    public interface OnInputUpdateListener
	{
        public void onInputUpdate(int numPeeps, int tip, BigDecimal total);
    }

    @Override
    public void onAttach(Activity activity)
	{
        super.onAttach(activity);

        // Makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try
		{
            mCallback = (OnInputUpdateListener) activity;
        }
		catch (ClassCastException e)
		{
            throw new ClassCastException(activity.toString()
										 + " must implement OnInputUpdateListener");
        }
    }

    public static FirstFragment newInstance(String text)
	{
        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);
		return f;
	}
}	
