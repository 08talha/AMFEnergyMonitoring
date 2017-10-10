package com.example.talha.amfenergymonitoring;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public String stdeneme;
    public double[] firstData= new double[1000];;
    public int runSayac=0;
    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    public int sayac2=0;
    public int farkSaat=0;
    public double gelenVeriWaatsaat=0;
    public TarihFarki tarihFarki;
    TextView textInfo, textStatus,akim,gTuketim,wSaat;
    ListView listViewPairedDevice;
    RelativeLayout inputPane;
    Boolean kontrolKart=false;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public ThreadConnectBTdevice myThreadConnectBTdevice;
    public ThreadConnected myThreadConnected;
    public int sayac=0,sayacRe=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MsgDb mbdata = new MsgDb(getApplicationContext());

        textInfo = (TextView)findViewById(R.id.info);
        textStatus = (TextView)findViewById(R.id.status);
        listViewPairedDevice = (ListView)findViewById(R.id.pairedlist);
        akim=(TextView)findViewById(R.id.akimDegeri);
        inputPane = (RelativeLayout) findViewById(R.id.inputpane);
        gTuketim=(TextView)findViewById(R.id.aTuketim);
        wSaat=(TextView)findViewById(R.id.eTuketim);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        boolean Startverilmesi = preferences.getBoolean("Startverilmesi",true);
        if(Startverilmesi){
            Date simdikiZaman = new Date();
            DateFormat df = new SimpleDateFormat("hh:mm:ss");
            String simdikizaman;
            simdikizaman=df.format(simdikiZaman);

            textInfo.setText(" sherad");
            mbdata.deleteTable("gunluk");
            mbdata.deleteTable("saatlik");
            mbdata.insertYillik(1,0.0);
            mbdata.insertSaatlik(1,simdikizaman);
            mbdata.insertGunluk(1,1,"08-08-1994");
            mbdata.insertAylik(1,1,"08-08-1994");
            editor.putBoolean("Startverilmesi", false);
            editor.commit();
        }


        final Button btn =(Button)findViewById(R.id.button);
        final Button btnGrafik =(Button)findViewById(R.id.generateBarChart);
