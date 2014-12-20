package m1.nayak.m1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseAnalytics;
import com.parse.ParseException;

import java.util.ArrayList;

import m1.nayak.m1.backend.Query;


public class MainActivity extends ActionBarActivity implements QuizConfigureFragment.OnFragmentInteractionListener, QuizChoiceFragment.OnFragmentInteractionListener, QuizQuestionFragment.OnFragmentInteractionListener {

    public String fragmentLevel;
    public boolean fragmentMC;

    // Get questions
    ProgressDialog dialog;
    ArrayList<String> chosenClasses;
    ArrayList<String> chosenCategories;
    boolean smartQuiz;

    // quiz navigation
    int curr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        if (Control.classes.size() == 0)
            new LoadClasses().execute();
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

    public void displayView(int position, boolean forward) {

        Fragment fragment = null;

        switch (position) {
            // Quiz settings
            case 0:
                fragment = new QuizConfigureFragment();
                break;

            case 1:
                fragment = QuizQuestionFragment.newInstance(false, curr, Control.questions.size());
                break;
            case 2:
                fragmentLevel = "main";
                fragmentMC = false;
                break;
            // Choose Classes
            case 3:
                fragmentLevel = "class";
                fragmentMC = true;
                break;
            default:
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (forward)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        else
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);

        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        fragmentTransaction.addToBackStack(null);
    }

    @Override
    public void onChoiceSelected(int position) {

        if (fragmentLevel.equals("main")) {
            switch (position) {
                // General Knowledge
                case 0:
//                    displayView(1);
                    break;
                // Diseases
                case 1:
                    break;
                // Enzymes
                case 2:
                    break;
                // Drugs
                case 3:
                    break;
                // Hormones
                case 4:
                    break;
            }

            Fragment fragment = new QuizQuestionFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentTransaction.replace(R.id.container, fragment);
//        fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onQuizStarted(ArrayList<String> categories, ArrayList<String> classes, boolean smart) {
        Control.questions.clear();

        Log.v("POOP", "Chosen classes:");
        for (String s : classes) {
            Log.v("POOP", s);
        }
        Log.v("POOP", "Chosen categories:");
        for (String s : categories) {
            Log.v("POOP", s);
        }
        Log.v("POOP", "Smart quiz = " + smartQuiz);

        chosenClasses = classes;
        smartQuiz = smart;
        chosenCategories = categories;

        dialog = new ProgressDialog(this);
        new GetQuestions().execute();
    }

    @Override
    public void onNextPressed(int c, boolean lastQuestion) {

        if(!lastQuestion) {
            curr++;
            displayView(1, true);
        } else {
            // open quiz results page
        }
    }

    @Override
    public void onPrevPressed(int c) {
        curr--;
        displayView(1, false);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    // Button listeners
    public void quizMe(View view) {
        displayView(0, true);
    }

    class GetQuestions extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Creating quiz ...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                Query.getData(chosenClasses, chosenCategories, smartQuiz);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
//                    if (Control.questions == null) {
//                        Toast.makeText(getActivity(), "Error with query", Toast.LENGTH_LONG).show();
//                    } else if (Control.questions.size() == 0) {
//                        Toast.makeText(getActivity(), "No questions match criteria!", Toast.LENGTH_LONG).show();
//                    } else {
//                        Intent i = new Intent(getActivity(), ReviewActivity.class);
//                        i.putExtra("numQuestions", Control.questions.size());
//                        startActivity(i);
//                    }
                    curr = 0;
                    displayView(1, true);
                }
            });
        }
    }

    class LoadClasses extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            Query.loadClasses();
            return null;
        }
    }
}