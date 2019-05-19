package com.redbear.chat;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import graphes.GraphHandler;
import graphes.MonitorGraph;
import graphes.ProcessData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Chat extends Activity {
	private final static String TAG = Chat.class.getSimpleName();
	LineChart lineChart;

	private LineChart graphLayout;
	private LineChart dataGraph;
	private MonitorGraph monitorGraph;
    private int graphCounter = 0;
	public static final String EXTRAS_DEVICE = "EXTRAS_DEVICE";
	private TextView tv = null;
	private EditText et = null;
	private Handler handler;
	private Button btn = null;
	private Button colorchange;
	private String mDeviceName;
    private static final float VISIBLE_VALUES = 20f;
	private boolean ready;
	float currentval = (float) 0.00;
	private String mDeviceAddress;
	private RBLService mBluetoothLeService;
    private ArrayList<Entry> indexEntries = new ArrayList<>();
	private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
    private long startTime;
    private ArrayList<String> xAxis = new ArrayList<>();
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
									   IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				getGattService(mBluetoothLeService.getSupportedGattService());
			} else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);

		tv = (TextView) findViewById(R.id.textView);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		//et = (EditText) findViewById(R.id.editText);
		//btn = (Button) findViewById(R.id.send);
		/*btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BluetoothGattCharacteristic characteristic = map
						.get(RBLService.UUID_BLE_SHIELD_TX);

				String str = et.getText().toString();
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				ready = true;
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);

				et.setText("");
			}
		});
		*/

	//	if(ready)
	//	{
	//		graphes.ProcessData.process(currentval);
	//		ready = false;
	//	}
		Intent intent = getIntent();

		mDeviceAddress = intent.getStringExtra(Device.EXTRA_DEVICE_ADDRESS);
		mDeviceName = intent.getStringExtra(Device.EXTRA_DEVICE_NAME);

		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
/*

		lineChart = (LineChart) findViewById(R.id.lineChart);

		ArrayList<String> xAXES = new ArrayList<>();
		//ArrayList<Entry> yAXESsin = new ArrayList<>();
		ArrayList<Entry> yAXEScos = new ArrayList<>();
		double x = 0 ;
		int numDataPoints = 1000;
		for(int i=0;i<numDataPoints;i++){
			//float sinFunction = Float.parseFloat(String.valueOf(Math.sin(x)));
			float cosFunction = Float.parseFloat(String.valueOf(Math.cos(x)));
			x = x + 0.1;
			//yAXESsin.add(new Entry(sinFunction,i));
			yAXEScos.add(new Entry(cosFunction,i));
			xAXES.add(i, String.valueOf(x));
		}
		String[] xaxes = new String[xAXES.size()];
		for(int i=0; i<xAXES.size();i++){
			xaxes[i] = xAXES.get(i).toString();
		}

		ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

		final LineDataSet lineDataSet1 = new LineDataSet(yAXEScos,"cos");
		lineDataSet1.setDrawCircles(false);
		lineDataSet1.setColor(Color.GREEN);
		/*
		LineDataSet lineDataSet2 = new LineDataSet(yAXESsin,"sin");
		lineDataSet2.setDrawCircles(false);
		lineDataSet2.setColor(Color.RED);
		*/
	/*
			lineDataSets.add(lineDataSet1);
		//lineDataSets.add(lineDataSet2);

		lineChart.setData(new LineData(xaxes,lineDataSets));

		lineChart.setVisibleXRangeMaximum(65f);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.changecolor);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lineDataSet1.setColor(Color.GREEN);
                } else {
                    lineDataSet1.setColor(Color.RED);
                }
            }
        });
        */

		handler = new Handler()
		{
			public void handleMessage(Message message)
			{
				switch (message.what)
				{
					case GraphHandler.Progress.READY:
						// Receives values from the ProcessData class
						// Updates the graph with those values
						currentval = ProcessData.returnCurrentVal();
						updateGraph(currentval);
						break;
					default:
						Log.v(TAG, "Message handle error");
				}
			}
		};


		//Sets up the graph
		graphLayout = (LineChart) findViewById(R.id.chart);
		dataGraph = new LineChart(this);
		monitorGraph = new MonitorGraph();

		dataGraph = monitorGraph.createChart(dataGraph);
		graphLayout.addView(dataGraph);
    }




	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();

			System.exit(0);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBluetoothLeService.disconnect();
		mBluetoothLeService.close();

		System.exit(0);
	}

	private void updateGraph(float indexFloat) {
          // Move to a new set of values
		//

		//
        graphCounter++;

        // Each entry list accumulates every new value
        indexEntries.add(new Entry(indexFloat, graphCounter));
       // thumbEntries.add(new Entry(thumbFloat, graphCounter));

        // Gets the current time (in seconds) and adds it to the graph's x-axis
        xAxis.add("" + ((System.currentTimeMillis() - startTime) / 1000));

        // Creates line graph data with the updated entry lists
        LineDataSet indexDataSet = new LineDataSet(indexEntries, "Index");
        //LineDataSet thumbDataSet = new LineDataSet(thumbEntries, "Thumb");
        indexDataSet.setColor(Color.BLUE);
       // thumbDataSet.setColor(Color.GREEN);

        // Refreshes the graph
        dataGraph.invalidate();

        // Adds all of the line data to a single data set
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(indexDataSet);
      //  dataSets.add(thumbDataSet);

        // Updates the data that the graph uses
        LineData lineData = new LineData(xAxis, dataSets);
        dataGraph.setData(lineData);
        dataGraph.notifyDataSetChanged();

        // Arranges the graph to only show the latest values
        dataGraph.setVisibleXRangeMaximum(VISIBLE_VALUES);
        dataGraph.moveViewToX(graphCounter);

        // Checks to see if the user has completed one repetition
        //checkRep(indexFloat);
	}

	private void displayData(byte[] byteArray) {
		if (byteArray != null) {
			String data = new String(byteArray);
			//float currentval = stringtofloat(data);
			currentval = parseWithDefault(data);
			tv.append(data);
			// find the amount we need to scroll. This works by
			// asking the TextView's internal layout for the position
			// of the final line and then subtracting the TextView's height
			final int scrollAmount = tv.getLayout().getLineTop(
					tv.getLineCount())
					- tv.getHeight();
			// if there is no need to scroll, scrollAmount will be <=0
			if (scrollAmount > 0)
				tv.scrollTo(0, scrollAmount);
			else
				tv.scrollTo(0, 0);
		}
        updateGraph(currentval);
	}
/*
	private int stringtoint(String data){
		int intData = Integer.parseInt(data);
		return intData;
	}
*/
	private float parseWithDefault(String s) {
			try {
			return Float.parseFloat(s);
			}
			catch (NumberFormatException e) {
			// It's OK to ignore "e" here because returning a default value is the documented behaviour on invalid input.
			return (float)0;
	}
}
    private float stringtofloat(String data){
        float intData = Float.valueOf(data);
        return intData;
}
	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		BluetoothGattCharacteristic characteristic = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
		map.put(characteristic.getUuid(), characteristic);

		BluetoothGattCharacteristic characteristicRx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
		mBluetoothLeService.setCharacteristicNotification(characteristicRx,
				true);
		mBluetoothLeService.readCharacteristic(characteristicRx);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

		return intentFilter;
	}
}
