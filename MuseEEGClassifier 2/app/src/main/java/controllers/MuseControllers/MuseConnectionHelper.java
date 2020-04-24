package controllers.MuseControllers;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import controllers.SVMController.SVM_Helper;

import static java.lang.Thread.sleep;


public class MuseConnectionHelper {

    static boolean MUSE_CONNECTED = false;
    int reconnectCount = 0;

    private final AtomicReference<Handler> fileHandler = new AtomicReference<>();
    private final AtomicReference<MuseFileWriter> fileWriter = new AtomicReference<>();
    private final Handler handler = new Handler();
    public final double[] eegBuffer = new double[6];
    public final double[] alphaBuffer = new double[6];
    public final double[] accelBuffer = new double[3];
    public final double[] hsiBuffer = new double[4];
    String TAG = "MUSE_HELPER";
    private Muse muse;
    private Context context;
    public Long start= null;
    private boolean isGood = true;
    private Double alphaValue = 0d;
    private Double betaValue = 0d;
    private Double deltaValue = 0d;
    private Double gammaValue = 0d;
    private Double thetaValue = 0d;
    private Map<MuseDataPacketType, Double> lastValues = new HashMap<>();
    private Map<MuseDataPacketType, List<Double>> tempBuffer = new HashMap<>();
    public static final int TEMP_BUFFER_MAX = 1;
    //private MoodClassifier moodClassifier = new MoodClassifier(this);

    private TextView tv_hsi_1;
    private TextView tv_hsi_2;
    private TextView tv_hsi_3;
    private TextView tv_hsi_4;



    public Runnable updateGUI = new Runnable() {
        @Override
        public void run() {

            tv_hsi_1.setText("" + eegBuffer[0]);
            tv_hsi_2.setText("" + eegBuffer[1]);
            tv_hsi_3.setText("" + eegBuffer[2]);
            tv_hsi_4.setText("" + eegBuffer[3]);
            handler.postDelayed(updateGUI, 500);
        }
    };
    private TextView tv_muse_status;
    private DataListener dataListener; // Receive packets from connected band
    private ConnectionListener connectionListener; //Headband connection Status
    private String name = "RealTimeEEGClassifier";
    private String muse_status;
    private SVM_Helper sh;

    public MuseConnectionHelper(SVM_Helper sh) {

        this.sh = sh;
        WeakReference<MuseConnectionHelper> weakActivity =
                new WeakReference<MuseConnectionHelper>(this);
        connectionListener = new ConnectionListener(weakActivity); //Status of Muse Headband
        dataListener = new DataListener(weakActivity); //Get data from EEG

    }

    public void setTv_muse_status(TextView tv_muse_status) {
        this.tv_muse_status = tv_muse_status;
    }

    public void setHSITextView(TextView hsi1, TextView hsi2, TextView hsi3, TextView hsi4) {
        this.tv_hsi_1 = hsi1;
        this.tv_hsi_2 = hsi2;
        this.tv_hsi_3 = hsi3;
        this.tv_hsi_4 = hsi4;

    }

    public void setMuse(Muse muse) {
        this.muse = muse;
    }

