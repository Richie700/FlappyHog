package pl.cezaryregec.flappyhog;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            // try to download update info
            URL updatefile = new URL(GameEngine.UPDATE_URL);

            // read it
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            updatefile.openStream()));

            // get update info
            int newVersion;
            String updateURL;

            newVersion = Integer.parseInt(in.readLine());
            updateURL = in.readLine();

            // close stream
            in.close();

            // if a new version is available
            if(GameEngine.VERSION_CODE < newVersion) {
                return updateURL;  // return url
            } else {
                return null; // no need to update
            }
        } catch (Exception e) {
            return null; // something went wrong
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        // get updates
        final String url = s;

        // if nothing to update, quit
        if(url == null) return;

        GameEngine.DIALOG_WAITING = true;

        // ask for update
        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(GameEngine.mContext);
        builder.setMessage("An update is available");
        builder.setCancelable(true);

        // update
        builder.setPositiveButton(
                "Get new version",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                        GameEngine.mContext.startActivity(intent);
                    }
                });

        // cancel
        builder.setNegativeButton(
                "Later",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        GameEngine.DIALOG_WAITING = false;
                    }
                });

        // show dialog
        AlertDialog alert = builder.create();
        alert.show();
    }
}