///"@color/colorPrimaryDark"
        btn.setText("Kapat");
        btn.setBackgroundColor(Color.RED);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sayac%2==0){
                    byte[] bytesToSend = "1".getBytes();
                    myThreadConnected.write(bytesToSend);
                    btn.setText("Aç");
                    btn.setBackgroundColor(Color.GREEN);
                }else{
                    byte[] bytesToSend = "0".getBytes();
                    myThreadConnected.write(bytesToSend);
                    btn.setText("Kapat");
                    btn.setBackgroundColor(Color.RED);
                }
                sayac++;
            }
        });
        btnGrafik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarChart();
            }
        });



        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String stInfo = bluetoothAdapter.getName() + "\n" +
                bluetoothAdapter.getAddress();
        textInfo.setText(stInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();
    }

    private void setup() {   //bluetot seçme
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, //listede tıklama olayı
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);
                    Toast.makeText(MainActivity.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress() + "\n"
                                    + "BondState: " + device.getBondState() + "\n"
                                    + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                    + "Class: " + device.getClass(),
                            Toast.LENGTH_LONG).show();

                    textStatus.setText("start ThreadConnectBTdevice");

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    public class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {  //kart id getirme kodu
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);

                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);

                        listViewPairedDevice.setVisibility(View.GONE);
                        inputPane.setVisibility(View.VISIBLE);
                        textStatus.setVisibility(View.GONE);
                        textInfo.setVisibility(View.GONE);

                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
    String str="";
    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    public class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;

        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes)
                            + strReceived;
                    str=msgReceived+"";

                    sayacRe++;

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //wSaat.setText(strReceived);
                            final MsgDb mbdata = new MsgDb(getApplicationContext());
                            HexCevir hxCevir=new HexCevir();
                            Date simdikiZaman = new Date();
                            DateFormat df = new SimpleDateFormat("hh:mm:ss");
                            String simdikizaman;
                            simdikizaman=df.format(simdikiZaman);
                            Date simdikiZamanT = new Date();
                            DateFormat dfT = new SimpleDateFormat("yyyy-MM-dd");
                            String simdikizamanT;
                            simdikizamanT=dfT.format(simdikiZamanT);
                            tarihFarki=new TarihFarki();
                            //int fark=tarihFarki.getNumMountBetween(simdikizamanT,);
                            TextView txtt=(TextView)findViewById(R.id.textView3);
                            txtt.setText(msgReceived+" ");

                            int tuketim=0;
                            String[] ss;
                            ss = msgReceived.split("");
                            runSayac++;


                            if(ss.length==8) {
                                int a1,a2,a3;
                                a1=16*hxCevir.hxCevir(ss[2]);
                                a1+=hxCevir.hxCevir(ss[3]);

                                a2=16*hxCevir.hxCevir(ss[4]);
                                a2+=hxCevir.hxCevir(ss[5]);
                                tuketim=a2;
                                mbdata.insertYillik(1,tuketim);
                                a3=16*hxCevir.hxCevir(ss[6]);
                                a3+=hxCevir.hxCevir(ss[7]);

                                akim.setText(a1+" A");
                                gTuketim.setText(a2+" W");
                                wSaat.setText(a3+" Wh");
                            }else if(ss.length==9) {
                                int a1,a2,a3;
                                a1=16*hxCevir.hxCevir(ss[2]);
                                a1+=hxCevir.hxCevir(ss[3]);

                                a2=16*hxCevir.hxCevir(ss[4]);
                                a2+=hxCevir.hxCevir(ss[5]);

                                a3=256*hxCevir.hxCevir(ss[6]);
                                a3+=16*hxCevir.hxCevir(ss[7]);
                                a3+=hxCevir.hxCevir(ss[8]);
                                tuketim=a2;
                                mbdata.insertYillik(1,tuketim);


                                akim.setText(a1+" A");
                                gTuketim.setText(a2+" W");
                                wSaat.setText(a3+" Wh");
                            }

                            Cursor rs = mbdata.getSaatlik(1);
                            rs.moveToFirst();
                            String cekilenzman = rs.getString(rs.getColumnIndex(mbdata.CONTACTS_COLUMN_Tarih));
                            rs.close();
                            Cursor rs2 = mbdata.getBoyutAlma("gunluk");
                            rs2.moveToFirst();
                            String sss = rs2.getString(rs2.getColumnIndex(mbdata.CONTACTS_COLUMN_Sayac));
                            sayac2=Integer.parseInt(sss);
                            rs2.close();
                            Cursor rs5 = mbdata.getBoyutAlma("gunluk");
                            rs5.moveToFirst();
                            String bolum = rs5.getString(rs5.getColumnIndex(mbdata.CONTACTS_COLUMN_Sayac));
                            rs5.close();
                            int bolum1=Integer.parseInt(bolum);
                            //gTuketim.setText(bolum1);
                            if(bolum1>1) {
                                for(int i=1;i<bolum1;i++){
                                    Cursor rs6 = mbdata.getGunluk(i,"wattSaat");
                                    rs6.moveToFirst();
                                    double degerlerA = rs6.getDouble(rs6.getColumnIndex(mbdata.CONTACTS_COLUMN_WatSaat));
                                    firstData[i]=degerlerA; //gelen wattsaat buraya eklenecek
                                    rs6.close();
                                }


                            }

                            else{
                                Cursor rs7 = mbdata.getBoyutAlma("yillik");
                                rs7.moveToFirst();
                                String anlikS = rs7.getString(rs7.getColumnIndex(mbdata.CONTACTS_COLUMN_Sayac));
                                rs7.close();
                                int bolum2=Integer.parseInt(bolum);
                                for(int i=1;i<bolum1;i++) {
                                    Cursor rs9 = mbdata.getGunluk(i,"wattSaat");
                                    rs9.moveToFirst();
                                    double aaa = rs9.getDouble(rs9.getColumnIndex(mbdata.CONTACTS_COLUMN_WatSaat));
                                    rs9.close();
                                    gelenVeriWaatsaat = aaa;

                                }
                                firstData[0]=gelenVeriWaatsaat;
                            }
                            firstData[0]=tuketim;

                            farkSaat=tarihFarki.getNumDakkaBetween(simdikizaman,cekilenzman);

                            if(farkSaat>15 && sayac2<96){
                                int noBelirleme=sayac2+1;
                                double araSayi=gelenVeriWaatsaat;
                                mbdata.insertGunluk(noBelirleme,araSayi,simdikizaman);
                                mbdata.upDateAnlik(1,simdikizaman);


                            }else if(farkSaat>15 && sayac2==96){
                                int sayac3=0;
                                Cursor rs4 = mbdata.getBoyutAlma("aylik");
                                rs4.moveToFirst();
                                String sayac3_ = rs4.getString(rs4.getColumnIndex(mbdata.CONTACTS_COLUMN_Sayac));
                                rs4.close();
                                sayac3=Integer.parseInt(sayac3_);
                                sayac3++;
                                double veri2=0,veri1=0;
                                for(int i=1;i<24;i++) {
                                    Cursor rs3 = mbdata.getGunluk(i,"wattSaat");
                                    rs3.moveToFirst();
                                    veri1 = rs3.getDouble(rs3.getColumnIndex(mbdata.CONTACTS_COLUMN_WatSaat));
                                    rs3.close();
                                    veri2+=veri1;
                                }
                                mbdata.insertAylik(sayac3,veri2,simdikizamanT);
                                mbdata.deleteTable("gunluk");
                                mbdata.upDateAnlik(1,simdikizaman);
                                sayac2=0;
                                sayac2++;
                                double araSayi=gelenVeriWaatsaat;
                                mbdata.insertGunluk(sayac2,araSayi,simdikizamanT);

                            }


                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            kontrolKart=true;
                            textStatus.setText(msgConnectionLost);

                        }});
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {

                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void getBarChart(){
        XYMultipleSeriesRenderer barChartRenderer = getBarChartRenderer();
        setBarChartSettings(barChartRenderer);
        Intent intent = ChartFactory.getBarChartIntent(this, getBarDemoDataset(), barChartRenderer, BarChart.Type.DEFAULT);
        startActivity(intent);
    }

    private XYMultipleSeriesDataset getBarDemoDataset() {
        XYMultipleSeriesDataset barChartDataset = new XYMultipleSeriesDataset();
        CategorySeries firstSeries = new CategorySeries("AMF Energy Monitoring");
        int sayy=0;
        for(int i=0;firstData[i]!=0.0;i++){
            firstSeries.add(firstData[i]);
            sayy++;
        }
        barChartDataset.addSeries(firstSeries.toXYSeries());

        CategorySeries secondSeries = new CategorySeries("");
        double[] secondData=new double[sayy];
        for(int j = 0; j<secondData.length; j++)
            secondSeries.add(0);
        barChartDataset.addSeries(secondSeries.toXYSeries());
        return barChartDataset;
    }

    public XYMultipleSeriesRenderer getBarChartRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(35);
        renderer.setChartTitleTextSize(25);
        renderer.setLabelsTextSize(25);
        renderer.setLegendTextSize(25);
        renderer.setMargins(new int[] {55, 55, 55, 15});
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(Color.BLUE);
        renderer.addSeriesRenderer(r);
        r = new SimpleSeriesRenderer();
        r.setColor(Color.YELLOW);
        renderer.addSeriesRenderer(r);
        return renderer;
    }
    private void setBarChartSettings(XYMultipleSeriesRenderer renderer) {
        int enBuyuk=2;
        enBuyuk= (int) firstData[0];
        for(int i=0;i<firstData.length;i++){
            if(enBuyuk<firstData[i]){
                enBuyuk= (int) firstData[i];
            }
        }
        renderer.setChartTitle("Enerji Tuketim");
        renderer.setXTitle("Zaman");
        renderer.setYTitle("KW/H");
        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(10.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(enBuyuk);
    }
}
