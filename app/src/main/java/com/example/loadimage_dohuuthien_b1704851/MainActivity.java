package com.example.loadimage_dohuuthien_b1704851;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String URL = "http://10.0.2.2:80/Buoi6/";
        //Thực thi tải dữ liệu hình ảnh
        new DownloadImageTask().execute(URL+"meomeo.jpg");
        //Thực thi tải dữ liệu văn bản
        new DownloadTextTask().execute(URL + "Thuhttpget.txt");
    }

    public InputStream OpenHttpConnection(String urlString) {
        InputStream inputStream = null;
        int response = -1;
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            if (connection instanceof HttpURLConnection) {

                HttpURLConnection httpCon = (HttpURLConnection) connection;
                httpCon.setInstanceFollowRedirects(true);
                httpCon.setRequestMethod("GET");
                httpCon.connect();

                response = httpCon.getResponseCode();
                Log.w("Response Code", "" + response);

                if (response == HttpURLConnection.HTTP_OK) {
                    inputStream = httpCon.getInputStream();
                }
            }
        } catch (IOException e) {
            Log.w("Response Code", "" + e.getMessage());
        }
        return inputStream;
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            // Mở kết nối đến Server, phương thức đã được định nghĩa ở trên
            in = OpenHttpConnection(URL);
            if (in == null) {
                Log.e("Image URL", "Check connection or URL again!");
                return bitmap;
            }
            // Tải dữ liệu thông qua InputStream in
            // và giải mã vào đối tượng bitmap
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            Log.e("NetworkingActivity", e.getLocalizedMessage());
        }
        return bitmap;
    }
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        //Thực hiện tải dữ liệu
        // Khi hoàn tất, kết quả được truyền vào phương thức onPostExecute
        @Override
        protected Bitmap doInBackground(String... urls) {
            return DownloadImage(urls[0]);
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.imv);
            //Hiển thị ảnh trên màn hình
            img.setImageBitmap(result);
            TextView tv = (TextView) findViewById(R.id.tvURLimg);
            if (result != null)
                tv.setText("Got image.");
            else
                tv.setText("Can't get image.");
        }
    }
    private String DownloadText(String URL) {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        String str = "";
        in = OpenHttpConnection(URL);
        if (in == null){
            Log.e("Text URL", "Check connection or URL again!");
            return str;
        }
        try {
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char[] inputBuffer = new char[BUFFER_SIZE];
            while ((charRead = isr.read(inputBuffer)) > 0) {
                //Chuyển chars thành String
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.e("Networking", e.getLocalizedMessage());
            return str;
        }
        return str;
    }
    public class DownloadTextTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return DownloadText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            TextView tv1 = (TextView) findViewById(R.id.tv);
            tv1.setText(result);
            TextView tv = (TextView) findViewById(R.id.tvURLtext);
            if (!result.trim().equals(""))
                tv.setText("Got text.");
            else
                tv.setText("Can't get text.");
        }
    }


}