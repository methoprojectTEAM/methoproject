package com.example.niephox.methophotos.ViewControllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.example.niephox.methophotos.Activities.AccountManageActivity;
import com.example.niephox.methophotos.R;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private View rootView ;
    private Context context;

    public NavigationItemListener(DrawerLayout drawerLayout, Context context  ) {
        this.drawerLayout = drawerLayout;
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.album:
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            case R.id.metadata:
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            case R.id.settings:
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            case R.id.manageAcc:
                item.setChecked(true);
                Intent intent = new Intent(context, AccountManageActivity.class);
                context.startActivity(intent);
                drawerLayout.closeDrawers();
                return true;
            case R.id.userLogout:
                item.setChecked(true);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                //auth.addAuthStateListener(authListener);

                auth.signOut();
                drawerLayout.closeDrawers();
                return true;
            case R.id.about:
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            case R.id.feedback:
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            case R.id.version:
                item.setChecked(true);
                drawerLayout.closeDrawers();
            default:
                return false;
        }
    }

}
