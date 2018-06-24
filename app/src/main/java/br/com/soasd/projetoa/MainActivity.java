package br.com.soasd.projetoa;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Fragments.BaseFragment;
import Fragments.Doacoes;
import Fragments.Home;
import Fragments.MeusItens;
import Fragments.Settings;
import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.exception.MedicamentosDisponiveisException;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import service.MyFirebaseInstanceIDService;
import utils.FragmentHistory;
import utils.Utils;
import views.FragNavController;


public class MainActivity extends BaseActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener  {

    @BindView(br.com.soasd.projetoa.R.id.content_frame)
    FrameLayout contentFrame;

    @BindView(br.com.soasd.projetoa.R.id.toolbar)
    Toolbar toolbar;

    @BindView(br.com.soasd.projetoa.R.id.bottom_tab_layout)
    TabLayout bottomTabLayout;

    @BindArray(br.com.soasd.projetoa.R.array.tab_name)
    String[] TABS;

    private static int TIPO_ACESSO;
    private String FBID, EMAIL;
    private int[] mTabIconsSelected = {
            br.com.soasd.projetoa.R.drawable.tab_home,
            br.com.soasd.projetoa.R.drawable.tab_doacoes,
            br.com.soasd.projetoa.R.drawable.tab_meus_itens,
            br.com.soasd.projetoa.R.drawable.tab_settings
            };


    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.soasd.projetoa.R.layout.activity_main);
        ButterKnife.bind(this);
        new MyFirebaseInstanceIDService().onTokenRefresh();

        initToolbar();
        initTab();
        fragmentHistory = new FragmentHistory();

        Bundle extras = getIntent().getExtras();

        TIPO_ACESSO = extras.getInt("TIPO_ACESSO");
        EMAIL = extras.getString("EMAIL");

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), br.com.soasd.projetoa.R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();


        switchTab(0);

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fragmentHistory.push(tab.getPosition());

                switchTab(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                mNavController.clearStack();

                switchTab(tab.getPosition());


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

    }

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(br.com.soasd.projetoa.R.layout.tab_item_bottom, null);
        ImageView icon = view.findViewById(br.com.soasd.projetoa.R.id.tab_icon);
        icon.setImageDrawable(Utils.setDrawableSelector(MainActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));

        return view;
    }

    public void updateToolbarTitle(String title) {


        getSupportActionBar().setTitle(title);

    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {


            updateToolbar();

        }
    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            updateToolbar();

        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        Bundle data;
        switch (index) {
            case FragNavController.TAB1:
                data = new Bundle();
                data.putInt("TIPO_ACESSO", TIPO_ACESSO);
                data.putString("EMAIL", EMAIL);
                Home h = new Home();
                h.setArguments(data);
                return h;
            case FragNavController.TAB2:
                data = new Bundle();
                data.putInt("TIPO_ACESSO", TIPO_ACESSO);
                data.putString("EMAIL", EMAIL);
                Doacoes d = new Doacoes();
                d.setArguments(data);
                return d;
            case FragNavController.TAB3:
                data = new Bundle();
                data.putInt("TIPO_ACESSO", TIPO_ACESSO);
                data.putString("EMAIL", EMAIL);
                MeusItens m = new MeusItens();
                m.setArguments(data);
                return m;
            case FragNavController.TAB4:
                data = new Bundle();
                data.putInt("TIPO_ACESSO", TIPO_ACESSO);
                data.putString("EMAIL", EMAIL);
                Settings s = new Settings();
                s.setArguments(data);
                return s;
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(br.com.soasd.projetoa.R.drawable.ic_arrow_back);
    }

    private void switchTab(int position) {
        mNavController.switchTab(position);


//        updateToolbarTitle(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:


                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }

    private void updateTabSelection(int currentTab){

        for (int i = 0; i <  TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if(currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            }else{
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {

            if (fragmentHistory.isEmpty()) {
                //super.onBackPressed();
            } else {


                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();

                    switchTab(position);

                    updateTabSelection(position);

                } else {

                    switchTab(0);

                    updateTabSelection(0);

                    fragmentHistory.emptyStack();
                }
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }



}
