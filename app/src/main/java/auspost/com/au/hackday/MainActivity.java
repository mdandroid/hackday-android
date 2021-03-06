package auspost.com.au.hackday;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import auspost.com.au.hackday.model.User;
import auspost.com.au.hackday.persistence.DatabaseManager;
import auspost.com.au.hackday.utils.UserUtils;

import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    public static final String HOLD_MAIL = "Hold mail";
    public static final String APPLY_FOR_A_BANK_ACCOUNT = "Apply for a bank account";
    public static final String APPLY_FOR_A_PASSPORT = "Apply for a passport";
    public static final String POSTAL_VOTE = "Postal vote";
    public static final String UPDATE_LAND_TITLE = "Update land title";
    private ServicesListAdapter formsListAdapter;

    private ListView formListView;
    private ImageButton profilePageButton;
    private TextView profilePer;
    private DatabaseManager databaseManager;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getIntent().getParcelableExtra("user");
        setContentView(R.layout.activity_main);
        formsListAdapter = new ServicesListAdapter(this.getApplicationContext(), user);
        formListView = (ListView) findViewById(R.id.list_forms);
        formListView.setAdapter(formsListAdapter);
        profilePageButton();

        databaseManager = new DatabaseManager(this);

        if (user.changeAddress.equalsIgnoreCase("true")) {
            formsListAdapter.update("Change of address", "Extra Info", true);
        } else {
            formsListAdapter.update("Change of address", "Extra Info", false);
        }
        formsListAdapter.update(HOLD_MAIL, "Extra Info", false);
        formsListAdapter.update(APPLY_FOR_A_BANK_ACCOUNT, "Drivers License", false);
        formsListAdapter.update(APPLY_FOR_A_PASSPORT, "Drivers License", false);
        formsListAdapter.update(POSTAL_VOTE, "Green", true);
    }

    private void verificationPercentage() {
        profilePer = (TextView) findViewById(R.id.verifiedPer);
        profilePer.setText(user.verificationPercentage + "%");
    }

    @Override
    public void onResume() {
        super.onResume();
        user = getIntent().getParcelableExtra("user");
        if (databaseManager.getVerificationPercentage(user.email) != null) {
            user.verificationPercentage = databaseManager.getVerificationPercentage(user.email);
        }
        if (databaseManager.getChangeAddress(user.email) != null) {
            user.changeAddress = databaseManager.getChangeAddress(user.email);
        }
        verificationPercentage();
/*        if (user.changeAddress.equalsIgnoreCase("true")) {
            formsListAdapter.update("COAN", "Green", true);
        } else {
            formsListAdapter.update("COAN", "Amber", false);
        }*/
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

    private void profilePageButton() {
        profilePageButton = (ImageButton) findViewById(R.id.profile_image);
        new DownloadImageTask(profilePageButton)
                .execute("http://wp.patheos.com.s3.amazonaws.com/blogs/tinseltalk/files/2012/05/280px-Tony_Stark_Avengers-150x150.png");
        profilePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
