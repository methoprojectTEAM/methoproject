package com.example.niephox.methophotos.ViewControllers.ViewAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.niephox.methophotos.R;
import com.example.niephox.methophotos.ViewControllers.GridSpacingItemDecoration;
import com.example.niephox.methophotos.ViewControllers.NavigationItemListener;

public class AlbumsViewActivityView {
	private final DrawerLayout mdrawerLayout;
	private final RecyclerView recyclerView;
	private final Toolbar toolbar;
	private final ActionBar actionBar;
	private final FloatingActionButton floatingActionButton;
	private final NavigationView navigationView;
	private final CollapsingToolbarLayout collapsingToolbar;
	private final AppBarLayout appBarLayout;
	private final View createAlbumView;
	private final EditText albumNameEditText;
	private final EditText albumDescriptionEditText;
	private final Button createAlbumButton;
	private final AlertDialog.Builder diaBuilder;
	private final AlertDialog dialog;

	public AlbumsViewActivityView(final AppCompatActivity activity){
		activity.setContentView(R.layout.activity_album);
		mdrawerLayout = activity.findViewById(R.id.drawer_layout);
		recyclerView = activity.findViewById(R.id.recycler_view);
		toolbar = activity.findViewById(R.id.toolbar);
		activity.setSupportActionBar(toolbar);
		actionBar = activity.getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
		floatingActionButton = activity.findViewById(R.id.addAlbum);
		navigationView = activity.findViewById(R.id.navigation);
		collapsingToolbar = activity.findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(" ");
		appBarLayout = activity.findViewById(R.id.appbar);
		createAlbumView = activity.getLayoutInflater().inflate(R.layout.layout_create_album, null);
		albumNameEditText = createAlbumView.findViewById(R.id.albumName);
		albumDescriptionEditText = createAlbumView.findViewById(R.id.albumDescription);
		createAlbumButton = createAlbumView.findViewById(R.id.createAlbumButton);
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initCollapsingToolbar(activity);
		RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity, 1);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10,activity), true));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		floatingActionButton.setOnClickListener((View.OnClickListener) activity);
		navigationView.setNavigationItemSelectedListener(new NavigationItemListener(mdrawerLayout, activity));

		diaBuilder = new AlertDialog.Builder(activity);
		diaBuilder.setView(createAlbumView);
		dialog = diaBuilder.create();


	}
	private void initCollapsingToolbar(final AppCompatActivity activity) {
		appBarLayout.setExpanded(true);
		// hiding & showing the title when toolbar expanded & collapsed
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			boolean isShow = false;
			int scrollRange = -1;

			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				if (scrollRange == -1) {
					scrollRange = appBarLayout.getTotalScrollRange();
				}
				if (scrollRange + verticalOffset == 0) {
					collapsingToolbar.setTitle(activity.getString(R.string.app_name));
					isShow = true;
				} else if (isShow) {
					collapsingToolbar.setTitle(" ");
					isShow = false;
				}
			}
		});
	}

	private int dpToPx(int dp,AppCompatActivity activity) {
		Resources r = activity.getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}

	public AlertDialog getDialog() {
		return dialog;
	}

	public DrawerLayout getMdrawerLayout() {
		return mdrawerLayout;
	}

	public RecyclerView getRecyclerView() {
		return recyclerView;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public FloatingActionButton getFloatingActionButton() {
		return floatingActionButton;
	}

	public NavigationView getNavigationView() {
		return navigationView;
	}

	public CollapsingToolbarLayout getCollapsingToolbar() {
		return collapsingToolbar;
	}

	public AppBarLayout getAppBarLayout() {
		return appBarLayout;
	}

	public View getCreateAlbumView() {
		return createAlbumView;
	}

	public EditText getAlbumNameEditText() {
		return albumNameEditText;
	}

	public EditText getAlbumDescriptionEditText() {
		return albumDescriptionEditText;
	}

	public Button getCreateAlbumButton() {
		return createAlbumButton;
	}
}
