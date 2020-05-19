package group.j.android.markdownald.db;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class NoteSyncTask extends AsyncTask<String, Integer, Integer> {
    private DownLoadServer downLoadServer = new DownLoadServer();
    private DatabaseHelper mDatabase;
    private static final String TAG = "NoteSyncTask";
    private static final String serverAddress = "101.132.106.166";
    private static final int portNo = 9997;

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_REGISTED = 2;
    private SyncListener listener;

    public NoteSyncTask(SyncListener listener) {
        this.listener = listener;
    }

    public NoteSyncTask(SyncListener listener, DatabaseHelper mDatabase) {
        this.listener = listener;
        this.mDatabase = mDatabase;
    }

    @Override
    protected void onPreExecute() {;
        super.onPreExecute();
        listener.onStart();
    }

    @Override
    protected Integer doInBackground(String... strings) {

        BufferedReader in = null;
        PrintWriter out = null;
        boolean RD = true;
        try {
            Socket socket = new Socket(serverAddress, portNo);
            Log.d(TAG, "doInBackground: Connecting to " + serverAddress + " on port " + portNo);

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter
                        (new OutputStreamWriter(socket.getOutputStream())), true);
                Log.d(TAG, "doInBackground: I/O created");
                out.write(strings[0]);
                out.flush();
                String result = "";
                while (RD) {
                    InputStream input = socket.getInputStream();
                    byte[] ReadBuffer = new byte[2048];
                    int ReadBufferLengh;
                    ReadBufferLengh = input.read(ReadBuffer);
                    if (ReadBufferLengh == -1) {
                        RD = false;
                    }else {
                        result = result + new String(ReadBuffer,0,ReadBufferLengh);
                    }
                }
                Log.d(TAG, result);
                JsonObject js = new JsonParser().parse(result).getAsJsonObject();
                Log.d(TAG, js.toString());
                boolean re = js .get("result").getAsBoolean();
                String loginInfor = js.get("infor").getAsString();
                if(!re)
                    return TYPE_REGISTED;
                if(loginInfor.equals("login successful"))
                    downLoadServer.downloadData(result,mDatabase);
                Thread.sleep(1000);
                return TYPE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (out != null) {
                    out.flush();
                    out.close();
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "doInBackground: socket closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                Log.d(TAG, "onPostExecute: successfully synced");
                break;
            case TYPE_FAILED:
                listener.onFailed();
                Log.d(TAG, "onPostExecute: sync failed");
                break;
            case TYPE_REGISTED:
                listener.onRegistered();
                Log.d(TAG, "onPostExecute: register failed");
                break;
            default:
                break;
        }
    }

    public interface SyncListener {
        void onStart();
        void onSuccess();
        void onFailed();
        void onRegistered();
    }
}
