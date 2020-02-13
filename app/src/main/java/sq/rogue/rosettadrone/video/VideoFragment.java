package sq.rogue.rosettadrone.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.sdkmanager.LiveStreamManager;
import dji.ux.widget.FPVWidget;
import sq.rogue.rosettadrone.R;
import sq.rogue.rosettadrone.video.VideoFeedView;

public class VideoFragment extends Fragment {


    private FPVWidget fpvWidget;
    private BottomNavigationView bottomNavigationView;
    private AppBarLayout appBarLayout;
    private int appbarheight = 0;
    //    final Handler handler = new Handler();
    private final String TAG = getClass().getSimpleName();
    //    private final String INSTANCE_STATE_KEY = "saved_state";
    private final int DEFAULT_MAX_CHARACTERS = 200000;
    //    GestureDetector gestureDetector;
    private TextView mTextViewTraffic;
    private ScrollView mScrollView;
    //    Runnable mLongPressed = new Runnable() {
//        public void run() {
//            clearLogText();
//        }
//    };
    private boolean mViewAtBottom = true;
    private int mMaxCharacters = DEFAULT_MAX_CHARACTERS;
    private int LONG_PRESS_TIMEOUT = 3000;
    private boolean isFullscreen = false;




    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG, "onCreateView");
            super.onCreateView(inflater, container, savedInstanceState);
            this.setRetainInstance(true);

            View main_view = inflater.inflate(R.layout.activity_main, container, false);
            bottomNavigationView = main_view.findViewById(R.id.navigationView);
            appBarLayout = main_view.findViewById(R.id.app_bar_layout);
            View view = inflater.inflate(R.layout.fragment_video, container, false);

            fpvWidget = view.findViewById(R.id.fpv_widget);
            fpvWidget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewClick(fpvWidget);
                }
            });

        return view;
    }

    private void onViewClick(View view) {
        if (!isFullscreen) {

            appBarLayout.setVisibility(View.GONE);
            appBarLayout.setExpanded(false, true);

//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
//            this.appbarheight = lp.height;
//            lp.height = 0;
//            appBarLayout.setLayoutParams(lp);

            bottomNavigationView.setVisibility(View.GONE);

            setFullscreen();
            registerSystemUiVisibility();
            isFullscreen = true;
        } else if (isFullscreen) {

            unregisterSystemUiVisibility();
            exitFullscreen(getActivity());
            appBarLayout.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(true, true);

//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
//            lp.height = this.appbarheight;
//            appBarLayout.setLayoutParams(lp);

            bottomNavigationView.setVisibility(View.VISIBLE);
            isFullscreen = false;
        }
    }


    /**
     * Set the backing TextView
     *
     * @param textView The new backing TextView
     */
    public void setTextView(TextView textView) {
        mTextViewTraffic = textView;
    }

    /**
     * @param savedInstanceState Any saved state we are carrying over into the new activity instance
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        this.setRetainInstance(true);


    }

    /**
     * Checks the length of log and compares it against the maximum number of characters permitted.
     * If the log is longer than the maximum number of characters the log is cleared.
     *
     * @return True if the log is cleared. False if the log is not.
     */
    // TODO: Implement a better solution to overflow control
    public boolean checkOverflow() {
        /*
        Very naive solution. Writing out to a log is possible solution if log needs preserved,
        however parsing with substring will have a very severe impact on performance
         */
//        if (mTextViewTraffic.getText().length() > DEFAULT_MAX_CHARACTERS) {
//            clearLogText();
//            return true;
//        }
        return false;
    }

    /**
     * Verifies that the log can hold more text, then appends the text to the log and if enabled scrolls
     * to the bottom of the log.
     *
     * @param text The text to append to the log.
     */
    public void appendLogText(String text) {
        checkOverflow();

        mTextViewTraffic.append(text);

        scrollToBottom();
    }

    /**
     * Calculates the difference between the top of the TextView and the height of the TextView then
     * scrolls to the difference.
     */
    public void scrollToBottom() {
        if (mTextViewTraffic != null && mTextViewTraffic.getLayout() != null) {
            final int scrollAmt = mTextViewTraffic.getLayout().getLineTop(mTextViewTraffic.getLineCount())
                    - mTextViewTraffic.getHeight();
            if (scrollAmt > 0 && scrollAmt < 1200) {
                mTextViewTraffic.scrollTo(0, scrollAmt);
            }
        }

//        else {
//            mTextViewTraffic.scrollTo(0, 0);
//        }

//        Log.d("TEST", String.valueOf(scrollAmt));
    }

    /**
     * Clears all text out of the TextView.
     */
    public void clearLogText() {
        mTextViewTraffic.setText("");
    }

    /**
     * Retrieves the text from the underlying TextView.
     *
     * @return String representation of the log.
     */
    public String getLogText() {
        if (mTextViewTraffic != null)
            return mTextViewTraffic.getText().toString();
        return null;
    }

    /**
     * Helper method to set the underlying TextView text. Will overwrite all text currently in the log.
     *
     * @param text Text to set.
     */
    public void setLogText(String text) {
        mTextViewTraffic.setText(text);
    }

    /**
     * Gets the maximum number of characters the log can hold.
     *
     * @return The maximum number of characters the log can hold.
     */
    public int getMaxCharacters() {
        return mMaxCharacters;
    }

    /**
     * Sets the maximum number of characters the log can hold.
     *
     * @param maxCharacters The new maximum number of characters the log can hold.
     */
    public void setMaxCharacters(int maxCharacters) {
        mMaxCharacters = maxCharacters;
    }


    public static boolean isImmersiveAvailable() {
        return android.os.Build.VERSION.SDK_INT >= 19;
    }

    public void setFullscreen() {
        setFullscreen(getActivity());
    }

    public void setFullscreen(Activity activity) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (isImmersiveAvailable()) {
                flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    public void exitFullscreen(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    private Handler _handler = new Handler();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void registerSystemUiVisibility() {
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    setFullscreen();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void unregisterSystemUiVisibility() {
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(null);
    }
}
