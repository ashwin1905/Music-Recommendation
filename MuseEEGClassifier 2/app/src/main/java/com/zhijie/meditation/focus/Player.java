package com.zhijie.meditation.focus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Player extends Activity {
    public static final String TAG = "Player";
    private String emotion="Yes";
    private ArrayList<Music> arrayList;
    private ArrayList<Music> arrayList1;
    private CustomMusicAdapter adapter;
    private ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_player );

        int mood = 0;
        String file1 = "predicted_emotion.csv";
        //String path = (Environment.getExternalStorageDirectory().getAbsolutePath().toString()+ file1) ;
        String[] abc = new String[15];
        String emotion = read(file1);
        if(emotion!=null){
        Log.d("TAG","ashwin"+emotion);}

        songList = (ListView) findViewById( R.id.songList);
        arrayList = new ArrayList<>();

        if(emotion.equals("Positive")){
            abc = pos(emotion);
            Log.d(TAG, "Reached Positive");
            arrayList.add( new Music( "Overburdened", R.raw.overburdened) );
            arrayList.add( new Music( "FuriousRose", R.raw.furiousrose) );
            arrayList.add( new Music( "HombrealAgua", 0)  );
            arrayList.add( new Music( "Teenager", 0)  );
            arrayList.add( new Music( "DogDaysAreOver", 0)  );
            arrayList.add( new Music( "Secrets", 0)  );
            arrayList.add( new Music( "YouretheOne", 0)  );
            arrayList.add( new Music( "CartolaTivesim", 0)  );
            arrayList.add( new Music( "DrakeTakeCare", 0)  );
            arrayList.add( new Music( "Fireflies", 0)  );
        }
        if(emotion.equals("Negative")){
            abc = pos(emotion);
            Log.d(TAG, "Reached Positive");
            arrayList.add( new Music( "CityOnOurKnees", R.raw.cityonourknees) );
            arrayList.add( new Music( "DropTheWorld", R.raw.droptheworld) );
            arrayList.add( new Music( "HombrealAgua", 0)  );
            arrayList.add( new Music( "Teenager", 0)  );
            arrayList.add( new Music( "DogDaysAreOver", 0)  );
            arrayList.add( new Music( "Secrets", 0)  );
            arrayList.add( new Music( "YouretheOne", 0)  );
            arrayList.add( new Music( "CartolaTivesim", 0)  );
            arrayList.add( new Music( "DrakeTakeCare", 0)  );
            arrayList.add( new Music( "Fireflies", 0)  );
        }
        if(emotion.equals("Neutral")){
            abc = pos(emotion);
            Log.d(TAG, "Reached Neutral");
            arrayList.add( new Music( "ElTiempoLoDira", R.raw.eltiempolodiraanthonyducapo) );
            arrayList.add( new Music( "LauvCanada", R.raw.lauvcanada) );
            arrayList.add( new Music( "MarisaMonteProibido", 0) );
            arrayList.add( new Music( "OMG", 0)  );
            arrayList.add( new Music( "TakiRari", 0)  );
            arrayList.add( new Music( "Undo", 0)  );
            arrayList.add( new Music( "UsherOMG", 0)  );
        }

        adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, arrayList);
        songList.setAdapter(adapter);




    }

    public String read(String csvFile) {
        String line = "No";

        StringBuilder sb= new StringBuilder();
        String abc = null;
        try{
            Log.d(TAG,"reached in 1");
            File textfile=new File( Environment.getExternalStorageDirectory().getAbsolutePath().toString(),csvFile);
            FileInputStream fis=new FileInputStream(textfile);
            if(fis!=null){
                InputStreamReader isr = new InputStreamReader( fis );
                BufferedReader buff= new BufferedReader( isr );
                while((line = buff.readLine())!=null){
                    Log.d(TAG,line);
                    abc = line;
                    sb.append(line);
                    Log.d(TAG,line);
                }
                fis.close();
            }
            return abc;
        }
        catch(IOException e){
            line="No";
        }
        return abc;
    }

    public String[] pos(String emo) {
        String line = "No";
        String[] a= new String[15];
        int i=0;
        String csvFile1 = "predicted_genres.csv";
        try {
            File textfile = new File( Environment.getExternalStorageDirectory().getAbsolutePath().toString(), csvFile1 );
            FileInputStream fis = new FileInputStream( textfile );
            if (fis != null) {
                InputStreamReader isr = new InputStreamReader( fis );
                BufferedReader buff = new BufferedReader( isr );
                while ((line = buff.readLine()) != null) {
                    String[] fields = line.split(",");
                    String name = fields[0];
                    String genre = fields[1];
                    String emotion = fields[2];
                    if (emotion.equals(emo)){
                        Log.d(TAG,name);
                        a[i]=name;
                        i=i+0;
                    }
                }
            }
            return a;
        }catch (IOException e){
            Log.d(TAG,"could not predict");
        }
        return a;
    }


}