    public final void connect_to_muse() {
        muse.unregisterAllListeners();
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_ABSOLUTE);
        muse.registerDataListener(dataListener, MuseDataPacketType.BETA_ABSOLUTE);
        muse.registerDataListener(dataListener, MuseDataPacketType.DELTA_ABSOLUTE);
        muse.registerDataListener(dataListener, MuseDataPacketType.GAMMA_ABSOLUTE);
        muse.registerDataListener(dataListener, MuseDataPacketType.THETA_ABSOLUTE);
        muse.runAsynchronously();
    }

    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();
        // Format a message to show the change of connection state in the UI.
        muse_status = current.toString();
        Log.i(TAG, "Muse Connection Status: " + muse_status);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(tv_muse_status != null)
                    tv_muse_status.setText(muse_status);
            }
        });
        if (current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            MUSE_CONNECTED = false;
            //Retry connection of muse for 5 times
            if (muse != null && reconnectCount < 5) {
                reconnectCount++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (muse != null)
                            connect_to_muse();
                    }
                }, 500);
            }
        }
        else if (current == ConnectionState.CONNECTED) {
            reconnectCount = 0; //reset the count
            MUSE_CONNECTED = true;

        }
    }

    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) throws IOException {
        final long n = p.valuesSize();
        if (start == null) {
        start = System.currentTimeMillis();}
        switch (p.packetType()) {
            /*case EEG:
                assert (eegBuffer.length >= n);
                getEegChannelValues(eegBuffer, p, start);
                sh.receiveEEGPacket(eegBuffer);
                break;*/
            case ALPHA_ABSOLUTE:
                if (isGood)
                    alphaValue = getChannelValue1(p);
                collectSignal(alphaValue, p.packetType());
                break;
            case BETA_ABSOLUTE:
                if (isGood)
                    betaValue = getChannelValue1(p);
                collectSignal(betaValue, p.packetType());
                break;
            case DELTA_ABSOLUTE:
                if (isGood)
                    deltaValue = getChannelValue1(p);
                collectSignal(deltaValue, p.packetType());
                break;
            case GAMMA_ABSOLUTE:
                if (isGood)
                    gammaValue = getChannelValue1(p);
                collectSignal(gammaValue, p.packetType());
                break;
            case THETA_ABSOLUTE:
                if (isGood)
                    thetaValue = getChannelValue1(p);
                collectSignal(thetaValue, p.packetType());
                break;
        }
    }



    private void getEegChannelValues(double[] buffer, MuseDataPacket p, long start) {
        final double eegScalar = 841.4075;
        buffer[0] = p.getEegChannelValue(Eeg.EEG1)-eegScalar;
        buffer[1] = p.getEegChannelValue(Eeg.EEG2)-eegScalar;
        buffer[2] = p.getEegChannelValue(Eeg.EEG3)-eegScalar;
        buffer[3] = p.getEegChannelValue(Eeg.EEG4)-eegScalar;
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT)-eegScalar;
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT)-eegScalar;
       /* try {
           // savetocsv( buffer,start );
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<MuseConnectionHelper> activityRef;

        ConnectionListener(final WeakReference<MuseConnectionHelper> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    class DataListener extends MuseDataListener {
        final WeakReference<MuseConnectionHelper> activityRef;

        DataListener(final WeakReference<MuseConnectionHelper> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            try {
                activityRef.get().receiveMuseDataPacket(p, muse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
        }
    }

    public void stoplistening(){
        muse.unregisterAllListeners();
    }

    private void savetocsv(String[] doubles, long start) throws IOException {
        long end = System.currentTimeMillis();
        String file ="emotion.csv";
        float sec = (end - start) / 1000F; System.out.println(sec + " seconds");
        if(sec<1) {
            File a = new File( Environment.getExternalStorageDirectory().getAbsolutePath().toString(), file );
            Log.d( TAG, "Ash Loc: " + Environment.getExternalStorageDirectory().getAbsolutePath().toString() );
            FileWriter writer = new FileWriter( new File( Environment.getExternalStorageDirectory().getAbsolutePath().toString(), file ), true );
            if (a.length() == 0) {
                String header = "time,alpha,beta,gamma,theta,emotion\n";
                writer.append( header );
            }
            String entry = String.valueOf(sec) + "," + doubles[0] + "," + doubles[1] + "," + doubles[2] + "," + doubles[3] + "\n";
            Log.d( TAG, "Values I needed:" + doubles[0] + "," + doubles[1] + "," + doubles[2] + "," + doubles[3] + "\n" );
            writer.append( entry );
            writer.close();
        }
        else
            {
                Log.d( TAG, "1 sec complete:"+  "\n" );
            }
    }

    @Nullable
    private Double getChannelValue1(MuseDataPacket p) {
        return (p.getEegChannelValue(Eeg.EEG1) + p.getEegChannelValue(Eeg.EEG2) + p.getEegChannelValue(Eeg.EEG3) + p.getEegChannelValue(Eeg.EEG4)) / 4;
    }

    public void collectSignal(Double buffer, MuseDataPacketType type) {
        List<Double> bufferList = tempBuffer.get(type);
        if (bufferList == null) {
            tempBuffer.put(type, new ArrayList<Double>());
            bufferList = tempBuffer.get(type);
        }
        int size = bufferList.size();
        if (size > TEMP_BUFFER_MAX) {
            Double avg = 0d;
            for (Double currentVal :
                    bufferList) {
                avg += currentVal;
            }
            bufferList.clear();
            avg /= 4;
            lastValues.put(type, avg);

            if (lastValues.containsKey(MuseDataPacketType.ALPHA_ABSOLUTE) &&
                    lastValues.containsKey(MuseDataPacketType.BETA_ABSOLUTE) &&
                    lastValues.containsKey(MuseDataPacketType.GAMMA_ABSOLUTE) &&
                    lastValues.containsKey(MuseDataPacketType.THETA_ABSOLUTE) &&
                    lastValues.containsKey(MuseDataPacketType.DELTA_ABSOLUTE)
            ) {
               Log.d(TAG,"ashwin123: " + String.valueOf(lastValues));
               // String[] values = lastValues.values().toArray(new String[0]);
               //savetocsv( values, start);

                lastValues.clear();
            }
        }
        bufferList.add(buffer);
    }
}
