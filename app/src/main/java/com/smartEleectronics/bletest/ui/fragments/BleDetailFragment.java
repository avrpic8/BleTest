package com.smartEleectronics.bletest.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.smartEleectronics.bletest.R;
import com.smartEleectronics.bletest.databinding.FragmentBleDetailBinding;
import com.smartEleectronics.bletest.util.Constants;
import com.smartEleectronics.bletest.util.MyCustomFill;
import com.smartEleectronics.bletest.util.SensorLiveData;
import com.smartEleectronics.bletest.viewModels.DetailViewModel;


public class BleDetailFragment extends Fragment {

    private FragmentBleDetailBinding binding;

    private DetailViewModel detailViewModel;
    private BleDevice bleDevice;

    /// chart variables
    private static final int MAX_ENTRIES = 1000;
    private LineChart mainChart;
    private LineData dataChart;
    private LineDataSet set0;
    private Description desc = new Description();
    String colorChart = "#00ff0000";

    /// orientations sensor
    private SensorManager sensorManager;
    private Sensor gSensor;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bleDevice = getBleDevice();
        binding.setBleDeviceData(bleDevice);

        initChart();
        initViewModel();
        initButtons();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        BleManager.getInstance().stopNotify(
                bleDevice,
                Constants.SERVICE_UUID,
                Constants.CHARACTERISTIC_UUID_TX);
    }


    //////////////////////////////////////////////////////////
    private void initViewModel(){
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        binding.setLifecycleOwner(this);

        detailViewModel.getToastMessage().observe(getViewLifecycleOwner(), s -> {
            showToast(s);
        });

        detailViewModel.receiveDataFromBleDevice(bleDevice);
        detailViewModel.getLiveReceivedData().observe(getViewLifecycleOwner(), data -> {
            String strData = new String(data);
            setLineValue(Integer.parseInt(strData));
            //detailViewModel.addText(binding.edtResponse, strData);
        });

        detailViewModel.getSendingStatus().observe(getViewLifecycleOwner(), sendingStatus -> {
            if(sendingStatus){
                detailViewModel.blinkLED(binding.activeSend, 100, 100);
            }else{
                detailViewModel.stopBlinkLED();
            }
        });
        detailViewModel.getReadingStatus().observe(getViewLifecycleOwner(), readinStatus->{
            detailViewModel.blinkLED(binding.activeReceive, 100, 100);
            detailViewModel.stopBlinkLED();
        });

        detailViewModel.sensorData.observe(getViewLifecycleOwner(), sensorEvent->{
            //setLineValue((int) sensorEvent.values[0] * 10);
        });
    }

    private void initButtons(){
        binding.btnSendData.setOnClickListener(view -> {
            String data = binding.edtWriteCommand.getText().toString();
            detailViewModel.sendDataToBleDevice(bleDevice, data);
        });
    }

    private void initChart(){

        binding.mainChart.getAxisLeft().setDrawGridLines(true);
        binding.mainChart.getAxisRight().setDrawGridLines(true);
        binding.mainChart.getAxisRight().setDrawLabels(false);

        // set x axis
        binding.mainChart.getXAxis().setDrawGridLines(true);
        binding.mainChart.getXAxis().setDrawLabels(true);
        binding.mainChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis xAxis = binding.mainChart.getXAxis();
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        xAxis.setGridColor(ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_light_background));
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(MAX_ENTRIES);


        // set y axis
        YAxis leftAxis = binding.mainChart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        leftAxis.setGridColor(ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_light_background));
        leftAxis.setAxisMaximum(1023f);
        leftAxis.setAxisMinimum(0);


        // add empty data
        LineData data = new LineData();
        binding.mainChart.setData(data);


        // customize charts legend
        Legend mainChartLegend = binding.mainChart.getLegend();
        mainChartLegend.setTextColor(ContextCompat.getColor(getContext(), R.color.black));


        binding.mainChart.setBorderColor(ContextCompat.getColor(getContext(), androidx.cardview.R.color.cardview_light_background));

        binding.mainChart.setTouchEnabled(false);
        binding.mainChart.setDrawBorders(true);
        binding.mainChart.setDragEnabled(false);
        binding.mainChart.getDescription().setEnabled(false);
    }

    private void setLineValue(int inputValue0) {

        dataChart = binding.mainChart.getData();
        if (dataChart != null) {

            set0 = (LineDataSet) dataChart.getDataSetByIndex(0);
            if (set0 == null) {

                set0 = createSet(ColorTemplate.rgb(colorChart), ColorTemplate.rgb(colorChart), "Real Time Plot");
                dataChart.addDataSet(set0);
            }

            if (set0.getEntryCount() == MAX_ENTRIES) {
                set0.removeFirst();
                // change Indexes - move to beginning by 1
                for (Entry entry : set0.getValues())
                    entry.setX(entry.getX() - 1);
            }


            set0.addEntry(new Entry(set0.getEntryCount(), inputValue0));
            dataChart.notifyDataChanged();
            //ChartUtils.removeEntries(set);

            binding.mainChart.notifyDataSetChanged();
            binding.mainChart.invalidate();
            // limit the number of visible entries
            binding.mainChart.setVisibleXRangeMaximum(MAX_ENTRIES);
            //mainChart.moveViewToX(dataChart.getEntryCount());
        }
    }

    private LineDataSet createSet(int colorChart, int colorFill, String plotName) {

        LineDataSet set = new LineDataSet(null, plotName);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_fill);

        //set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colorChart);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setDrawFilled(true);
        set.setFillFormatter(new MyCustomFill());
        set.setFillDrawable(drawable);
        //set.setFillAlpha(255);
        set.setDrawHighlightIndicators(false);
        set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        /*set.setCircleRadius(4f);
        set.setFillColor(colorFill);
        set.setHighLightColor(Color.rgb(244, 117, 117));*/
        return set;
    }

    private BleDevice getBleDevice(){
        BleDevice device = this.getActivity()
                .getIntent()
                .getParcelableExtra(getString(R.string.ble_device_data_key));
        return device;
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}